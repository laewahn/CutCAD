package de.mcp.customizer.algorithm;

import toxi.geom.*;

import java.util.*;

import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.primitives.Edge;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.model.shapes.Rectangle;
import static java.lang.System.*;

/**
 * Rotates two shapes until two edges of them are at the same position
 * 
 * Rotates two shapes until two edges are at the same position - the edges need
 * to have a common point and both of them are already connected. The already
 * connected edges need to have at least one common point (same as the edges,
 * which should be rotated) and one of them has to have only one connection
 * (such that a rotation is still possible)
 * 
 */
public class RotateAdjectantShapes {
	private static float bigTolerance = 5f;
	private static float smallTolerance = 1f;

	private static Shape virtualShape = new Rectangle(new Vec3D(0, 0, 0), 1, 1);
	private static Edge edgeA = new Edge(virtualShape.getGShape(), new Vec3D(1,
			1, 1), new Vec3D(1, 1, 1), new Vector2D(1, 1), new Vector2D(1, 1));
	private static Edge edgeB = new Edge(virtualShape.getGShape(), new Vec3D(1,
			1, 1), new Vec3D(1, 1, 1), new Vector2D(1, 1), new Vector2D(1, 1));
	private static Vec3D intersectionPoint = new Vec3D(0, 0, 0);

