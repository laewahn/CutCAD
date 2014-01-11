package de.mcp.customizer.algorithm;

import toxi.geom.*;

import java.util.*;

import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Edge;
import de.mcp.customizer.model.Rectangle;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.model.Tenon;

import static java.lang.System.*;

public class RotateAdjectantShapes
{
  private static Shape virtualShape = new Rectangle(new Vec3D(0,0,0), 1,1);
  private static Edge edgeA = new Edge(virtualShape.getShape(), new Vec3D(1,1,1), new Vec3D(1,1,1), new Vec2D(1,1), new Vec2D(1,1));
  private static Edge edgeB = new Edge(virtualShape.getShape(), new Vec3D(1,1,1), new Vec3D(1,1,1), new Vec2D(1,1), new Vec2D(1,1));
  private static Vec3D intersectionPoint = new Vec3D(0,0,0);

  public static boolean rotateBothShapes(Connection connection, Edge masterEdge, Edge slaveEdge)
  {
    // Assumption: 
    // - both vectors are not  the same/ both are different is already checked (and not true)
    // - masterEdge and slaveEdge are connencted with a common shape
    // - At least one of them could rotate
    edgeA = masterEdge;
    edgeB = slaveEdge;

    if (getNormalVector(masterEdge, slaveEdge).isZeroVector())
    {
      out.println("Error: Parallel Edges"); // Special case both rotating edges are aligned...What to do???
      return false;
    }

    Edge rotatingEdgeMaster = getRotatingEdge(masterEdge);
    Edge rotatingEdgeSlave = getRotatingEdge(slaveEdge);

    // The already connected Edges of both shapes are used to make are virtual shape for the alignment
    initialiseVirtualShape(masterEdge, slaveEdge);

    Edge rotateEdgeSlaveOfConnectingShape = virtualShape.getShape().getEdges().get(1); // the Vec3D of the slave-
    Edge rotateEdgeMasterOfConnectingShape = virtualShape.getShape().getEdges().get(0); // and the masterEdge are stored here

    if (masterEdge.getShape().getNumberOfConnections() > 1) 
    {
      float angle = rotateOnlyOneShape(connection, slaveEdge);
      boolean couldConnect = tryToConnectOneEdge(connection, rotateEdgeSlaveOfConnectingShape, rotatingEdgeSlave, angle);
      if (!couldConnect) return false;

      Tenon.createOutlineOfEdge(getAlreadyConnectedEdge(rotatingEdgeSlave), rotatingEdgeSlave);
      Tenon.createOutlineOfEdge(masterEdge, slaveEdge);
      connection.lockConnection(true);
      return true;
    }
    else if (slaveEdge.getShape().getNumberOfConnections() > 1)
    {
      float angle = rotateOnlyOneShape(connection, masterEdge);
      boolean couldConnect = tryToConnectOneEdge(connection, rotateEdgeMasterOfConnectingShape, rotatingEdgeMaster, angle);
      if (!couldConnect) return false;

      Tenon.createOutlineOfEdge(getAlreadyConnectedEdge(rotatingEdgeMaster), rotatingEdgeMaster);
      Tenon.createOutlineOfEdge(masterEdge, slaveEdge);
      connection.lockConnection(true);
      return true;
    }
    else
    {
      // since we use a virtual shape, both edges have to be aligned planar
      connection.connectEdges(rotateEdgeMasterOfConnectingShape, rotatingEdgeMaster, (float) Math.PI);
      connection.connectEdges(rotateEdgeSlaveOfConnectingShape, rotatingEdgeSlave, (float) Math.PI);
//      out.println("Aligned");
      
//       System.out.println("Master: ");
//       for (Edge e : masterEdge.getShape().getEdges())
//       {
//         System.out.println((e.getP3D1()) + ", " + (e.getP3D2()));
//       }
//       System.out.println("Slave: ");
//       for (Edge e : slaveEdge.getShape().getEdges())
//       {
//         System.out.println((e.getP3D1()) + ", "  + (e.getP3D2()));
//       }
//       System.out.println("Connection: ");
//       for (Edge e : virtualShape.getShape().getEdges())
//       {
//         System.out.println((e.getP3D1()) + ", "  + (e.getP3D2()));
//       }

      intersectionPoint = findIntersectionPoint(masterEdge, slaveEdge);
//      out.println("found intersectionPoint");

      float angleMasterB = getRotationFor(masterEdge);
      float angleSlaveB = getRotationFor(slaveEdge);
//      out.println("found angles");

      boolean couldConnect = tryToConnectBothEdges(connection, rotateEdgeMasterOfConnectingShape, rotatingEdgeMaster, angleMasterB, rotateEdgeSlaveOfConnectingShape, rotatingEdgeSlave, angleSlaveB);
      if (!couldConnect) return false;

      Tenon.createOutlineOfEdge(getAlreadyConnectedEdge(rotatingEdgeMaster), rotatingEdgeMaster);
      Tenon.createOutlineOfEdge(getAlreadyConnectedEdge(rotatingEdgeSlave), rotatingEdgeSlave);
      Tenon.createOutlineOfEdge(masterEdge, slaveEdge);
      connection.lockConnection(true);
      return true;
    }
  }
  
