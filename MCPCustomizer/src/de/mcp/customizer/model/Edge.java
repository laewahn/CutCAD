package de.mcp.customizer.model;

import java.util.ArrayList;
import de.mcp.customizer.algorithm.CreateTenons;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Drawable3D;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 * Edge object. Has a representation of the (logical) position of its end points
 * in 2D and 3D, and a representation of the (real) outline in 2D
 */
public class Edge implements Drawable2D, Drawable3D {
	private GShape shape; // know parent
	private Vec3D p3D1, p3D2; // 3D logic
	private Vec2D v1, v2; // 2D logic
	private ArrayList<Vec2D> tenons; // 2D representation
	private float scalingFactor = 0.5f;
	private float boundingBoxSize = 4 / scalingFactor;
	private boolean isHighlighted, isLocked, isSelected;
	ArrayList<Vec2D> definingPoints; // highlighting area for selecting an edge

	/**
	 * @brief Constructor
	 * 
	 *        Create a edge - the outline (Arraylist of Vec2D) is automatically
	 *        build (at the beginning just the start- and end-point in 2D)
	 * 
	 * @param shape
	 *            Form to which this edge belong
	 * @param p3D1
	 *            start point in 3D
	 * @param p3D2
	 *            end point in 3D
	 * @param v1
	 *            start point in 2D
	 * @param v2
	 *            end point in 2D
	 */
	public Edge(GShape shape, Vec3D p3D1, Vec3D p3D2, Vec2D v1, Vec2D v2) {
		this.shape = shape;
		this.p3D1 = p3D1;
		this.p3D2 = p3D2;
		this.v1 = v1;
		this.v2 = v2;
		CreateTenons.createOutlineOfEdge(this);
		this.isHighlighted = false;
		this.isSelected = false;
	}

	/**
	 * True if edge already is connected
	 * 
	 * @return
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * Form, to which this edge belong
	 * 
	 * @return
	 */
	public GShape getShape() {
		return shape;
	}

	/**
	 * True if edge should be highlighted
	 * 
	 * @return
	 */
	public boolean isHighlighted() {
		return isHighlighted;
	}

	/**
	 * Set highlighted true, if the edge should be highlighted (e.g. selected as
	 * a first edge for a connection)
	 * 
	 * @param highlighted
	 */
	public void setHighlighted(boolean highlighted) {
		this.isHighlighted = highlighted;
	}

	/**
	 * True, if mouse is over edge
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * Set true if mouse over edge
	 * 
	 * @param isSelected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	/**
	 * Get start point of the edge (in the 2D view)
	 * 
	 * @return
	 */
	public Vec2D getV1() {
		return this.v1;
	}

	/**
	 * Get end point of the edge (in the 2D view)
	 * 
	 * @return
	 */
	public Vec2D getV2() {
		return this.v2;
	}

	/**
	 * Get start point of the edge (in the 3D view)
	 * 
	 * @return
	 */
	public Vec3D getP3D1() {
		return p3D1;
	}

	/**
	 * Get end point of the edge (in the 3D view)
	 * 
	 * @return
	 */
	public Vec3D getP3D2() {
		return p3D2;
	}

	/**
	 * Get outline of the edge as a List of Vec2D
	 * 
	 * @return
	 */
	public ArrayList<Vec2D> getTenons() {
		return tenons;
	}

	/**
	 * Set true, if the edge is connected with another edge
	 * 
	 * @param locked
	 */
	public void setLocked(boolean locked) {
		this.isLocked = locked;
	}

	/**
	 * Set start point of the edge (in the 3D view)
	 * 
	 * @param v
	 */
	public void setP3D1(Vec3D v) {
		this.p3D1 = v;
	}

	/**
	 * Set end point of the edge (in the 3D view)
	 * 
	 * @param v
	 */
	public void setP3D2(Vec3D v) {
		this.p3D2 = v;
	}

