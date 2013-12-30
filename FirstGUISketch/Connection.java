import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import java.util.*;

public class Connection 
{
  private Edge masterEdge, slaveEdge;
  private Tenon tenon;

  public Connection()
  {
  }

  public Connection(Edge masterEdge, Edge slaveEdge)
  {
    this.masterEdge = masterEdge;
    this.slaveEdge = slaveEdge;
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
    p.stroke(255, 0, 0);
    p.line(mid1.x(), mid1.y(), mid2.x(), mid2.y());
    p.stroke(0);
  }

  public void connect()
  {

    GShape master = masterEdge.getShape();
    GShape slave = slaveEdge.getShape();

    alignEdges(slave, masterEdge, slaveEdge);

    Vec3D toOrigin = slaveEdge.getP3D1().scale(-1);

    slave.translate3D(toOrigin);

    alignShapes(master, slave, masterEdge, slaveEdge);

    // rotate the slave by the specified angle (currently hardcoded 90 degrees)
    Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();
    slave.rotateAroundAxis(rotationAxis, (float) Math.toRadians(-90.0));

    Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1());
    slave.translate3D(toMaster);
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

