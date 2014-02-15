package de.mcp.customizer.model.primitives;

import java.io.Serializable;
import java.util.ArrayList;

import de.mcp.customizer.application.Pluggable;
import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;
import toxi.geom.*;

/**
 * Connects two shapes such that the second one forms a cut-out in the first one
 */
public class Cutout implements Drawable2D, Serializable, Pluggable {
	
	private static final long serialVersionUID = -3035265244890031332L;
	
	private Vector2D position;
	private float angle;
	private GShape master, slave;
	private boolean isSelected, isActive;
	private float scalingFactor, boundingBoxSize;

	/**
	 *        Creates a new Cut-out object, where both shapes are stored and a
	 *        new position and angle is created for the positioning of the
	 *        cut-out form on the master shape. The material of the cut-out
	 *        shape is set to Nothing (could be of course reassigned to another,
	 *        if some kind of inlay is wanted)
	 * 
	 * @param master
	 *            The shape, where a cut-out should appear
	 * @param slave
	 *            The shape, which is used as a cut-out form
	 */
	public Cutout(GShape master, GShape slave) {
		this.master = master;
		this.slave = slave;
		slave.setMaterial(AllMaterials.getMaterials().get(0));
		this.angle = 0;
		if (!master.overlapsWith(slave)) {
			this.position = new Vector2D(findCenter(master).sub(findCenter(slave)));
		} else {
			this.position = slave.getPosition2D().sub(master.getPosition2D());
		}
		this.isSelected = false;
		this.isActive = false;
	}

	/**
	 * Move the cut-out at another position
	 * 
	 * @param direction
	 *            offset to add (0.1mm)
	 */
	public void translate2D(Vector2D direction) {
		this.position.addSelf(direction);
	}

	/**
	 * Makes a copy of a cut-out (for printing)
	 * 
	 * @param newMaster
	 *            The shape, to which the copy belong (where the copy is placed)
	 * @return copy of the cut-out object
	 */
	public Cutout copyFor(GShape newMaster) {
		Cutout copy = new Cutout(newMaster, this.slave);
		copy.angle = this.angle;
		copy.position.set(this.position);
		this.isSelected = false;
		this.isActive = false;
		return copy;
	}

	/**
	 * @return the shape, where the cut-out is placed
	 */
	public Shape getMasterShape() {
		return this.master.getShape();
	}

	/**
	 * @return the shape, which is used as a cut-out form
	 */
	public Shape getSlaveShape() {
		return this.slave.getShape();
	}

	/**
	 * @param b
	 *            set true, if mouse is over cut-out
	 */
	public void setSelected(boolean b) {
		this.isSelected = b;
	}

	/**
	 * @return true, if mouse over cut-out
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @return List of Vector2D, which determine the form the cut-out (modified by angle and position)
	 */
	public ArrayList<Vector2D> getVectors() {
		ArrayList<Vector2D> modifiedVectors = new ArrayList<Vector2D>();

		Polygon2D findCenter = new Polygon2D();
		for (Vector2D v : slave.getTenons())
			findCenter.add(v.copy().getVec2D());
		Vector2D center = new Vector2D(findCenter.getCentroid());
		for (Vector2D v : slave.getTenons()) {
			modifiedVectors.add(v.copy().sub(center)
					.rotate((float) Math.toRadians(angle)).add(position)
					.add(center));
		}
		return modifiedVectors;
	}

	/**
	 * Returns the total number of possible controls to change the parameter
	 * (allows properties enable this number of controls)
	 * 
	 * @return Total number of controlled parameters
	 */
	public int getNumberOfControls() {
		return 3;
	}

	/**
	 * Returns the actual value for the corresponding parameter
	 * 
	 * @param index
	 *            The number of the controlled parameter
	 * 
	 * @return value of parameter
	 */
	public int getValue(int index) {
		if (index == 0)
			return (int) position.x() / 10;
		else if (index == 1)
			return (int) position.y() / 10;
		else
            return (int) angle;
	}

	/**
	 * Returns the type of control for the parameter. This selects different
	 * units and maximal/minimal values for the different parameters
	 * 
	 * @param index
	 *            The number of the controlled parameter
	 * 
	 * @return Type of parameter (0 angle(0..360), 1 position(0..6000), 2
	 *         position(-300..300), 3 number(3..16))
	 */
	public int getControlType(int index) {
		if (index == 0)
			return 1;
		else if (index == 1)
			return 1;
		else
			return 0;
	}

