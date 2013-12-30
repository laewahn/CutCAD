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
  private Tenon tenon;
  private boolean connected;

  public Connection()
  {
  }

  public Connection(Edge masterEdge, Edge slaveEdge)
  {
    this.masterEdge = masterEdge;
    this.slaveEdge = slaveEdge;
    this.connected = false;
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
    if (connected) {
      Vec2D mid1 = this.getMasterEdge().getMid().add(getMasterEdge().getShape().getPosition2D());
      Vec2D mid2 = this.getSlaveEdge().getMid().add(getSlaveEdge().getShape().getPosition2D());
      p.stroke(255, 0, 0);
      p.line(mid1.x(), mid1.y(), mid2.x(), mid2.y());
      p.stroke(0);
    }
  }

  public void connect()
  {
    if (masterEdge.getShape() == slaveEdge.getShape())
    {
      // no connection allowed between two sides of the same shape
      // (until we use flexible materals :-)
      out.println("Do not connect two sides of the same shape!");
    }
    else if (masterEdge.isLocked() || slaveEdge.isLocked())
    {
      // do not connect edges which already have a connection
      out.println("At least one edge is already connected!");
    }
    else if (masterEdge.getV2().sub(masterEdge.getV1()).magnitude() != slaveEdge.getV2().sub(slaveEdge.getV1()).magnitude())
    {
      // no connection between edges of different length (problem: not exacty same length...)
      out.println("Edges have different length!");
    }
    else if (!slaveEdge.getShape().isAlignLocked())
    {
      // align & rotate slave, makeTennons
      connectAlign(masterEdge, slaveEdge);
      connectRotate(masterEdge, slaveEdge, (float) Math.toRadians(-90.0));
      createTenons(masterEdge, slaveEdge);

      lockAlign();
      lockRotated();
      lockEdge();
    }
    else if (!masterEdge.getShape().isAlignLocked())
    {
      // align & rotate master, makeTennons
      connectAlign(slaveEdge, masterEdge);
      connectRotate(slaveEdge, masterEdge, (float) Math.toRadians(-90.0));
      createTenons(masterEdge, slaveEdge);

      lockAlign();
      lockRotated();
      lockEdge();
    }
    else if (!slaveEdge.getShape().isRotatedLocked())
    {
      // rotate slave, makeTennons
      connectRotate(masterEdge, slaveEdge, (float) Math.toRadians(-90.0));
      createTenons(masterEdge, slaveEdge);

      lockRotated();
      lockEdge();
    }
    else if (!masterEdge.getShape().isRotatedLocked())
    {
      // rotate master, makeTennons
      connectRotate(slaveEdge, masterEdge, (float) Math.toRadians(-90.0));
      createTenons(masterEdge, slaveEdge);

      lockRotated();
      lockEdge();
    }
    else if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f))
    {
      // makeTennons
      createTenons(masterEdge, slaveEdge);
      lockEdge();
    }
    else if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f))
    {
      // makeTennons
      createTenons(masterEdge, slaveEdge); //tenons are symmetric, the different orientation didn't do something wrong (at least i hope so)
      lockEdge();
    } 
    else {
      // do nothing, if we haven't forgotten anything
    }
  }

  private void lockAlign()
  {
    slaveEdge.getShape().setAlignLocked(true);
    masterEdge.getShape().setAlignLocked(true);
  }

  private void lockRotated()
  {
    slaveEdge.getShape().setRotatedLocked(true);
    masterEdge.getShape().setRotatedLocked(true);
  }

  private void lockEdge()
  {
    slaveEdge.setLocked(true);
    masterEdge.setLocked(true);
    this.connected = true;
  }

  private void connectAlign(Edge masterEdge, Edge slaveEdge) {
    GShape master = masterEdge.getShape();
    GShape slave = slaveEdge.getShape();

    alignEdges(slave, masterEdge, slaveEdge);

    Vec3D toOrigin = slaveEdge.getP3D1().scale(-1);

    slave.translate3D(toOrigin);

    alignShapes(master, slave, masterEdge, slaveEdge);
  }

  private void connectRotate(Edge masterEdge, Edge slaveEdge, float angle) {
    GShape slave = slaveEdge.getShape();
    // rotate the slave by the specified angle (currently hardcoded 90 degrees)
    Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();
    slave.rotateAroundAxis(rotationAxis, angle);

    Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1());
    slave.translate3D(toMaster);
  }

  private void createTenons(Edge masterEdge, Edge slaveEdge) {
    this.tenon = new Tenon(masterEdge, slaveEdge, (float)Math.PI/2, true, true);
    masterEdge.getShape().setTenon(masterEdge, tenon);
    slaveEdge.getShape().setTenon(slaveEdge, tenon);
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