	/**
	 *        Rotates two edges with a common point until the other is common,
	 *        too
	 * 
	 *        Two edges on different shapes, which have one common point, at
	 *        least one of their corresponding shapes can be rotated (only one
	 *        connection with another shape) and the rotating axis are also
	 *        connected with this common point. If only one shape may rotate,
	 *        use the coordinates of the other edges not-common point of the
	 *        edge to determine how to rotate, and then tries to rotate until
	 *        this position is reached. Otherwise it will stop. If both shapes
	 *        may rotate, it will find the intersection of the circles which
	 *        both edges will describe by rotating around their rotating axis,
	 *        and then again tries to rotate (now both edges) until this
	 *        position is reached (or stops , if it can't reach it). Works only
	 *        if the rotating edges have also a common point (the same as the two
	 *        edges which should be connected)
	 * 
	 * @param connection
	 *            The connection which should be made by rotating the edges. If
	 *            both shapes could be aligned, such that these edges are at the
	 *            same position, this connection is finally made and stored
	 * @param masterEdge
	 *            The first edge which should be connected with the
	 * @param slaveEdge
	 *            , the second edge
	 * @return true if the shapes could be rotated until both edges are at the same position
	 */
	public static boolean rotateBothShapes(Connection connection,
			Edge masterEdge, Edge slaveEdge) {
		// Assumption:
		// - both vectors are not the same/ both are different is already
		// checked (and not true)
		// - masterEdge and slaveEdge are connected with a common shape
		// - At least one of them could rotate
		edgeA = masterEdge;
		edgeB = slaveEdge;

		if (getNormalVector(masterEdge, slaveEdge).isZeroVector()) {
			out.println("Error: Parallel Edges"); // Special case both rotating
			// edges are aligned...What
			// to do???
			return false;
		}

		Edge rotatingEdgeMaster = getRotatingEdge(masterEdge);
		Edge rotatingEdgeSlave = getRotatingEdge(slaveEdge);

		// The already connected Edges of both shapes are used to create a
		// virtual shape for the alignment
		initialiseVirtualShape(masterEdge, slaveEdge);

		Edge rotateEdgeSlaveOfConnectingShape = virtualShape.getGShape()
				.getEdges().get(1); // the Vec3D of the slave-
		Edge rotateEdgeMasterOfConnectingShape = virtualShape.getGShape()
				.getEdges().get(0); // and the masterEdge are stored here

		if (masterEdge.getGShape().getNumberOfConnections() > 1) {
			connection.connectEdges(rotateEdgeSlaveOfConnectingShape,
					rotatingEdgeSlave, (float) Math.PI); // align the other
			// shape planar with
			// the virtual shape
			float angle = rotateOnlyOneShape(slaveEdge, masterEdge);
			boolean couldConnect = tryToConnectOneEdge(connection,
					rotateEdgeSlaveOfConnectingShape, rotatingEdgeSlave, angle);
			if (!couldConnect) {
//				connection.connectEdges(rotateEdgeSlaveOfConnectingShape,
//						rotatingEdgeSlave, (float) Math.PI);
				return false;
			}

			CreateTenons.createOutlineOfEdge(
					getAlreadyConnectedEdge(rotatingEdgeSlave),
					rotatingEdgeSlave);
			CreateTenons.createOutlineOfEdge(masterEdge, slaveEdge);
			connection.lockConnection(true);
			return true;
		} else if (slaveEdge.getGShape().getNumberOfConnections() > 1) {
			// the equivalent as above for the other edge
			connection.connectEdges(rotateEdgeMasterOfConnectingShape,
					rotatingEdgeMaster, (float) Math.PI);
			float angle = rotateOnlyOneShape(masterEdge, slaveEdge);
			boolean couldConnect = tryToConnectOneEdge(connection,
					rotateEdgeMasterOfConnectingShape, rotatingEdgeMaster,
					angle);
			if (!couldConnect) {
//				connection.connectEdges(rotateEdgeMasterOfConnectingShape,
//						rotatingEdgeMaster, (float) Math.PI);
				return false;
			}

			CreateTenons.createOutlineOfEdge(
					getAlreadyConnectedEdge(rotatingEdgeMaster),
					rotatingEdgeMaster);
			CreateTenons.createOutlineOfEdge(masterEdge, slaveEdge);
			connection.lockConnection(true);
			return true;
		} else {
			// since we use a virtual shape, both edges have to be aligned
			// planar
			connection.connectEdges(rotateEdgeMasterOfConnectingShape,
					rotatingEdgeMaster, (float) Math.PI);
			connection.connectEdges(rotateEdgeSlaveOfConnectingShape,
					rotatingEdgeSlave, (float) Math.PI);

			intersectionPoint = findIntersectionPoint(masterEdge, slaveEdge);

			// Not symmetric, different angles for both shapes(edges) possible
			float angleMasterB = getRotationFor(masterEdge);
			float angleSlaveB = getRotationFor(slaveEdge);

			boolean couldConnect = tryToConnectBothEdges(connection,
					rotateEdgeMasterOfConnectingShape, rotatingEdgeMaster,
					angleMasterB, rotateEdgeSlaveOfConnectingShape,
					rotatingEdgeSlave, angleSlaveB);
			if (!couldConnect) {
//				connection.connectEdges(rotateEdgeMasterOfConnectingShape,
//						rotatingEdgeMaster, (float) Math.PI);
//				connection.connectEdges(rotateEdgeSlaveOfConnectingShape,
//						rotatingEdgeSlave, (float) Math.PI);
				return false;
			}

			CreateTenons.createOutlineOfEdge(
					getAlreadyConnectedEdge(rotatingEdgeMaster),
					rotatingEdgeMaster);
			CreateTenons.createOutlineOfEdge(
					getAlreadyConnectedEdge(rotatingEdgeSlave),
					rotatingEdgeSlave);
			CreateTenons.createOutlineOfEdge(masterEdge, slaveEdge);
			connection.lockConnection(true);
			return true;
		}
	}

	/*
	 * If it's the masterEdge, returns the slaveEdge and vice versa
	 */
	private static Edge getOtherEdge(Edge edge) {
		return (edge == edgeA) ? edgeB : edgeA;
	}

	/*
	 * Returns the common point with the other edge
	 */
	private static Vec3D getCommonPoint(Edge edge) {
		return (compareEdges(edge, getOtherEdge(edge))) ? edge.getP3D1() : edge
				.getP3D2();
	}

	/*
	 * Returns the point to move until it is also a common point between the two
	 * shapes (which is not a common point yet :-)
	 */
	private static Vec3D getNotCommonPoint(Edge edge) {
		return (compareEdges(edge, getOtherEdge(edge))) ? edge.getP3D2() : edge
				.getP3D1();
	}

