package de.mcp.customizer.model;

import java.util.ArrayList;
import java.util.List;
import de.mcp.customizer.algorithm.CreateTenons;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Drawable3D;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Line2D;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 * General shape class, everything some kind of shape needs to know,
 * edges, corners, draw2D and draw3D, transform outline from 2D to 3D
 * correct corner from...
 */
public class GShape implements Drawable2D, Drawable3D {
	private int numberOfConnections;
	private Vec2D position2D;
	private Vec3D position3D;
	private boolean isSelected, isActive;
	private List<Vec2D> vertices;
	private List<Vec3D> vertices3D;
	private List<Cutout> cutouts = new ArrayList<Cutout>();
	private List<Edge> edges;
	private Shape shape;
	private Material material;
	private String name;
	private float scalingFactor = 0.5f;
	private float scalingFactor3D = 0.5f;

	/**
	 * Creates edges with the 3D and 2D representation of this form (one edge between each pair of vertices)
	 * Uses a standard material. 2D position is just a XY-Projection of the 3D position (where the z-component is 0)
	 * 
	 * @param initVertices Form of the shape in 2D view
	 * @param position of this form in 3D
	 * @param shape type of form, to which this form belong
	 */
	public GShape(List<Vec2D> initVertices, Vec3D position, Shape shape) {
		this.position2D = position.to2DXY();
		this.position3D = position;
		this.isSelected = false;
		this.isActive = false;
		this.shape = shape;
		this.numberOfConnections = 0;
		this.material = AllMaterials.getBaseMaterial();

		vertices = initVertices;
		edges = new ArrayList<Edge>();
		vertices3D = new ArrayList<Vec3D>();

		for (Vec2D v : vertices) {
			vertices3D.add(v.add(position2D).to3DXY());
		}
		for (int i = 0; i < vertices.size(); i++) {
			edges.add(new Edge(this, vertices3D.get(i), vertices3D.get((i + 1)
					% (vertices.size())), vertices.get(i), vertices.get((i + 1)
							% (vertices.size()))));
		}
	}
	
	/**
	 * @return all Cut-outs of an objects
	 */
	public List<Cutout> getCutouts() {
		return cutouts;
	}

	/**
	 * @param factor for scaling the 2D view
	 */
	public void setScalingFactor(float factor) {
		scalingFactor = factor;
	}

	/**
	 * @return factor for scaling the 2D view
	 */
	public float getScalingFactor() {
		return scalingFactor;
	}

	/**
	 * @param factor for scaling the 3D view
	 */
	public void setScalingFactor3D(float factor) {
		scalingFactor3D = factor;
	}

	/**
	 * @return factor for scaling the 3D view
	 */
	public float getScalingFactor3D() {
		return scalingFactor3D;
	}

	/**
	 * Recalculate form (edges) of the shape, if the form of its parent shape is modified
	 * @param basicShape changed form in 2D
	 */
	public void recalculate(List<Vec2D> basicShape) {
		if (this.numberOfConnections == 0) {
			vertices = basicShape;
			edges.clear();
			vertices3D.clear();

			for (Vec2D v : vertices) {
				vertices3D.add(v.add(position2D).to3DXY());
			}
			for (int i = 0; i < vertices.size(); i++) {
				edges.add(new Edge(this, vertices3D.get(i), vertices3D
						.get((i + 1) % (vertices.size())), vertices.get(i),
						vertices.get((i + 1) % (vertices.size()))));
			}
		}
	}

	/**
	 * @param cutout add this cut-out to this form
	 */
	public void addCutout(GShape cutout) {
		cutouts.add(new Cutout(this, cutout));
	}

	/**
	 * @param cutout remove this cut-out from the form
	 */
	public void removeCutout(Cutout cutout) {
		cutouts.remove(cutout);
	}

	/**
	 * @return number of connected edges
	 */
	public int getNumberOfConnections() {
		return this.numberOfConnections;
	}

	/**
	 * @param connections a number of new connected edges
	 */
	public void addNumberOfConnections(int connections) {
		this.numberOfConnections = this.numberOfConnections + connections;
	}

	/**
	 * @return the parent shape
	 */
	public Shape getShape() {
		return this.shape;
	}

	/**
	 * @return the thickness of this form (0.1mm)
	 */
	public int getThickness() {
		return this.material.getMaterialThickness();
	}