	/**
	 * Returns the name (label) for the control for the parameter
	 * 
	 * @param index
	 *            The number of the controlled parameter
	 * 
	 * @return Label of the parameter
	 */
	public String getNameOfControl(int index) {
		if (index == 0)
			return "X-Position";
		else if (index == 1)
			return "Y-Position";
		else
			return "Angle";
	}

	/**
	 * Change Parameter 1
	 * 
	 * @param size
	 *            X-Position(0.1mm)
	 */
	public void setValue0(int size) {
		this.position.set(size * 10, this.position.y());
	}

	/**
	 * Change Parameter 2
	 * 
	 * @param size
	 *            Y-Position(0.1mm)
	 */
	public void setValue1(int size) {
		this.position.set(this.position.x(), size * 10);
	}

	/**
	 * Change Parameter 3
	 * 
	 * @param angle
	 *            Angle(degree)
	 */
	public void setValue2(int angle) {
        this.angle = angle;	
        }

	
	/**
	 * Draw cut-out line
	 */
	public void draw2D(PGraphics p, Transformation t) {
		scalingFactor = t.getScale();
		boundingBoxSize = 4 / scalingFactor;

		Vector2D mid1 = findCenter(slave).add(master.getPosition2D())
				.add(this.position).scale(scalingFactor);
		Vector2D mid2 = findCenter(slave).add(slave.getPosition2D()).scale(
				scalingFactor);
		if (this.isSelected) {
			p.stroke(255, 0, 0);
		} else if (this.isActive) {
			p.stroke(125, 0, 0);
		} else {
			p.stroke(0, 60, 0);
		}
		p.line(mid1.x(), mid1.y(), mid2.x(), mid2.y());
		p.stroke(0);
	}

	/**
	 * find the (mass)center of a shape
	 * 
	 * @param shape
	 *            A GShape
	 * @return the position of the mass center
	 */
	public Vector2D findCenter(GShape shape) {
		Polygon2D findCenter = new Polygon2D();
		for (Edge e : shape.getEdges())
			findCenter.add(e.getV1().copy().getVec2D());
		Vector2D center = new Vector2D(findCenter.getCentroid());
		return center;
	}

	/**
	 * check if mouse is over cut-out or the corresponding connecting line
	 * 
	 * @param mousePosition the current position of the mouse
	 * @return true, if mouse is above cut-out/line
	 */
	public boolean mouseOver(Vector2D mousePosition) {
		Vector2D mid1 = findCenter(slave).add(master.getPosition2D()).add(
				this.position);
		Vector2D mid2 = findCenter(slave).add(slave.getPosition2D());

		// create a vector that is perpendicular to the connections line
		Vector2D perpendicularVector = mid1.sub(mid2).perpendicular()
				.getNormalizedTo(boundingBoxSize);

		// with the perpendicular vector, calculate the defining points of a
		// rectangle around the connections line
		ArrayList<Vec2D> definingPoints = new ArrayList<Vec2D>();
		for (Vector2D v : getVectors()) {
			definingPoints.add(v.add(master.getPosition2D()).getVec2D());
		}

		ArrayList<Vec2D> definingPointsLine = new ArrayList<Vec2D>();
		definingPointsLine.add(mid1.sub(perpendicularVector).scale(
				scalingFactor).getVec2D());
		definingPointsLine.add(mid2.sub(perpendicularVector).scale(
				scalingFactor).getVec2D());
		definingPointsLine.add(mid2.add(perpendicularVector).scale(
				scalingFactor).getVec2D());
		definingPointsLine.add(mid1.add(perpendicularVector).scale(
				scalingFactor).getVec2D());

		// create a rectangle around the edge
		Polygon2D borders = new Polygon2D(definingPoints);
		if (borders.containsPoint(mousePosition.getVec2D())) {
			return true;
		} else {
			borders = new Polygon2D(definingPointsLine);
			// check if the mousePointer is within the created rectangle
			return borders.containsPoint(mousePosition.scale(scalingFactor).getVec2D());
		}
	}

	/**
	 * remove cut-out from a shape
	 */
	public void removeCutout() {
		master.removeCutout(this);
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

	/**
	 * @param scaleFactor scale cut-out for printing
	 */
	public void scale2D(float scaleFactor) {
		this.slave.scale2D(scaleFactor);
	}
}
