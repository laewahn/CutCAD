package de.mcp.cutcad.model.primitives;

import java.io.Serializable;
import java.util.ArrayList;

import de.mcp.cutcad.algorithm.CreateTenons;
import de.mcp.cutcad.view.Drawable2D;
import de.mcp.cutcad.view.Drawable3D;
import de.mcp.cutcad.view.Transformation;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;

/**
 * Edge object. Has a representation of the (logical) position of its end points
 * in 2D and 3D, and a representation of the (real) outline in 2D
 */
public class Edge implements Drawable2D, Drawable3D, Serializable {
	
	private static final long serialVersionUID = 924861921334437216L;
	
	private GShape gShape; // know parent
	private Vector3D p3D1, p3D2; // 3D logic
	private Vector2D v1, v2; // 2D logic
	private ArrayList<Vector2D> tenons; // 2D representation
	private float scalingFactor, scalingFactor3D, boundingBoxSize;
	private boolean isHighlighted, isLocked, isSelected;
	transient ArrayList<Vec2D> definingPoints; // highlighting area for selecting an edge

	/**
	 *        Create a edge - the outline (Arraylist of Vector2D) is automatically
	 *        build (at the beginning just the start- and end-point in 2D)
	 * 
	 * @param shape
	 *            Form to which this edge belong
	 * @param p3D1
	 *            start point in 3D (0.1mm)
	 * @param p3D2
	 *            end point in 3D (0.1mm)
	 * @param v1
	 *            start point in 2D (0.1mm)
	 * @param v2
	 *            end point in 2D (0.1mm)
	 */
	public Edge(GShape gShape, Vector3D p3D1, Vector3D p3D2, Vector2D v1, Vector2D v2) {
		this.gShape = gShape;
		this.p3D1 = p3D1;
		this.p3D2 = p3D2;
		this.v1 = v1;
		this.v2 = v2;
		CreateTenons.createOutlineOfEdge(this);
		this.isHighlighted = false;
		this.isSelected = false;
	}

	/**
	 * @return true if edge already is connected
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * @return Form, to which this edge belong
	 */
	public GShape getGShape() {
		return gShape;
	}

	/**
	 * @return true if edge should be highlighted
	 */
	public boolean isHighlighted() {
		return isHighlighted;
	}

	/**
	 * @param highlighted set true, if the edge should be highlighted (e.g. selected as
	 * a first edge for a connection)
	 */
	public void setHighlighted(boolean highlighted) {
		this.isHighlighted = highlighted;
	}

	/**
	 * @return true, if mouse is over edge
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected set true if mouse over edge
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	/**
	 * @return Get start point of the edge (in the 2D view) (0.1mm)
	 */
	public Vector2D getV1() {
		return this.v1;
	}

	/**
	 * @return get end point of the edge (in the 2D view)
	 */
	public Vector2D getV2() {
		return this.v2;
	}

	/**
	 * @return get start point of the edge (in the 3D view)
	 */
	public Vector3D getP3D1() {
		return p3D1;
	}

	/**
	 * @return get end point of the edge (in the 3D view)
	 */
	public Vector3D getP3D2() {
		return p3D2;
	}

	/**
	 * @return get outline of the edge as a List of Vector2D
	 */
	public ArrayList<Vector2D> getTenons() {
		return tenons;
	}

	/**
	 * @param locked set true, if the edge is connected with another edge
	 */
	public void setLocked(boolean locked) {
		this.isLocked = locked;
	}

	/**
	 * @param v set start point of the edge (in the 3D view)
	 */
	public void setP3D1(Vector3D v) {
		this.p3D1 = v;
	}

	/**
	 * @param v set end point of the edge (in the 3D view)
	 */
	public void setP3D2(Vector3D v) {
		this.p3D2 = v;
	}

	/**
	 * @param v set start point of the edge (in the 2D view)
	 */
	public void setV1(Vector2D v) {
		this.getV1().set(v);
	}

	/**
	 * @param v set end point of the edge (in the 2D view)
	 */
	public void setV2(Vector2D v) {
		this.getV2().set(v);
	}

	/**
	 * Set outline of the edge
	 * 
	 * @param tenons
	 *            List of corner points of the outline
	 */
	public void setTenons(ArrayList<Vector2D> tenons) {
		this.tenons = tenons;
	}