	/**
	 * @return Material, of which the form consists
	 */
	public Material getMaterial() {
		return this.material;
	}

	/**
	 * @return position in 3D space
	 */
	public Vec3D getPosition3D() {
		return this.position3D;
	}

	/**
	 * @return The current selection-status of the connection
	 */
	public boolean isSelected() {
		return this.isSelected;
	}

	/**
	 * @return all edges of the form
	 */
	public List<Edge> getEdges() {
		return this.edges;
	}

	/**
	 * @return all corners of a form
	 */
	public List<Vec2D> getVertices() {
		return this.vertices;
	}

	/**
	 * checks, if this form overlaps with another form
	 * 
	 * @param s checking for overlaps with this form
	 * @return true, if overlaps exist
	 */
	public boolean overlapsWith(GShape s) {
		if (noLineIntersections(this.getVerticesIncludingPosition2D(),
				s.getVerticesIncludingPosition2D())) {
			if (this.containsAtLeastOnePointFromList(s
					.getVerticesIncludingPosition2D())
					|| s.containsAtLeastOnePointFromList(this
							.getVerticesIncludingPosition2D())) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	private boolean noLineIntersections(List<Vec2D> vectors1,
			List<Vec2D> vectors2) {
		List<Line2D> lines1 = createListOfLines(vectors1);
		List<Line2D> lines2 = createListOfLines(vectors2);

		for (Line2D x : lines1) {
			for (Line2D y : lines2) {
				if (x.intersectLine(y)
						.getType()
						.equals(Line2D.LineIntersection.Type
								.valueOf("INTERSECTING"))) {
					return false;
				}
			}
		}
		return true;
	}

	private List<Line2D> createListOfLines(List<Vec2D> vectors) {
		ArrayList<Line2D> lines = new ArrayList<Line2D>();

		for (int i = 0; i < vectors.size() - 1; i++) {
			lines.add(new Line2D(vectors.get(i), vectors.get(i + 1)));
		}
		lines.add(new Line2D(vectors.get(vectors.size() - 1), vectors.get(0)));

		return lines;
	}

	private boolean containsAtLeastOnePointFromList(List<Vec2D> points) {
		Polygon2D thisShape = new Polygon2D(
				this.getVerticesIncludingPosition2D());
		for (Vec2D v : points) {
			if (thisShape.containsPoint(v))
				return true;
		}
		return false;
	}

	private List<Vec2D> getVerticesIncludingPosition2D() {
		ArrayList<Vec2D> vectors = new ArrayList<Vec2D>();
		for (Vec2D v : this.getVertices()) {
			vectors.add(v.add(this.position2D));
		}
		return vectors;
	}

	/*
	 * Correct first point of the outline of an edge by enlarging the first bit of the outline of this edge
	 * and the last bit of the outline of its predecessor and finding the intersection point of this two lines.
	 * 
	 * @param edge which (first) point should be corrected
	 * @return corrected start point for this edge (and the last point of its predecessor)
	 */
	private Vec2D correctIntersection(Edge edge) {
		int index = edges.indexOf(edge);

		index = (index == 0) ? edges.size() - 1 : index - 1;

		Edge edge1 = edges.get(index);
		Edge edge2 = edge;

		Vec2D firstVectorOfEdge1 = edge1.getTenons().get(0);
		Vec2D secondVectorOfEdge1 = edge1.getTenons().get(1);
		Vec2D firstVectorOfEdge2 = edge2.getTenons().get(1);
		Vec2D secondVectorOfEdge2 = edge2.getTenons().get(0);

		Line2D firstTenonOfEdge1 = new Line2D(firstVectorOfEdge1,
				secondVectorOfEdge1);
		firstTenonOfEdge1 = firstTenonOfEdge1.toRay2D()
				.toLine2DWithPointAtDistance(10000);
		Line2D firstTenonOfEdge2 = new Line2D(firstVectorOfEdge2,
				secondVectorOfEdge2);
		firstTenonOfEdge2 = firstTenonOfEdge2.toRay2D()
				.toLine2DWithPointAtDistance(10000);

		if (String.valueOf(
				firstTenonOfEdge1.intersectLine(firstTenonOfEdge2).getType())
				.equals("INTERSECTING")) {
			return firstTenonOfEdge1.intersectLine(firstTenonOfEdge2).getPos();
		}
		return firstVectorOfEdge2;
	}

	/**
	 * @return outline of the form in 2D space
	 */
	public ArrayList<Vec2D> getTenons() {
		ArrayList<Vec2D> allVectors = new ArrayList<Vec2D>();
		for (Edge e : edges) {

			allVectors.add(correctIntersection(e));
			for (int i = 1; i < e.getTenons().size() - 1; i++) {
				allVectors.add(e.getTenons().get(i));
			}

		}
		allVectors.add(allVectors.get(allVectors.size() - 1)); // ??????????????
		// (not needed for normal drawing, but for drawing of the copy -
		// otherwise this last vector is missing
		// (the first shape, which is drawn, appears for a short time in correct
		// form, but then this vector
		// disappears?????
		return allVectors;
	}

	/*
	 * Transform the outline of the form in 2D to a outline in 3D (with an offset in z axis for top and bottom)
	 * by calculating the necessary steps to transform the logical 2D positions of its edges to the logical 3D positions
	 * of its edges and performing the same steps for the 2D outline
	 * 
	 * @param isTop true, if the top (with positive offset of half the thickness in z-direction) should be calculated
	 * @param vectors2D outline of the form in 2D
	 * @return outline of the form in 3D
	 */
	private ArrayList<Vec3D> transformTo3D(boolean isTop,
			ArrayList<Vec2D> vectors2D) {
		// Use the algorithm for connecting two shapes to align logical 2D and
		// 3D view
		// Therefore produce a new GShape with the 2D positions (for the 3D
		// representation)
		//
		GShape helperShape = new GShape(vertices, position3D, this.getShape());

		GShape master = this;
		GShape slave = helperShape;
		Edge masterEdge = this.getEdges().get(0);
		Edge slaveEdge = helperShape.getEdges().get(0);

		Vec3D masterEdgeDirection = masterEdge.getP3D2().sub(
				masterEdge.getP3D1());
		Vec3D slaveEdgeDirection = slaveEdge.getP3D2().sub(slaveEdge.getP3D1());

		float angleBetweenEdges = safeAngleBetween(slaveEdgeDirection,
				masterEdgeDirection);

		Vec3D normalVector = slaveEdgeDirection.cross(masterEdgeDirection)
				.getNormalized();
		while (normalVector.equals(new Vec3D(0, 0, 0))) {
			normalVector = masterEdgeDirection.cross(
					new Vec3D((float) Math.random(), (float) Math.random(),
							(float) Math.random())).getNormalized();
		}
		slave.rotateAroundAxis(normalVector, angleBetweenEdges);

		slaveEdgeDirection = slaveEdge.getP3D2().sub(slaveEdge.getP3D1());

		Vec3D toOrigin = slaveEdge.getP3D1().scale(-1).copy();

		slave.translate3D(toOrigin);

		Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();

		float angleBetweenNormals = calculateAngleBetweenNormals(master, slave);
		slave.rotateAroundAxis(rotationAxis, angleBetweenNormals);

		if (calculateAngleBetweenNormals(master, slave) > 0.001f) {
			slave.rotateAroundAxis(rotationAxis, (float) -2.0
					* angleBetweenNormals);
			angleBetweenNormals = -angleBetweenNormals;
		}

		Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1()).copy();

		// Now we know everything, apply the same Translations to the outline
		// Vec2D array
		// Translated by thickness/2 to top or bottom
		int offsetZ;
		if (isTop) {
			offsetZ = this.getThickness() / 2;
		} else {
			offsetZ = -this.getThickness() / 2;
		}

		ArrayList<Vec3D> vectors3D = new ArrayList<Vec3D>();
		for (Vec2D v : vectors2D) {
			vectors3D.add(v.to3DXY().add(position3D)
					.addSelf(new Vec3D(0, 0, offsetZ)));
		}
		for (int i = 0; i < vectors3D.size(); i++) {
			vectors3D.set(
					i,
					vectors3D.get(i).rotateAroundAxis(normalVector,
							angleBetweenEdges));
			vectors3D.set(i, vectors3D.get(i).addSelf(toOrigin));
			vectors3D.set(
					i,
					vectors3D.get(i).rotateAroundAxis(rotationAxis,
							angleBetweenNormals));
			vectors3D.set(i,
					vectors3D.get(i).addSelf(toMaster).scale(scalingFactor3D));
		}
		return vectors3D;
	}

	/*
	 * Calculates the angle between two Vec3D - the basic angleBetween didn't
	 * return valid values for parallel Vec3D, the two possible cases are
	 * checked - since both vec3D are normalized in our case, an addition of
	 * both values should have a result near zero, if an angle of 180 degree
	 * exists between these two vectors, otherwise the angle is 0 degree.
	 */
	private float safeAngleBetween(Vec3D masterEdgeDirection,
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
	 * Calculate the angle between two GShapes
	 * 
	 * @param master first GShape
	 * @param slave second GShape
	 * @return angle between GShapes(radiant)
	 */
	private float calculateAngleBetweenNormals(GShape master, GShape slave) {
		Vec3D masterNormal = master.getNormalVector();
		Vec3D slaveNormal = slave.getNormalVector();
		return safeAngleBetween(masterNormal, slaveNormal);
	}

	/**
	 * Calculates the perpendicular vector between two points in the 2D space
	 * (the "inwards" direction of the form)
	 * 
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return perpendicular vector
	 */
	public Vec2D get2Dperpendicular(Vec2D v1, Vec2D v2) {
		for (Vec2D v3 : vertices) {
			if (!v3.sub(v1).to3DXY().cross(v2.sub(v1).to3DXY()).isZeroVector()) {
				Vec3D normal = v3.sub(v1).to3DXY().cross(v2.sub(v1).to3DXY());
				return normal.cross((v2.sub(v1)).to3DXY()).invert()
						.add(v1.to3DXY()).normalize().to2DXY();
			}
		}
		return new Vec2D(0, 1);
	}

	/**
	 * Calculates the perpendicular vector between two points in the 3D space
	 * (the "inwards" direction of the form)
	 * 
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return perpendicular vector
	 */
	public Vec3D get3Dperpendicular(Vec3D v1, Vec3D v2) {
		for (Vec3D v3 : vertices3D) {
			if (!v3.sub(v1).cross(v2.sub(v1)).isZeroVector()) {
				Vec3D normal = v3.sub(v1).cross(v2.sub(v1));
				return normal.cross(v2.sub(v1)).invert().add(v1).normalize();
			}
		}
		return new Vec3D(0, 0, 1);
	}

	/**
	 * @return position of the object in 2D space
	 */
	public Vec2D getPosition2D() {
		return this.position2D;
	}

	/**
	 * Changes the material of the object, and updates the outline 
	 * of itself and other connected forms corresponding to the new
	 * (changed) thickness
	 * 
	 * @param material the new material for the form
	 */
	public void setMaterial(Material material) {
		this.material = material;
		if (this.getNumberOfConnections() > 0) {
			for (Edge e : edges) {
				for (Connection c : Connection.getConnections()) {
					if (c.getMasterEdge() == e)
						CreateTenons.createOutlineOfEdge(c.getSlaveEdge(), e);
					else if (c.getSlaveEdge() == e)
						CreateTenons.createOutlineOfEdge(c.getMasterEdge(), e);
				}
			}
		}
	}

	/**
	 * @param selected
	 *            set true, if mouse is over cut-out
	 */
	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	/**
	 * @param scaleFactor scale cut-out for printing
	 */
	public void scale2D(float scaleFactor) {
		this.position2D = this.position2D.scale(scaleFactor);
		ArrayList<Vec2D> newVertices = new ArrayList<Vec2D>();
		for (int i = 0; i < this.vertices.size(); i++) {
			newVertices.add(this.vertices.get(i).scale(scaleFactor));
		}
		this.vertices = newVertices;
		for (int i = 0; i < this.edges.size(); i++) {
			this.edges.get(i).scale2D(scaleFactor);
		}
		for (int i = 0; i < cutouts.size(); i++) {
			cutouts.get(i).scale2D(scaleFactor);
		}
	}

	/**
	 * Change the position of the object in 2D space
	 * 
	 * @param position new position of the form
	 */
	public void setPosition2D(Vec2D position) {
		this.position2D = position;
	}

	/**
	 * 
	 * @param direction how the form should be moved in the 2D space
	 */
	public void translate2D(Vec2D direction) {
		this.position2D.addSelf(direction);
	}

	/**
	 * Rotates the form in the 3D space
	 * 
	 * @param rotationAxis around which the rotation took place
	 * @param theta how much to rotate
	 */
	public void rotateAroundAxis(Vec3D rotationAxis, float theta) {
		for (Vec3D v : vertices3D) {
			v.rotateAroundAxis(rotationAxis, theta);
		}
	}

	/**
	 * @param translationVector how the form should be moved in the 3D space
	 */
	public void translate3D(Vec3D translationVector) {
		for (Vec3D v : vertices3D) {
			v.addSelf(translationVector);
		}
	}

	/**
	 * @return normal-vector of 3D representation of the form
	 */
	public Vec3D getNormalVector() {
		return edges.get(0).getP3D1().sub(edges.get(0).getP3D2())
				.cross(edges.get(1).getP3D1().sub(edges.get(1).getP3D2()))
				.getNormalized();
	}

	/**
	 * Complete 2D drawing - draw cover and edges
	 * 
	 * @param p where to draw
	 */
	public void draw2D(PGraphics p) {
		this.createCover2D(p, getTenons(), position2D);

		for (Edge e : edges) // not good... but i've no better idea
		{
			e.draw2D(p);
		}
	}

	/**
	 * Complete 3D drawing - draw sides, cover and edges
	 * 
	 * @param p where to draw
	 */
	public void draw3D(PGraphics p) {
		if (!this.getMaterial().getMaterialName().equals("Nothing 0,5 mm")) {
			createCover3D(p, true);
			createCover3D(p, false);
			createSides(p, getTenons());
			for (Cutout cutout : cutouts) {
				createSides(p, cutout.getVectors());
			}

			// if we want to show the "Logic" shape:
			/*
			 * p.noFill(); p.stroke(0,0,255); p.beginShape(); for (Edge e :
			 * edges) { p.vertex(e.getP3D1().x(), e.getP3D1().y(),
			 * e.getP3D1().z()); } p.endShape(PConstants.CLOSE);
			 */

			for (Edge e : edges)
			{
				e.draw3D(p);
			}
		}
	}

	/*
	 * 3D drawing of the sides of the form
	 * 
	 * @param p where to draw
	 * @param vectors outline of the form
	 */
	private void createSides(PGraphics p, ArrayList<Vec2D> vectors) {
		this.setFillColor(p);
		ArrayList<Vec3D> top = transformTo3D(true, vectors);
		ArrayList<Vec3D> bottom = transformTo3D(false, vectors);

		for (int i = 0; i < top.size() - 1; i++) {
			p.beginShape();
			p.vertex(top.get(i).x(), top.get(i).y(), top.get(i).z());
			p.vertex(top.get(i + 1).x(), top.get(i + 1).y(), top.get(i + 1).z());
			p.vertex(bottom.get(i + 1).x(), bottom.get(i + 1).y(),
					bottom.get(i + 1).z());
			p.vertex(bottom.get(i).x(), bottom.get(i).y(), bottom.get(i).z());
			p.endShape(PConstants.CLOSE);
		}
		p.beginShape();
		p.vertex(top.get(top.size() - 1).x(), top.get(top.size() - 1).y(), top
				.get(top.size() - 1).z());
		p.vertex(top.get(0).x(), top.get(0).y(), top.get(0).z());
		p.vertex(bottom.get(0).x(), bottom.get(0).y(), bottom.get(0).z());
		p.vertex(bottom.get(top.size() - 1).x(),
				bottom.get(top.size() - 1).y(), bottom.get(top.size() - 1).z());
		p.endShape(PConstants.CLOSE);
	}

	/*
	 * 3D drawing of the top/bottom of the form
	 * 
	 * @param p where to draw
	 * @param isTop true, if it is the top side which should be drawn
	 */
	private void createCover3D(PGraphics p, boolean isTop) {
		this.setFillColor(p);
		p.beginShape();
		for (Vec3D vector : transformTo3D(isTop, getTenons())) {
			p.vertex(vector.x(), vector.y(), vector.z());
		}

		for (Cutout cutout : cutouts) {
			p.beginContour();
			for (Vec3D vector : transformTo3D(isTop, cutout.getVectors())) {
				p.vertex(vector.x(), vector.y(), vector.z());
			}
			p.endContour();
		}
		p.endShape(PConstants.CLOSE);
		p.beginShape();
		for (Cutout cutout : cutouts) {
			if (!cutout.getSlaveShape().getGShape().getMaterial()
					.getMaterialName().equals("Nothing 0,5 mm")) {
				p.fill(cutout.getSlaveShape().getGShape().getMaterial()
						.getMaterialColor());
				for (Vec3D vector : transformTo3D(isTop, cutout.getVectors())) {
					p.vertex(vector.x(), vector.y(), vector.z());
				}
			}
		}
		p.endShape(PConstants.CLOSE);
	}

	/*
	 * 2D drawing of this form
	 * 
	 * @param p where to draw
	 * @param vectors outline of the form
	 * @param position of the form
	 */
	private void createCover2D(PGraphics p, ArrayList<Vec2D> vectors,
			Vec2D position) {
		this.setFillColor(p);
		p.beginShape();
		for (Vec2D vector : getTenons()) {
			Vec2D scaledPosition = vector.add(getPosition2D()).scale(
					scalingFactor);
			p.vertex(scaledPosition.x(), scaledPosition.y());
		}

		for (Cutout cutout : cutouts) {
			p.beginContour();
			for (Vec2D vector : cutout.getVectors()) {
				Vec2D scaledPosition = vector.add(getPosition2D()).scale(
						scalingFactor);
				p.vertex(scaledPosition.x(), scaledPosition.y());
			}
			p.endContour();
		}
		p.endShape(PConstants.CLOSE);
	}

	/*
	 * Set the color for drawing depending on selected material (fill color)
	 * and selection/active state (outline)
	 * 
	 * @param p the corresponding graphic object, for which this should be valid
	 */
	private void setFillColor(PGraphics p) {
		if (this.isSelected()) {
			p.stroke(255, 0, 0);
		} else if (this.isActive) {
			p.stroke(125, 0, 0);
		} else {
			p.stroke(0);
		}
		p.fill(getMaterial().getMaterialColor());
	}

	/**
	 * check if mouse is over cut-out or the corresponding connecting line
	 * 
	 * @param mousePosition the current mouse position
	 * @return true, if mouse is above cut-out/line
	 */
	public boolean mouseOver(Vec2D mousePosition) {
		Polygon2D test = new Polygon2D((List<Vec2D>) vertices);
		return test.containsPoint(mousePosition.sub(position2D));
	}

	/**
	 * Form copy for printing - using the outline vertices as new shape form
	 * (keeps the outline without actually having the connections)
	 * 
	 * @param shape the form to which this form should belong
	 * @return a new instance (to be attached to the corresponding new shape)
	 */
	public GShape copy(Shape shape) {
		GShape copy = new GShape(getTenons(), new Vec3D(position3D), shape);
		copy.setMaterial(this.material);
		copy.setName(this.getName());
		copy.removeAllCutouts();
		for (Cutout c : this.cutouts) {
			copy.addCutout(new GShape(c.getVectors(), new Vec3D(0, 0, 0), shape));
		}
		return copy;
	}

	/**
	 * Copy everything, including cut-outs, to make another identical object
	 * 
	 * @return a copy of this form
	 */
	public Shape copyCompleteStructure() {
		Shape copy = this.getShape().copy();
		copy.getGShape().recalculate(this.vertices);
		copy.getGShape().setMaterial(this.material);
		copy.getGShape().setName("CopyOf" + this.getName());
		copy.getGShape().removeAllCutouts();
		for (Cutout c : this.cutouts) {
			copy.getGShape().addCutout(c.copyFor(copy.getGShape()));
		}
		return copy;
	}

	/**
	 * remove all cut-outs from this shape (clean up for deletion)
	 */
	private void removeAllCutouts() {
		this.cutouts.clear();
	}

	/**
	 * @param cutout add this cut-out to this shape
	 */
	public void addCutout(Cutout cutout) {
		cutouts.add(cutout);
	}

	/**
	 * @return the name of this individual shape
	 */
	public String getName() {
		return name;
	}

	/**
	 * Name (identifier) a shape. Should consists of a descriptive general name of that kind of shape and a individual number for each instance
	 * @param name the name of the shape
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * check, if cut-out is made active (can be manipulated)
	 * 
	 * @return true, if active
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            set true, if mouse is pressed when mouse is over cut-out
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
