package de.mcp.customizer.algorithm;

import java.util.ArrayList;

import de.mcp.customizer.model.Edge;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 * @brief Static class to calculate the outline (tenon structure) of an edge
 * 
 *        Can be used either for unconnected edges (one edge) or two connected
 *        edges so far. Uses the normal vectors of the shape, to whom these
 *        edges belong, from the edges themselves the 2D coordinates as well as
 *        their Array of Vec2D, in which the outcome of the function (outline of
 *        the edge) is stored.
 */
public class CreateTenons {
	private static float tolerance = 0.1f;
	// factor to calculate the length of a tenon depending on the thickness
	// of the shapes:
	private static int relationTenonToEdge = 4;

	/**
	 * @brief Calculates the outline of one (unconnected) edge
	 * 
	 *        Calculates the outline of one (unconnected) edge - which is
	 *        basically just the line between the two points of this edge in the
	 *        2D view. It takes this two Vec2D, and sets the internal Arraylist
	 *        of Vec2D (representing the outline of the edge) within edges to
	 *        this two Vec2D
	 * 
	 * @param edge
	 *            is the the edge which outline has to be modified
	 */
	public static void createOutlineOfEdge(Edge edge) {
		ArrayList<Vec2D> basic = new ArrayList<Vec2D>();
		basic.add(edge.getV1());
		basic.add(edge.getV2());
		edge.setTenons(basic);
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
					.equalsWithTolerance(new Vec3D(0, 0, 0), tolerance)) {
				angle = (float) Math.PI;
			} else {
				angle = 0;
			}
		}
		return angle;
	}

	/**
	 * @brief Calculates the outline of two (connected) edges
	 * 
	 *        Calculates the outline of two (connected) edges - using the
	 *        addition of the thicknesses of both shapes times a factor for the
	 *        length of a tenon, and then uses the angle between both shapes
	 *        with these thicknesses to calculate how much each tenon of both
	 *        edges have to be cut in or extruded to be able to assemble them on
	 *        on side and have enough extrusion to be able to sandpaper these
	 *        tenons for a smooth edge. Breaks down if either the material
	 *        thickness is to big in relation to the size of a shape or the
	 *        angle is far to sharp (both need to cut-ins which are bigger than
	 *        possible). The resulting values are used to calculate an array of
	 *        vectors which determine the outline of each edge with its tenons.
	 *        These are then stored within the edge.
	 * 
	 * @param masterEdge
	 *            First Edge, which should be connected with
	 * @param slaveEdge
	 *            , the second edge
	 * 
	 *            The order of these edges determines which edge starts with a
	 *            cut in (the first one), while the other is extruded at the its
	 *            ends.
	 */
	public static void createOutlineOfEdge(Edge masterEdge, Edge slaveEdge) {

		Vec3D p1 = masterEdge.getShape().get3Dperpendicular(
				masterEdge.getP3D1(), masterEdge.getP3D2());
		Vec3D p2 = slaveEdge.getShape().get3Dperpendicular(slaveEdge.getP3D1(),
				slaveEdge.getP3D2());
		float angle = safeAngleBetween(p1, p2);

		float edgeLength = masterEdge.getV1().distanceTo(masterEdge.getV2());

		int thicknessMaster = masterEdge.getShape().getThickness();
		int thicknessSlave = slaveEdge.getShape().getThickness();

		// Calculate the number of tenons on the edges with a preliminary length
		// of these tenons
		int numberOfTenons = (int) ((2 * edgeLength) / (relationTenonToEdge * (thicknessMaster + thicknessSlave)));

		// The total number of tenons of a side should be always an odd number,
		// minimum 1
		// (better look (symmetric) than even numbers)
		numberOfTenons = (numberOfTenons / 2) * 2;
		numberOfTenons = numberOfTenons + 1;

		// The "exact" length of the tenons
		float lengthOfATenon = edgeLength / numberOfTenons;

		// How high (extrusion) and depth (cut-in) the tenons of both edges are
		// (not symmetric if both shapes have different thicknesses)
		float masterTenonHeight = getHeight(thicknessMaster, thicknessSlave,
				angle);
		float masterTenonDepth = masterTenonHeight; // still can't believe that,
		// but it works
		float slaveTenonHeight = getHeight(thicknessSlave, thicknessMaster,
				angle);
		float slaveTenonDepth = slaveTenonHeight; // still can't believe that,
		// but it works (as above)
		// Try to figure out if the tenon intrusions are to big (longer than
		// tenonSize)
		// Solution: Just limit them to 2 times their thickness
		// Problem:
		// - did not work for small angles...
		// - user has to modify the tenons afterwards (sandpaper :-)
		// - allow bigger intrusions has the inert problem that this may
		// interfere with neighbor tenons
		// - no check if the tenon is bigger than the actual size of the shape
		// (Solution for future work: find intersection with the other sides of
		// the shape ?)
		if ((angle > (float) 0 && angle < (float) Math.PI / 2)
				|| (angle > (float) Math.PI && angle < (float) Math.PI * 3 / 2)) {
			if (masterTenonDepth > 2 * thicknessMaster) {
				masterTenonDepth = 2 * thicknessMaster;
				slaveTenonHeight = thicknessMaster * (float) Math.sin(angle);
			}
			if (slaveTenonDepth > 2 * thicknessSlave) {
				slaveTenonDepth = 2 * thicknessSlave;
				masterTenonHeight = thicknessSlave * (float) Math.sin(angle);
			}
		} else {
			angle = angle - (float) Math.PI / 2;
			if (masterTenonDepth > 2 * thicknessMaster) {
				masterTenonDepth = 2 * thicknessMaster;
				slaveTenonHeight = (float) (masterTenonDepth / Math.sin(angle) + Math
						.abs((thicknessSlave / 2) / Math.tan(angle)));
			}
			if (slaveTenonDepth > 2 * thicknessSlave) {
				slaveTenonDepth = 2 * thicknessSlave;
				masterTenonHeight = (float) (slaveTenonDepth / Math.sin(angle) + Math
						.abs((thicknessMaster / 2) / Math.tan(angle)));
			}
		}
		masterEdge.setTenons(createTenons(masterEdge, lengthOfATenon,
				masterTenonHeight, masterTenonDepth, numberOfTenons, true));
		slaveEdge.setTenons(createTenons(slaveEdge, lengthOfATenon,
				slaveTenonHeight, slaveTenonDepth, numberOfTenons, false));
	}

	/*
	 * The Height (and Depth) of a tenon is calculated with the basic triangle
	 * formulas for a rectangled triangle, with the angle between the two shapes
	 * and both thicknesses as parameters
	 */
	private static float getHeight(int thicknessMaster, int thicknessSlave,
			float angle) {
		if (angle == (float) 0 || angle == (float) Math.PI) {
			// 180 degree angle (and 0 degree, but that shouldn't happen)
			// just use the intrusion of one side as a extrusion for the other
			angle = angle - 0.001f - (float) Math.PI / 2;
			return (thicknessSlave / 2) / (float) Math.cos(angle)
					+ (thicknessMaster / 2) * (float) Math.tan(angle);
		} else if (angle == (float) Math.PI / 2
				|| angle == (float) Math.PI * 3 / 2) {
			// 90 degree, -90 degree angle
			// since the logical edge is in the middle of the thickness we have
			// to add half the thickness the other (connected) shape
			// as extrusion as well as intrusion
			return thicknessSlave / 2;
		} else if ((angle > (float) 0 && angle < (float) Math.PI / 2)
				|| (angle > (float) Math.PI && angle < (float) Math.PI * 3 / 2)) {
			// acute angle: Each edge has to be intruded by
			// - the other shapes thickness/sin(angle) (triangle: opposite =
			// thickness/2 -> hypotenuse
			// - the own thickness/tan(angle) (triangle: opposite = thickness/2
			// -> adjacent
			return (thicknessSlave / 2) / (float) Math.sin(angle)
					+ (thicknessMaster / 2) / (float) Math.tan(angle);
		} else {
			// obtuse angle: Each edge has to be intruded by
			// - the other shapes thickness/cos(angle) (triangle: adjacent =
			// thickness/2 -> hypotenuse
			// - the own thickness/tan(angle) (triangle: opposite = thickness/2
			// -> adjacent
			angle = angle - (float) Math.PI / 2;
			return (thicknessSlave / 2) / (float) Math.cos(angle)
					+ (thicknessMaster / 2) * (float) Math.tan(angle);
		}
	}

	/*
	 * Creates the list of vectors for an edge: Each outline of an edge starts
	 * at its starting point, with either extruded (cut in) for the above
	 * mentioned height (depth) in perpendicular direction to the direction of
	 * the edge. The next point is then a length of one tenon away in the
	 * direction of this edge. Then substract the height (depth) and add next
	 * offset - depth(height) for the next point. And so on, until the end of
	 * the edge is reached.
	 */
	private static ArrayList<Vec2D> createTenons(Edge edge,
			float lengthOfATenon, float tenonHeight, float tenonDepth,
			int numberOfTenons, boolean beginWithExtrusion) {
		Vec2D edgeDirection = edge.getV2().sub(edge.getV1()).getNormalized();
		Vec2D tenonDirection = edge.getShape().get2Dperpendicular(edge.getV2(),
				edge.getV1());

		ArrayList<Vec2D> listTenons = new ArrayList<Vec2D>();
		Vec2D currentTenon = edge.getV1().copy();

		int startExtruded = beginWithExtrusion ? 0 : 1;

		for (int i = 0; i < numberOfTenons; i++) {
			float offset = ((i % 2) == startExtruded) ? tenonHeight
					: tenonDepth * (-1);
			currentTenon = currentTenon.add(tenonDirection.scale(offset));
			listTenons.add(currentTenon);
			currentTenon = currentTenon
					.add(edgeDirection.scale(lengthOfATenon));
			listTenons.add(currentTenon);
			currentTenon = currentTenon.sub(tenonDirection.scale(offset));
		}
		return listTenons;
	}
}