	/*
	 * Find the edge, around which a shape may rotate and returns it
	 */
	private static Edge getRotatingEdge(Edge edge) {
		List<Edge> edges = edge.getGShape().getEdges();
		int numberOfEdges = edges.size();
		if (compareEdges(edge, getOtherEdge(edge))) {
			return edges.get((edges.indexOf(edge) + numberOfEdges - 1)
					% (numberOfEdges));
		} else {
			return edges.get((edges.indexOf(edge) + 1) % (numberOfEdges));
		}
	}

	/*
	 * Returns the point of the above mentioned rotatingEdge, which is not the
	 * common point (other possible rotations are not allowed here)
	 */
	private static Vec3D getPointOfRotatingEdge(Edge edge) {
		Edge rotatingEdge = getRotatingEdge(edge);
		return (compareEdges(rotatingEdge, edge)) ? rotatingEdge.getP3D2()
				: rotatingEdge.getP3D1();
	}

	/*
	 * Checks if either the first or the second point of edge1 is on the same
	 * position (common point) as a point of edge2
	 */
	private static boolean compareEdges(Edge edge1, Edge edge2) {
		boolean compareMasterP1ToSlaveP1 = edge1.getP3D1().equalsWithTolerance(
				edge2.getP3D1(), bigTolerance);
		boolean compareMasterP1ToSlaveP2 = edge1.getP3D1().equalsWithTolerance(
				edge2.getP3D2(), bigTolerance);
		return ((compareMasterP1ToSlaveP1 || compareMasterP1ToSlaveP2));
	}

	/*
	 * Initializes virtual shape is made out of the two rotating edges
	 */
	private static void initialiseVirtualShape(Edge masterEdge, Edge slaveEdge) {
		List<Edge> edges = virtualShape.getGShape().getEdges();

		edges.get(0).setP3D1(getRotatingEdge(masterEdge).getP3D1().copy());
		edges.get(0).setP3D2(getRotatingEdge(masterEdge).getP3D2().copy());
		edges.get(1).setP3D1(getRotatingEdge(slaveEdge).getP3D1().copy());
		edges.get(1).setP3D2(getRotatingEdge(slaveEdge).getP3D2().copy());

		edges.get(2).setP3D1(getRotatingEdge(masterEdge).getP3D2().copy());
		edges.get(2).setP3D2(getRotatingEdge(slaveEdge).getP3D1().copy());

		edges.get(3).setP3D1(getRotatingEdge(slaveEdge).getP3D1().copy());
		edges.get(3).setP3D2(getRotatingEdge(masterEdge).getP3D1().copy());
	}

	/*
	 * Only one Shape can be rotated: Comparing the normal vector of the virtual
	 * shape (= normal vector of the shape, to which edgeA belong, because of
	 * alignment) with the normalVector of plane of the rotating Edge, around
	 * which this shape (of edgeA) may rotate and the edgeB of the other shape
	 * (which is the position which should be also occupied by edgeA after the
	 * rotation. This angle is then returned
	 */
	private static float rotateOnlyOneShape(Edge edgeA, Edge edgeB) {
		Vec3D directionEdge = getNotCommonPoint(edgeB).sub(
				getCommonPoint(edgeB));
		Vec3D directionRotatingEdge = getPointOfRotatingEdge(edgeA).sub(
				getCommonPoint(edgeA));
		Vec3D normalVectorSlave = directionEdge.cross(directionRotatingEdge)
				.normalize();

		float angle = safeAngleBetween(virtualShape.getGShape()
				.getNormalVector(), normalVectorSlave);

		return angle;
	}

	/*
	 * Returns the projection of the intersection point of two edges, which are
	 * rotated around other vectors (describing circles in 3D), which is planar
	 * projected on the plane of the virtual shape
	 */
	private static Vec3D findIntersectionPoint(Edge edge1, Edge edge2) {
		// Since both shapes are aligned in one plane, we can add the
		// (master-)edge to the rotating edge as it is and a second time
		// mirrored at the rotating edge. Both resulting points form a line
		// perpendicular to the rotating edge, which is the projection
		// of the circle on this plane, which is described through the rotation
		// of the (master-)edge end point around the rotating axis.
		Line3D intersectionLine1 = getIntersectionLine(edge1);
		Line3D intersectionLine2 = getIntersectionLine(edge2);

		// The intersection of both (circle-projection)lines is the projection
		// of the point in the 3D space, where both end points of
		// master- and slaveShape will met by rotation
		return intersectionLine1.closestLineTo(intersectionLine2).getLine()
				.getMidPoint();
	}