  private static Edge getOtherEdge(Edge edge) 
  {
    return (edge == edgeA) ? edgeB : edgeA;
  }

  private static Vec3D getCommonPoint(Edge edge)
  {
    return (compareEdges(edge, getOtherEdge(edge))) ? edge.getP3D1() : edge.getP3D2();
  }

  private static Vec3D getNotCommonPoint(Edge edge)
  {
    return (compareEdges(edge, getOtherEdge(edge))) ? edge.getP3D2() : edge.getP3D1();
  }

  private static Vec3D getPointOfRotatingEdge(Edge edge)
  {
    Edge rotatingEdge = getRotatingEdge(edge);
    return (compareEdges(rotatingEdge, edge)) ? rotatingEdge.getP3D2() : rotatingEdge.getP3D1();
  }

  private static Edge getRotatingEdge(Edge edge)
  {
    List<Edge> edges = edge.getShape().getEdges();
    int numberOfEdges = edges.size();
    if (compareEdges(edge, getOtherEdge(edge))) 
    {
      return edges.get((edges.indexOf(edge)+numberOfEdges-1)%(numberOfEdges));
    }
    else
    {
      return edges.get((edges.indexOf(edge)+1)%(numberOfEdges));
    }
  }

  private static boolean compareEdges(Edge edge1, Edge edge2)
  {
    boolean compareMasterP1ToSlaveP1 = edge1.getP3D1().equalsWithTolerance(edge2.getP3D1(), 0.01f);
    boolean compareMasterP1ToSlaveP2 = edge1.getP3D1().equalsWithTolerance(edge2.getP3D2(), 0.01f);
    return ((compareMasterP1ToSlaveP1 || compareMasterP1ToSlaveP2));
  }

  private static void initialiseVirtualShape(Edge masterEdge, Edge slaveEdge)
  {
    List<Edge> edges = virtualShape.getShape().getEdges();
    
    edges.get(0).setP3D1(getRotatingEdge(masterEdge).getP3D1().copy());
    edges.get(0).setP3D2(getRotatingEdge(masterEdge).getP3D2().copy());
    edges.get(1).setP3D1(getRotatingEdge(slaveEdge).getP3D1().copy());
    edges.get(1).setP3D2(getRotatingEdge(slaveEdge).getP3D2().copy());
    
    edges.get(2).setP3D1(getRotatingEdge(masterEdge).getP3D2().copy());
    edges.get(2).setP3D2(getRotatingEdge(slaveEdge).getP3D1().copy());

    edges.get(3).setP3D1(getRotatingEdge(slaveEdge).getP3D1().copy());
    edges.get(3).setP3D2(getRotatingEdge(masterEdge).getP3D1().copy());
  }

  private static float rotateOnlyOneShape(Connection connection, Edge edge)
  {
    Vec3D directionEdge = getNotCommonPoint(edge).sub(getCommonPoint(edge));
    Vec3D directionRotatingEdge = getPointOfRotatingEdge(edge).sub(getCommonPoint(edge));
//    float angleResult = directionEdge.angleBetween(directionRotatingEdge, true);
    Vec3D normalVectorSlave = directionEdge.cross(directionRotatingEdge);

    float angle = virtualShape.getShape().getNormalVector().angleBetween(normalVectorSlave, true);

    return angle;
  }

  private static Vec3D findIntersectionPoint(Edge edge1, Edge edge2)
  {
    // Since both shapes are aligned in one plane, we can add the (master-)edge to the rotating edge as it is and a second time
    // mirrored at the rotating edge. Both resulting points form a line perpendicular to the rotating edge, which is the projection
    // of the circle on this plane, which is described through the rotation of the (master-)edge endpoint around the roating axis.
    Line3D intersectionLine1 = getIntersectionLine(edge1);
    Line3D intersectionLine2 = getIntersectionLine(edge2);

    // The intersection of both (circle-projection)lines is the projection of the point in the 3D space, where both endpoints of
    // master- and slaveShape will met by rotation
    return intersectionLine1.closestLineTo(intersectionLine2).getLine().getMidPoint();
  }

