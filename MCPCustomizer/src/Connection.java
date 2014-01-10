import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import toxi.geom.Line3D;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class Connection 
{
  private Edge masterEdge, slaveEdge;
  private boolean isSelected;
  private List<Connection> connections;

  public Connection(List<Connection> connections)
  {
    this.connections = connections;
    this.isSelected = false;
  }

  public Connection(Edge masterEdge, Edge slaveEdge, List<Connection> connections)
  {
    this.masterEdge = masterEdge;
    this.slaveEdge = slaveEdge;
    this.connections = connections;
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
    if(masterEdge.getShape().getNumberOfConnections() == 0)
    {
      masterEdge.getShape().recalculate(masterEdge.getShape().getVertices());
    }
    if(slaveEdge.getShape().getNumberOfConnections() == 0)
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
    else if (slaveEdge.getShape().getNumberOfConnections() == 0)
    {
      connectEdges(masterEdge, slaveEdge, (float) Math.PI);
      new Tenon(masterEdge, slaveEdge);
      lockConnection(true);
      return true;
      
    }
    else if (masterEdge.getShape().getNumberOfConnections() == 0)
    {
      connectEdges(slaveEdge, masterEdge, (float) Math.PI);
      new Tenon(masterEdge, slaveEdge);
      lockConnection(true);
      return true;
    }
    else if (isEqualEdge(masterEdge, slaveEdge))
    {
      new Tenon(masterEdge, slaveEdge); 
      // tenons are symmetric, the different orientation didn't do something wrong (at least i hope so)
      lockConnection(true);
      return true;
    }
    else if(((masterEdge.getShape().getNumberOfConnections() == 1) || (slaveEdge.getShape().getNumberOfConnections() == 1))
      && (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f) || 
          masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f) || 
          masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f) || 
          masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f)))
    {
      return findAngleIntersection(masterEdge, slaveEdge);
    }
    return false;
  }