	/*
	 * Calculates the angle between two Vec3D - the basic angleBetween didn't
	 * return valid values for parallel Vec3D, the two possible cases are
	 * checked - since both vec3D are normalized in our case, an addition of
	 * both values should have a result near zero, if an angle of 180 degree
	 * exists between these two vectors, otherwise the angle is 0 degree.
	 */
	private static float safeAngleBetween(Vec3D masterEdgeDirection,
			Vec3D slaveEdgeDirection) {
		float angle = slaveEdgeDirection
				.angleBetween(masterEdgeDirection, true);
		if (Float.isNaN(angle)) {
			if (slaveEdgeDirection.add(masterEdgeDirection)
					.equalsWithTolerance(new Vec3D(0, 0, 0), 0.1f)) {
				angle = (float) Math.PI;
			} else {
				angle = 0;
			}
		}
		return angle;
	}

	/*
	 * Creates a Line3D from a edge by using one point of this edge as a
	 * starting point and then rotates this edge with 180 degree around a
	 * rotating axis with the virtual shape (mirror it) for the second point.
	 * This results in the projection of the circle, describing the rotation of
	 * the edge around the axis on the virtual shape plane.
	 */
	private static Line3D getIntersectionLine(Edge edge) {
		Edge virtualEdgeA = virtualShape.getGShape().getEdges().get(0);
		Edge virtualEdgeB = virtualShape.getGShape().getEdges().get(1);
		Vec3D rotationAxis = getPointOfRotatingEdge(edge).sub(
				getCommonPoint(edge));
		Vec3D notCommonPoint = getNotCommonPoint(edge)
				.sub(getCommonPoint(edge));
		float angle = safeAngleBetween(rotationAxis, notCommonPoint);

		Vec3D rotated = (getNotCommonPoint(edge).sub(getCommonPoint(edge)))
				.rotateAroundAxis(getNormalVector(virtualEdgeA, virtualEdgeB),
						-2 * angle).add(getCommonPoint(edge));
		if (safeAngleBetween(rotationAxis, rotated.sub(getCommonPoint(edge))) != angle) {
			rotated = (getNotCommonPoint(edge).sub(getCommonPoint(edge)))
					.rotateAroundAxis(
							getNormalVector(virtualEdgeA, virtualEdgeB),
							2 * angle).add(getCommonPoint(edge));
		}
		return new Line3D(getNotCommonPoint(edge), rotated);
	}

	/*
	 * Returns the angle, how the shape of edge has to be rotated until it is on
	 * the correct position. It uses the projection of the intersection point on
	 * the virtual shape to determine this intersection point in 3D space
	 * (triangle formulas) an then compares the normal vector of the virtual
	 * shape (= normal vector shape, because of the alignment) with the plane,
	 * which is made by the rotating axis and the edge between intersection
	 * point and the common point of the two edges, which should be connected
	 */
	private static float getRotationFor(Edge edge) {
		// To get the angles for the rotation, we can produce a line in the
		// direction of the rotating axis
		// and find intersection of this line with the above used
		// circle-projection line and can compute the distance between
		// this point and the previous computed intersection. This means: We
		// have the projection of the distance between the
		// goal point and the rotating axis and the distance between the actual
		// position and the rotating axis.
		// If we look at the plane perpendicular to the rotating edge, the first
		// corresponds to the opposite/adjacent of a triangle
		// while the other one is the hypotenuse with opposite/adjacent
		// perpendicular/parallel to the base shapes
		Line3D rotationAxis = new Line3D(getPointOfRotatingEdge(edge),
				getCommonPoint(edge)).toRay3D().toLine3DWithPointAtDistance(
				10000);
		Vec3D intersectionWithRotationAxis = getIntersectionLine(edge)
				.closestLineTo(rotationAxis).getLine().getMidPoint();
		double lengthIntersectionToAxisMaster = intersectionWithRotationAxis
				.distanceTo(intersectionPoint);
		double lengthVectorToAxisMaster = intersectionWithRotationAxis
				.distanceTo(getNotCommonPoint(edge));

		// Result: 4 possible angles, test, which one is the correct one
		// return
		// (float)Math.asin(lengthIntersectionToAxisMaster/lengthVectorToAxisMaster);
		return (float) Math.acos(lengthIntersectionToAxisMaster
				/ lengthVectorToAxisMaster);
		// and other rotating direction???
	}