	/**
	 * Set start point of the edge (in the 2D view)
	 * 
	 * @param v
	 */
	public void setV1(Vec2D v) {
		this.getV1().set(v);
	}

	/**
	 * Set end point of the edge (in the 2D view)
	 * 
	 * @param v
	 */
	public void setV2(Vec2D v) {
		this.getV2().set(v);
	}

	/**
	 * Set outline of the edge
	 * 
	 * @param tenons
	 *            List of corner points of the outline
	 */
	public void setTenons(ArrayList<Vec2D> tenons) {
		this.tenons = tenons;
	}

	/**
	 * Draw Box around the edge in 2D if the edge should be either highlighted
	 * or selected
	 */
	public void draw2D(PGraphics p) {
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
	public void draw3D(PGraphics p) {
		if (this.isHighlighted()) {
			Vec3D offset = this.getShape().getNormalVector()
					.normalizeTo(this.getShape().getThickness() / 2 + 4);
			p.stroke(255, 0, 0);
			p.noFill();
			p.strokeWeight(2);
			p.beginShape();
			Vec3D vector = p3D1.copy().add(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().add(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().sub(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D1.copy().sub(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			p.endShape(PConstants.CLOSE);
			p.strokeWeight(1);
			p.fill(255);
			p.stroke(0);
		} else if (this.isSelected()) {
			Vec3D offset = this.getShape().getNormalVector()
					.normalizeTo(this.getShape().getThickness() / 2 + 4);
			p.stroke(0, 255, 0);
			p.noFill();
			p.strokeWeight(2);
			p.beginShape();
			Vec3D vector = p3D1.copy().add(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().add(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D2.copy().sub(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			vector = p3D1.copy().sub(offset).scale(scalingFactor);
			p.vertex(vector.x(), vector.y(), vector.z());
			p.endShape(PConstants.CLOSE);
			p.strokeWeight(1);
			p.fill(255);
			p.stroke(0);
		}
	}

	/**
	 * Get mid point of the edge
	 * 
	 * @return
	 */
	public Vec2D getMid() {
		return new Vec2D((this.getV1().x() + this.getV2().x()) / 2, (this
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
	public boolean mouseOver(Vec2D mousePosition) {
		// create a vector that is perpendicular to the edge
		Vec2D perpendicularVector = this.getV2().sub(this.getV1())
				.perpendicular().getNormalizedTo(boundingBoxSize);

		// with the perpendicular vector, calculate the defining points of a
		// rectangle around the edge
		definingPoints = new ArrayList<Vec2D>();
		definingPoints.add(this.getV1().sub(perpendicularVector)
				.add(getShape().getPosition2D()).scale(scalingFactor));
		definingPoints.add(this.getV2().sub(perpendicularVector)
				.add(getShape().getPosition2D()).scale(scalingFactor));
		definingPoints.add(this.getV2().add(perpendicularVector)
				.add(getShape().getPosition2D()).scale(scalingFactor));
		definingPoints.add(this.getV1().add(perpendicularVector)
				.add(getShape().getPosition2D()).scale(scalingFactor));

		// create a rectangle around the edge
		Polygon2D borders = new Polygon2D(definingPoints);

		// check if the mousePointer is within the created rectangle
		return borders.containsPoint(mousePosition.scale(scalingFactor));
	}

	/**
	 * Get length of a edge
	 * 
	 * @return length(0.1mm)
	 */
	public float getLength() {
		return this.getV2().distanceTo(this.getV1());
	}

	/**
	 * scale cut-out for printing
	 * 
	 * @param scaleFactor
	 */
	public void scale2D(float scaleFactor) {
		this.v1 = v1.scale(scaleFactor);
		this.v2 = v2.scale(scaleFactor);
		ArrayList<Vec2D> newTenons = new ArrayList<Vec2D>();
		for (int i = 0; i < tenons.size(); i++) {
			newTenons.add(this.tenons.get(i).scale(scaleFactor));
		}
		this.tenons = newTenons;
	}
}