//TODO Clean up, if it works
  private boolean findAngleIntersection(Edge masterEdge, Edge slaveEdge)
  {
    // Assumption: 
    // - masterEdge, slaveEdge and connectingShape are within one plane
    // - both vectors are same same/ both are different is already checked (and not true)
    // - masterEdge and slaveEdge are conencted with a common shape
    // - both masterEdge and slaveEdge could rotate

    // Determine which points are at the same position and which may be moved by rotating
    Vec3D commonPoint = masterEdge.getP3D1();
    Vec3D notCommonPointMaster = masterEdge.getP3D2();
    Vec3D notCommonPointSlave = slaveEdge.getP3D2();
    if(masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f) || masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f))
    {
      commonPoint = masterEdge.getP3D1();
      notCommonPointMaster = masterEdge.getP3D2();
      notCommonPointSlave = (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f)) ? slaveEdge.getP3D2() : slaveEdge.getP3D1();
    }
    else if (masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f) || masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f))
    {
      commonPoint = masterEdge.getP3D2();
      notCommonPointMaster = masterEdge.getP3D1();
      notCommonPointSlave = (masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f)) ? slaveEdge.getP3D2() : slaveEdge.getP3D1();
    }
    else
    {
      return false;
    }

    // Find the shape, which connects both shapes, and wthin this shape the axis which are aligned to the master- and slaveEdge
    GShape connectingShape = masterEdge.getShape();
    Edge rotatingEdgeMaster = masterEdge;
    Edge rotatingEdgeMaster2 = slaveEdge;
    for (Connection c : connections)
    {
      if(c.getMasterEdge().getShape() == masterEdge.getShape())
      {
        rotatingEdgeMaster = c.getMasterEdge();
        rotatingEdgeMaster2 = c.getSlaveEdge();
        connectingShape = c.getSlaveEdge().getShape();
      }
      else if (c.getSlaveEdge().getShape() == masterEdge.getShape())
      {
        rotatingEdgeMaster = c.getSlaveEdge();
        rotatingEdgeMaster2 = c.getMasterEdge();
        connectingShape = c.getMasterEdge().getShape();
      }
    }
    // The point of the rotating axis, which is not at the same position as the commonpoint of master- and slaveEdge
    Vec3D notCommonPointRotatingEdgeMaster = (rotatingEdgeMaster.getP3D1().equalsWithTolerance(commonPoint, 0.01f)) ? rotatingEdgeMaster.getP3D2() : rotatingEdgeMaster.getP3D1();

    // length of the new to connect (master-)edge
    float lengthMasterEdge = commonPoint.distanceTo(notCommonPointMaster);
    // angle between the new to connect (master-)edge and the rotatingEdge
    float angleMaster = notCommonPointRotatingEdgeMaster.sub(commonPoint).angleBetween(notCommonPointMaster.sub(commonPoint), true);
    
    // Do the same as above for the second (slave)shape
    Edge rotatingEdgeSlave = slaveEdge;
    Edge rotatingEdgeSlave2 = masterEdge;
    for (Connection c : connections)
    {
      if(c.getMasterEdge().getShape() == slaveEdge.getShape())
      {
        rotatingEdgeSlave = c.getMasterEdge();
        rotatingEdgeSlave2 = c.getSlaveEdge();
        connectingShape = c.getSlaveEdge().getShape();
      }
      else if (c.getSlaveEdge().getShape() == slaveEdge.getShape())
      {
        rotatingEdgeSlave = c.getSlaveEdge();
        rotatingEdgeSlave2 = c.getMasterEdge();
        connectingShape = c.getMasterEdge().getShape();
      }
    }

    Vec3D notCommonPointRotatingEdgeSlave = (rotatingEdgeSlave.getP3D1().equalsWithTolerance(commonPoint, 0.01f)) ? rotatingEdgeSlave.getP3D2() : rotatingEdgeSlave.getP3D1();

    float lengthSlaveEdge = commonPoint.distanceTo(notCommonPointSlave);
    float angleSlave = notCommonPointRotatingEdgeSlave.sub(commonPoint).angleBetween(notCommonPointSlave.sub(commonPoint), true);

    if (masterEdge.getShape().getNumberOfConnections() > 1) 
    {
      float angleResult = notCommonPointMaster.sub(commonPoint).angleBetween(notCommonPointRotatingEdgeSlave.sub(commonPoint));
      if ((float)Math.abs(angleResult - angleSlave)<0.01) 
      {
        Vec3D newNormalSlave = notCommonPointMaster.sub(commonPoint).cross(notCommonPointRotatingEdgeSlave.sub(commonPoint));
        float angle = connectingShape.getNormalVector().angleBetween(newNormalSlave, true);

        connectEdges(rotatingEdgeSlave2, rotatingEdgeSlave, angle);
        //Check
        if (!notCommonPointSlave.equalsWithTolerance(notCommonPointMaster, 0.01f))
        {
          connectEdges(rotatingEdgeSlave2, rotatingEdgeSlave, angle + (float)Math.PI/2);
          if (!notCommonPointSlave.equalsWithTolerance(notCommonPointMaster, 0.01f))
          {
            connectEdges(rotatingEdgeSlave2, rotatingEdgeSlave, -angle);
            if (!notCommonPointSlave.equalsWithTolerance(notCommonPointMaster, 0.01f))
            {
              connectEdges(rotatingEdgeSlave2, rotatingEdgeSlave, -angle - (float)Math.PI/2);
              if (!notCommonPointSlave.equalsWithTolerance(notCommonPointMaster, 0.01f))
              {
                return false; // I don't know how to rotate else
              }
            }
          }
        }
        new Tenon(rotatingEdgeMaster2, rotatingEdgeMaster);
        new Tenon(rotatingEdgeSlave2, rotatingEdgeSlave);
        new Tenon(masterEdge, slaveEdge);
        lockConnection(true);
        return true;
      }
      else
      {
        return false;
      }
    }
    else if (slaveEdge.getShape().getNumberOfConnections() > 1)
    {
      float angleResult = notCommonPointSlave.sub(commonPoint).angleBetween(notCommonPointRotatingEdgeMaster.sub(commonPoint));
      if ((float)Math.abs(angleResult - angleMaster)<0.01) 
      {
        Vec3D newNormalMaster = notCommonPointSlave.sub(commonPoint).cross(notCommonPointRotatingEdgeMaster.sub(commonPoint));
        float angle = connectingShape.getNormalVector().angleBetween(newNormalMaster, true);

        connectEdges(rotatingEdgeMaster2, rotatingEdgeMaster, angle);
        //Check
        if (!notCommonPointMaster.equalsWithTolerance(notCommonPointSlave, 0.01f))
        {
          connectEdges(rotatingEdgeMaster2, rotatingEdgeMaster, angle + (float)Math.PI/2);
          if (!notCommonPointMaster.equalsWithTolerance(notCommonPointSlave, 0.01f))
          {
            connectEdges(rotatingEdgeMaster2, rotatingEdgeMaster, -angle);
            if (!notCommonPointMaster.equalsWithTolerance(notCommonPointSlave, 0.01f))
            {
              connectEdges(rotatingEdgeMaster2, rotatingEdgeMaster, -angle - (float)Math.PI/2);
              if (!notCommonPointMaster.equalsWithTolerance(notCommonPointSlave, 0.01f))
              {
                return false; // I don't know how to rotate else
              }
            }
          }
        }
        new Tenon(rotatingEdgeMaster2, rotatingEdgeMaster);
        new Tenon(rotatingEdgeSlave2, rotatingEdgeSlave);
        new Tenon(masterEdge, slaveEdge);
        lockConnection(true);
        return true;
      }
      else
      {
        return false;
      }
    }
    else
    {
      // better save than sorry: align shapes
      if (masterEdge.getShape().getNumberOfConnections() == 0) connectEdges(rotatingEdgeMaster2, masterEdge, (float) Math.PI);
      if (slaveEdge.getShape().getNumberOfConnections() == 0) connectEdges(rotatingEdgeSlave2, slaveEdge, (float) Math.PI);

      // Since both shapes are aligned in one plane, we can add the (master-)edge to the rotating edge as it is and a second time
      // mirrored at the rotating edge. Both resulting points form a line perpendicular to the rotating edge, which is the projection
      // of the circle on this plane, which is described through the rotation of the (master-)edge endpoint around the roating axis.
      Vec3D master1 = notCommonPointRotatingEdgeMaster.sub(commonPoint).getNormalizedTo(lengthMasterEdge).rotateAroundAxis(connectingShape.getNormalVector(), angleMaster).add(commonPoint);
      Vec3D master2 = notCommonPointRotatingEdgeMaster.sub(commonPoint).getNormalizedTo(lengthMasterEdge).rotateAroundAxis(connectingShape.getNormalVector(), -angleMaster).add(commonPoint);
      Line3D intersectionLineMaster = new Line3D(master1, master2);

      Vec3D slave1 = notCommonPointRotatingEdgeSlave.sub(commonPoint).getNormalizedTo(lengthSlaveEdge).rotateAroundAxis(connectingShape.getNormalVector(), angleSlave).add(commonPoint);
      Vec3D slave2 = notCommonPointRotatingEdgeSlave.sub(commonPoint).getNormalizedTo(lengthSlaveEdge).rotateAroundAxis(connectingShape.getNormalVector(), -angleSlave).add(commonPoint);
      Line3D intersectionLineSlave = new Line3D(slave1, slave2);
    
      // The intersection of both (circle-projection)lines is the projection of the point in the 3D space, where both endpoints of
      // master- and slaveShape will met by rotation
      Vec3D intersectionPoint = intersectionLineMaster.closestLineTo(intersectionLineSlave).getLine().getMidPoint();

      // To get the angles for the rotation, we can produce a line in the direction of the roating axis
      // and find intersection of this line with the above used circle-projection line and can compute the distance between
      // this point and the previous computed intersection. This means: We have the projection of the distance between the
      // goal point and the rotating axis and the distance between the actual position and the rotating axis.
      // If we look at the plane perpendicular to the rotating edge, the first corresponds to the opposite/adjacent of a triangle
      // while the other one is the hypotenuse with opposite/adgjacent perpendicul/parallel to the base shapes
      Line3D axisRotateMaster = new Line3D(notCommonPointRotatingEdgeMaster, commonPoint).toRay3D().toLine3DWithPointAtDistance(10000);
      Vec3D intersectionAxisMaster = intersectionLineMaster.closestLineTo(axisRotateMaster).getLine().getMidPoint();
      float lengthIntersectionToAxisMaster = intersectionAxisMaster.distanceTo(intersectionPoint);
      float lengthVectorToAxisMaster =  intersectionAxisMaster.distanceTo(notCommonPointMaster);

      // Result: 4 possible angles, test, which one is the correct one
      float angleMasterA = (float)Math.asin(lengthIntersectionToAxisMaster/lengthVectorToAxisMaster) + (float)Math.PI;
      float angleMasterB = (float)Math.acos(lengthIntersectionToAxisMaster/lengthVectorToAxisMaster);
      //and other rotating direction???

      // and of course the same again for the slave
      Line3D axisRotateSlave = new Line3D(notCommonPointRotatingEdgeSlave, commonPoint).toRay3D().toLine3DWithPointAtDistance(10000);
      Vec3D intersectionAxisSlave = intersectionLineSlave.closestLineTo(axisRotateSlave).getLine().getMidPoint();
      float lengthIntersectionToAxisSlave = intersectionAxisSlave.distanceTo(intersectionPoint);
      float lengthVectorToAxisSlave =  intersectionAxisSlave.distanceTo(notCommonPointSlave);
      
      float angleSlaveA = (float)Math.asin(lengthIntersectionToAxisSlave/lengthVectorToAxisSlave) + (float)Math.PI;
      float angleSlaveB = (float)Math.acos(lengthIntersectionToAxisSlave/lengthVectorToAxisSlave);


      if(rotatingEdgeMaster.getShape().getNumberOfConnections()==1)
      {
        connectEdges(rotatingEdgeMaster2, rotatingEdgeMaster, angleMasterB);
      }

      if(rotatingEdgeSlave.getShape().getNumberOfConnections()==1)
      {
        connectEdges(rotatingEdgeSlave2, rotatingEdgeSlave, angleSlaveB);
      }
      //TODO: Check if rotated int the correct direction
      new Tenon(rotatingEdgeMaster2, rotatingEdgeMaster);
      new Tenon(rotatingEdgeSlave2, rotatingEdgeSlave);
      new Tenon(masterEdge, slaveEdge);
      lockConnection(true);
      return true;
    }
  }
  
  private boolean isEqualEdge(Edge masterEdge, Edge slaveEdge)
  {
    if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f)) return true;
    if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), 0.01f) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), 0.01f)) return true;
    return false;
  }

  private void lockConnection(boolean locked)
  {
    int addNumber = locked ? 1 : -1;
    slaveEdge.getShape().addNumberOfConnections(addNumber);
    masterEdge.getShape().addNumberOfConnections(addNumber);
    slaveEdge.setLocked(locked);
    masterEdge.setLocked(locked);
  }
  
  public void setAngle(float angle)
  {
	  if (!(this.slaveEdge.getShape().getNumberOfConnections() > 1))
	  {
		  connectEdges(this.masterEdge, this.slaveEdge, (float) Math.toRadians(angle));
	  }
  }

  private void connectEdges(Edge masterEdge, Edge slaveEdge, float angle) {
    GShape master = masterEdge.getShape();
    GShape slave = slaveEdge.getShape();

    alignEdges(slave, masterEdge, slaveEdge);

    Vec3D toOrigin = slaveEdge.getP3D1().scale(-1);

    slave.translate3D(toOrigin);

    alignShapes(master, slave, masterEdge, slaveEdge);  
    
    // rotate the slave by the specified angle (currently hardcoded 90 degrees)
    Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();
    slave.rotateAroundAxis(rotationAxis, angle);

    Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1());
    slave.translate3D(toMaster);
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