	/*
	 * Search all connections for an edge, and returns the edge, with whom this
	 * edge forms a connection
	 */
	private static Edge getAlreadyConnectedEdge(Edge edge) {
		for (Connection c : Connection.getConnections()) {
			if (c.getMasterEdge() == edge)
				return c.getSlaveEdge();
			if (c.getSlaveEdge() == edge)
				return c.getMasterEdge();
		}
		return edgeA;
	}

	/*
	 * Returns the normalVector of a plane formed by edge 1 and edge2
	 */
	private static Vec3D getNormalVector(Edge edge1, Edge edge2) {
		Vec3D directionEdge1 = getPointOfRotatingEdge(edge1).sub(
				getCommonPoint(edge1));
		Vec3D directionEdge2 = getPointOfRotatingEdge(edge2).sub(
				getCommonPoint(edge2));
		return directionEdge1.cross(directionEdge2).normalize();
	}

	/*
	 * Try to rotate a slaveEdge around its rotation axis with a combination of
	 * +/- angle and multiples of PI until this edge and a masterEdge are at the
	 * same position. Returns true, if it could do this, otherwise false.
	 */
	private static boolean tryToConnectOneEdge(Connection connection,
			Edge masterEdge, Edge slaveEdge, float angle) {
		for (int i = 0; i < 9; i++) {
			float newAngle = (angle + (float) Math.PI / 2 * (int) (i / 2))
					* (float) Math.pow(-1, (i + 1));
			connection.connectEdges(masterEdge, slaveEdge, newAngle);

			if ((edgeA.getP3D1().equalsWithTolerance(edgeB.getP3D1(),
					smallTolerance) && edgeA.getP3D2().equalsWithTolerance(
					edgeB.getP3D2(), bigTolerance))
					|| (edgeA.getP3D1().equalsWithTolerance(edgeB.getP3D2(),
							smallTolerance) && edgeA.getP3D2()
							.equalsWithTolerance(edgeB.getP3D1(), bigTolerance))) {
				return true;
			}
		}
		for (int i = 0; i < 9; i++) {
			float newAngle = (-angle + (float) Math.PI / 2 * (int) (i / 2))
					* (float) Math.pow(-1, (i + 1));
			connection.connectEdges(masterEdge, slaveEdge, newAngle);

			if ((edgeA.getP3D1().equalsWithTolerance(edgeB.getP3D1(),
					smallTolerance) && edgeA.getP3D2().equalsWithTolerance(
					edgeB.getP3D2(), bigTolerance))
					|| (edgeA.getP3D1().equalsWithTolerance(edgeB.getP3D2(),
							smallTolerance) && edgeA.getP3D2()
							.equalsWithTolerance(edgeB.getP3D1(), bigTolerance))) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Try to rotate two edges around the corresponding axis and the calculated
	 * (+/-)angles combined with different multiplies of PI until both edges are
	 * at the same position. Rotate one edge with one possible edge, the call
	 * the tryToConnectOneEdge for the other edge until either both edges are at
	 * the same position (return true) or all "calculated" angles are tested
	 * (return false)
	 */
	private static boolean tryToConnectBothEdges(Connection connection,
			Edge masterEdge1, Edge masterEdge2, float masterAngle,
			Edge slaveEdge1, Edge slaveEdge2, float slaveAngle) {
		boolean isCorrectAligned = false;
		for (int i = 1; i < 9; i++) {
			float newAngle = (masterAngle + (float) Math.PI / 2
					* (int) ((i - 1) / 2))
					* (float) Math.pow(-1, i);
			connection.connectEdges(masterEdge1, masterEdge2, newAngle);
			isCorrectAligned = tryToConnectOneEdge(connection, slaveEdge1,
					slaveEdge2, slaveAngle);
			if (isCorrectAligned)
				return true;
		}
		return false;
	}
}