  private static Line3D getIntersectionLine(Edge edge)
  {
	Edge virtualEdgeA = virtualShape.getShape().getEdges().get(0);
	Edge virtualEdgeB = virtualShape.getShape().getEdges().get(1);
    float angle = getPointOfRotatingEdge(edge).sub(getCommonPoint(edge)).angleBetween(getNotCommonPoint(edge).sub(getCommonPoint(edge)), true);
    Vec3D rotated = getNotCommonPoint(edge).sub(getCommonPoint(edge)).rotateAroundAxis(getNormalVector(virtualEdgeA, virtualEdgeB), -2*angle).add(getCommonPoint(edge));
//    out.println("Normalvec " + virtualShape.getShape().getNormalVector());
//    out.println("RotatingEdgePoint" + getPointOfRotatingEdge(edge));
//    out.println("CommonPoint" + getCommonPoint(edge));
//    out.println("NotCommonPoint" + getNotCommonPoint(edge));
//    out.println(new Line3D(getNotCommonPoint(edge), rotated));
    return new Line3D(getNotCommonPoint(edge), rotated);
  }

  private static float getRotationFor(Edge edge)
  {
    // To get the angles for the rotation, we can produce a line in the direction of the roating axis
    // and find intersection of this line with the above used circle-projection line and can compute the distance between
    // this point and the previous computed intersection. This means: We have the projection of the distance between the
    // goal point and the rotating axis and the distance between the actual position and the rotating axis.
    // If we look at the plane perpendicular to the rotating edge, the first corresponds to the opposite/adjacent of a triangle
    // while the other one is the hypotenuse with opposite/adgjacent perpendicul/parallel to the base shapes
    Line3D rotationAxis = new Line3D(getPointOfRotatingEdge(edge), getCommonPoint(edge)).toRay3D().toLine3DWithPointAtDistance(10000);
    Vec3D intersectionWithRotationAxis = getIntersectionLine(edge).closestLineTo(rotationAxis).getLine().getMidPoint();
    float lengthIntersectionToAxisMaster = intersectionWithRotationAxis.distanceTo(intersectionPoint);
    float lengthVectorToAxisMaster =  intersectionWithRotationAxis.distanceTo(getNotCommonPoint(edge));

    // Result: 4 possible angles, test, which one is the correct one
    // float angleMasterA = (float)Math.asin(lengthIntersectionToAxisMaster/lengthVectorToAxisMaster)+(float)Math.PI/2;
    return (float)Math.acos(lengthIntersectionToAxisMaster/lengthVectorToAxisMaster);
    // and other rotating direction???
  }

  private static Edge getAlreadyConnectedEdge(Edge edge)
  {
    for (Connection c : Connection.getConnections())
    {
      if (c.getMasterEdge() == edge) return c.getSlaveEdge();
      if (c.getSlaveEdge() == edge) return c.getMasterEdge();
    }
    return edgeA;
  }

  private static Vec3D getNormalVector(Edge edge1, Edge edge2)
  {
    Vec3D directionEdge1 = getPointOfRotatingEdge(edge1).sub(getCommonPoint(edge1));
    Vec3D directionEdge2 = getPointOfRotatingEdge(edge2).sub(getCommonPoint(edge2));
    return directionEdge1.cross(directionEdge2).normalize();
  }

  private static boolean tryToConnectBothEdges(Connection connection, Edge masterEdge1, Edge masterEdge2, float masterAngle, Edge slaveEdge1, Edge slaveEdge2, float slaveAngle)
  {
    boolean isCorrectAligned = false;
    for (int i=1; i<9; i++)
    {
      float newAngle = (masterAngle + (float)Math.PI/2*(int)((i-1)/2)) * (float)Math.pow(-1, i);
      connection.connectEdges(masterEdge1, masterEdge2, newAngle);
      isCorrectAligned = tryToConnectOneEdge(connection, slaveEdge1, slaveEdge2, slaveAngle);
      if (isCorrectAligned) return true;
    }
    return false;
  }

  private static boolean tryToConnectOneEdge(Connection connection, Edge masterEdge, Edge slaveEdge, float angle)
  {
    for (int i=1; i<9; i++)
    {
      float newAngle = (angle + (float)Math.PI/2*(int)((i-1)/2)) * (float)Math.pow(-1, i);
      connection.connectEdges(masterEdge, slaveEdge, newAngle);
      if ((edgeA.getP3D1().equalsWithTolerance(edgeB.getP3D1(), 0.1f) && edgeA.getP3D2().equalsWithTolerance(edgeB.getP3D2(), 0.1f))
        || (edgeA.getP3D1().equalsWithTolerance(edgeB.getP3D2(), 0.1f) && edgeA.getP3D2().equalsWithTolerance(edgeB.getP3D1(), 0.1f))) 
      {
        return true;
      }
    }
    return false;
  }
}