import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import java.util.*;
import static java.lang.System.*;

public class Connection 
{
  private Edge masterEdge, slaveEdge;
  private boolean isSelected;

  public Connection()
  {
    this.isSelected = false;
  }

  public Connection(Edge masterEdge, Edge slaveEdge)
  {
    this.masterEdge = masterEdge;
    this.slaveEdge = slaveEdge;
    this.isSelected = false;
  }

  public Edge getMasterEdge()
  {
    return this.masterEdge;
  }

  public Edge getSlaveEdge()
  {
    return this.slaveEdge;
  }

  public void setMasterEdge(Edge e)
  {
    this.masterEdge = e;
  }

  public void setSlaveEdge(Edge e)
  {
    this.slaveEdge = e;
  }

  public void drawConnection(PGraphics p)
  {
    Vec2D mid1 = this.getMasterEdge().getMid().add(getMasterEdge().getShape().getPosition2D());
    Vec2D mid2 = this.getSlaveEdge().getMid().add(getSlaveEdge().getShape().getPosition2D());
    if (this.isSelected)
    {
      p.stroke(255, 0, 0);
    }
    else
    {
      p.stroke(60, 60, 60);
    }
    p.line(mid1.x(), mid1.y(), mid2.x(), mid2.y());
    p.stroke(0);
  }

  public void setSelected(boolean b)
  {
    this.isSelected = b;
  }

  public boolean isSelected()
  {
    return this.isSelected;
  }

  public boolean mouseOver(Vec2D position)
  {
    Vec2D mid1 = this.getMasterEdge().getMid().add(getMasterEdge().getShape().getPosition2D());
    Vec2D mid2 = this.getSlaveEdge().getMid().add(getSlaveEdge().getShape().getPosition2D());

    // create a vector that is perpendicular to the connections line
    Vec2D perpendicularVector = mid1.sub(mid2).perpendicular().getNormalized();

    // with the perpendicular vector, calculate the defining points of a rectangle around the connections line
    ArrayList<Vec2D> definingPoints = new ArrayList<Vec2D>();
    definingPoints.add(mid1.sub(perpendicularVector.getNormalizedTo(4)));
    definingPoints.add(mid2.sub(perpendicularVector.getNormalizedTo(4)));
    definingPoints.add(mid2.add(perpendicularVector.getNormalizedTo(4)));
    definingPoints.add(mid1.add(perpendicularVector.getNormalizedTo(4)));

    // create a rectangle around the edge
    Polygon2D borders = new Polygon2D(definingPoints);

    // check if the mousePointer is within the created rectangle
    return borders.containsPoint(position);
  }

  public void undoConnection()
  {
    // remove Tenons
    new Tenon(masterEdge);
    new Tenon(slaveEdge);
    // Edges are not locked anymore
    lockConnection(false);
    // Maybe in the future, this should also rotate the 3D-Shape back to its original position
    if(masterEdge.getShape().getConnected() == 0)
    {
      masterEdge.getShape().recalculate(masterEdge.getShape().getVertices());
    }
    if(slaveEdge.getShape().getConnected() == 0)
    {
      slaveEdge.getShape().recalculate(slaveEdge.getShape().getVertices());
    }
  }

  public boolean connect()
  {
    if (masterEdge.getShape() == slaveEdge.getShape())
    {
      // no connection allowed between two sides of the same shape
      // (until we use flexible materals :-)
      out.println("Do not connect two sides of the same shape!");
      return false;
    }
    else if (masterEdge.isLocked() || slaveEdge.isLocked())
    {
      // do not connect edges which already have a connection
      out.println("At least one edge is already connected!");
      return false;
    }
    else if (masterEdge.getV2().distanceTo(masterEdge.getV1()) != slaveEdge.getV2().distanceTo(slaveEdge.getV1()))
    {
      // no connection between edges of different length (problem: not exacty same length...)
      out.println("Edges have different length!");
      return false;
    }
    else if (slaveEdge.getShape().getConnected() == 0)
    {
      connectAlign(masterEdge, slaveEdge);
      connectRotate(masterEdge, slaveEdge, (float) Math.toRadians(-90.0));
      new Tenon(masterEdge, slaveEdge);
      lockConnection(true);
      return true;
      
    }
    else if (masterEdge.getShape().getConnected() == 0)
    {
      connectAlign(slaveEdge, masterEdge);
      connectRotate(slaveEdge, masterEdge, (float) Math.toRadians(-90.0));
      new Tenon(masterEdge, slaveEdge);
      lockConnection(true);
      return true;
    }
    else if (isEqualEdge(masterEdge, slaveEdge))
    {
      new Tenon(masterEdge, slaveEdge); // tenons are symmetric, the different orientation didn't do something wrong (at least i hope so)
      lockConnection(true);
      return true;
    }
    
    // if only one conenction between two shapes exists, we might rotate around this axis
    if (slaveEdge.getShape().getConnected() == 1 && isEqualEdge(masterEdge, slaveEdge))
    {
      connectRotate(masterEdge, slaveEdge, (float) Math.toRadians(-90.0));
      new Tenon(masterEdge, slaveEdge);
    }
    else if (masterEdge.getShape().getConnected() == 1 && isEqualEdge(masterEdge, slaveEdge))
    {
      connectRotate(slaveEdge, masterEdge, (float) Math.toRadians(-90.0));
      new Tenon(masterEdge, slaveEdge);
    }
    
    return false;
  }
  