	/**
	 * Draw Box around the edge in 2D if the edge should be either highlighted
	 * or selected
	 */
	public void draw2D(PGraphics p, Transformation t) {
		scalingFactor = t.getScale();
		boundingBoxSize = 4 / scalingFactor;
		
		if (this.isHighlighted()) {
			p.stroke(255, 0, 0);
			p.noFill();
			p.strokeWeight(2);
			p.beginShape();
			for (Vec2D vector : definingPoints) {
				p.vertex(vector.x(), vector.y());
			}
			p.endShape(PConstants.CLOSE);
			p.strokeWeight(1);
			p.fill(255);
			p.stroke(0);
		} else if (this.isSelected()) {
			p.stroke(0, 255, 0);
			p.noFill();
			p.strokeWeight(2);
			p.beginShape();
			for (Vec2D vector : definingPoints) {
				p.vertex(vector.x(), vector.y());
			}
			p.endShape(PConstants.CLOSE);
			p.strokeWeight(1);
			p.fill(255);
			p.stroke(0);
		}
	}

	/**
	 * Draw Box around the edge in 3D if the edge should be either highlighted
	 * or selected
	 */
	public void draw3D(PGraphics p, Transformation t) {
		scalingFactor3D = t.getScale();
		createBorderBox();
		if (this.isHighlighted()) {
			Vector3D offset = this.getGShape().getNormalVector()
					.normalizeTo(this.getGShape().getThickness() / 2 + 4);
			p.stroke(255, 0, 0);
			p.noFill();
			p.strokeWeight(2);
			p.beginShape();
			Vector3D vector = p3D1.copy().add(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().add(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().sub(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D1.copy().sub(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			p.endShape(PConstants.CLOSE);
			p.strokeWeight(1);
			p.fill(255);
			p.stroke(0);
		} else if (this.isSelected()) {
			Vector3D offset = this.getGShape().getNormalVector()
					.normalizeTo(this.getGShape().getThickness() / 2 + 4);
			p.stroke(0, 255, 0);
			p.noFill();
			p.strokeWeight(2);
			p.beginShape();
			Vector3D vector = p3D1.copy().add(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().add(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().sub(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D1.copy().sub(offset).scale(scalingFactor3D);
			p.vertex(vector.x(), vector.y(), vector.z());
			p.endShape(PConstants.CLOSE);
			p.strokeWeight(1);
			p.fill(255);
			p.stroke(0);
		}
	}

	/**
	 * @return mid point of the edge
	 */
	public Vector2D getMid() {
		return new Vector2D((this.getV1().x() + this.getV2().x()) / 2, (this
				.getV1().y() + this.getV2().y()) / 2);
	}

	/**
	 * Checks, if the mouse pointer is within a certain area around the edge
	 * 
	 * @param mousePosition
	 *            mouse position to check
	 * @return true, if mouse is over edge
	 */
	// Checks whether the mousepointer is within a certain area around the edge
	// only checking if the mousepointer is ON the edge would result in bad
	// usability
	// since the user would have to precisely point to a line that is one pixel
	// wide.
	public boolean mouseOver(Vector2D mousePosition) {
		createBorderBox();
		// create a rectangle around the edge
		Polygon2D borders = new Polygon2D(definingPoints);

		// check if the mousePointer is within the created rectangle
		return borders.containsPoint(mousePosition.scale(scalingFactor).getVec2D());
	}
	
	private void createBorderBox() {
		Vector2D perpendicularVector = this.getV2().sub(this.getV1())
				.perpendicular().getNormalizedTo(boundingBoxSize);
		definingPoints = new ArrayList<Vec2D>();
		definingPoints.add(this.getV1().sub(perpendicularVector)
				.add(getGShape().getPosition2D()).scale(scalingFactor).getVec2D());
		definingPoints.add(this.getV2().sub(perpendicularVector)
				.add(getGShape().getPosition2D()).scale(scalingFactor).getVec2D());
		definingPoints.add(this.getV2().add(perpendicularVector)
				.add(getGShape().getPosition2D()).scale(scalingFactor).getVec2D());
		definingPoints.add(this.getV1().add(perpendicularVector)
				.add(getGShape().getPosition2D()).scale(scalingFactor).getVec2D());	
	}

	/**
	 * @return length of an edge (0.1mm)
	 */
	public float getLength() {
		return this.getV2().distanceTo(this.getV1());
	}

	/**
	 * @param scaleFactor scale cut-out for printing
	 */
	public void scale2D(float scaleFactor) {
		this.v1 = v1.scale(scaleFactor);
		this.v2 = v2.scale(scaleFactor);
		ArrayList<Vector2D> newTenons = new ArrayList<Vector2D>();
		for (int i = 0; i < tenons.size(); i++) {
			newTenons.add(this.tenons.get(i).scale(scaleFactor));
		}
		this.tenons = newTenons;
	}
}