  private boolean isEqualEdge(Edge masterEdge, Edge slaveEdge)
  {
    if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f)) return true;
    if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f)) return true;
    return false;
  }

  private void lockConnection(boolean locked)
  {
    slaveEdge.getShape().setConnected(locked);
    masterEdge.getShape().setConnected(locked);
    slaveEdge.setLocked(locked);
    masterEdge.setLocked(locked);
  }

  private void connectAlign(Edge masterEdge, Edge slaveEdge) {
    GShape master = masterEdge.getShape();
    GShape slave = slaveEdge.getShape();

    alignEdges(slave, masterEdge, slaveEdge);

    Vec3D toOrigin = slaveEdge.getP3D1().scale(-1);

    slave.translate3D(toOrigin);

    alignShapes(master, slave, masterEdge, slaveEdge);  
    // Might be necessary for independent rotate/align?
    Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1());
    slave.translate3D(toMaster);
  }

  private void connectRotate(Edge masterEdge, Edge slaveEdge, float angle) {
    // Might be necessary for independent rotate/align?
    Vec3D toOrigin = slaveEdge.getP3D1().scale(-1);
    slaveEdge.getShape().translate3D(toOrigin);
    
    GShape slave = slaveEdge.getShape();
    // rotate the slave by the specified angle (currently hardcoded 90 degrees)
    Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();
    slave.rotateAroundAxis(rotationAxis, angle);

    Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1());
    slave.translate3D(toMaster);
  }

  private void createTenons(Edge masterEdge, Edge slaveEdge) {
    Tenon tenon = new Tenon(masterEdge, slaveEdge);
  }

  private void alignEdges(GShape slave, Edge masterEdge, Edge slaveEdge)
  {
    Vec3D masterEdgeDirection = masterEdge.getP3D2().sub(masterEdge.getP3D1());
    Vec3D slaveEdgeDirection = slaveEdge.getP3D2().sub(slaveEdge.getP3D1());

    float angle = slaveEdgeDirection.angleBetween(masterEdgeDirection, true);

    Vec3D normalVector = slaveEdgeDirection.cross(masterEdgeDirection).getNormalized();
    while (normalVector.equals(new Vec3D(0,0,0)))
    {
      normalVector = masterEdgeDirection.cross(new Vec3D((float)Math.random(), (float)Math.random(), (float)Math.random())).getNormalized();
    }
    slave.rotateAroundAxis(normalVector, angle);
  }

  private void alignShapes(GShape master, GShape slave, Edge masterEdge, Edge slaveEdge)
  {
    Vec3D slaveEdgeDirection = slaveEdge.getP3D2().getNormalized();

    float angleBetweenNormals = calculateAngleBetweenNormals(master, slave);
    slave.rotateAroundAxis(slaveEdgeDirection, angleBetweenNormals);

    if (calculateAngleBetweenNormals(master, slave) > 0)
    {
      slave.rotateAroundAxis(slaveEdgeDirection, (float) -2.0 * angleBetweenNormals);
    }
  }

  private float calculateAngleBetweenNormals(GShape master, GShape slave)
  {
    Vec3D masterNormal = master.getNormalVector();
    Vec3D slaveNormal = slave.getNormalVector();

    return masterNormal.angleBetween(slaveNormal, true);
  }
}

