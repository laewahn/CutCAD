package de.mcp.customizer.model.shapes;

import java.util.ArrayList;

import de.mcp.customizer.model.primitives.GShape;
import de.mcp.customizer.model.primitives.Shape;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 * Creates a symmetric polygon as a shape
 */
public class SymmetricPolygon extends Shape {

	private static final long serialVersionUID = 4464413451997462017L;

	private int size, number;
	private static int counter = 0;
	private GShape basic;
	private ArrayList<Vec2D> basicShape;

	/**
	 *        Creates a symmetric Polygon. For the basic shape The basic shape
	 *        has three edges (the minimum). Length of the side is the average
	 *        of sizeX and sizeY (because of mouse dragging). Automatic
	 *        generation of a individual name by a static counter of all
	 *        instances
	 * 
	 * @param position
	 *            Position within the coordinate system (0.1mm)
	 * @param sizeX
	 *            together with
	 * @param sizeY
	 *            determines the length of one side (average) (0.1mm)
	 */
	public SymmetricPolygon(Vec3D position, int sizeX, int sizeY) {
		this.size = (((int) (sizeX + sizeY) / 2) / 10) * 10;
		this.number = 3;
		basicShape = new ArrayList<Vec2D>();
		basicShape.add(new Vec2D(0, 0));
		basicShape.add(new Vec2D(size, 0));
		basicShape.add(new Vec2D(size, size));
		basic = new GShape(basicShape, position, this);
		basic.setName("SymmetricPolygon " + counter);
		counter++;
	}

	/**
	 * Returns the total number of possible controls to change the parameter
	 * (allows properties enable this number of controls)
	 * 
	 * @return Total number of controlled parameters
	 */
	public int getNumberOfControls() {
		return 2;
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
			return size / 10;
		else
			return number;
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
		else
			return 3;
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
			return "Length of edges";
		else
			return "Number of edges";
	}

	/**
	 * Recalculates the vectors of the symmetric polygon based on the input
	 * parameter
	 */
	public void recalculate() {
		if (number < 3)
			number = 3;
		float alpha = 2 * (float) Math.PI / number;
		float beta = ((float) Math.PI - alpha) / 2;
		Vec2D diameter = new Vec2D(
				(float) (size / Math.sin(alpha) * Math.sin(beta)), 0f);
		basicShape.clear();
		for (int i = 0; i < number; i++) {
			basicShape.add((diameter.getRotated(i * alpha)).add(new Vec2D(
					diameter.x(), diameter.y())));
		}
		basic.recalculate(basicShape);
	}

	/**
	 * Change Parameter 1
	 * 
	 * @param size
	 *            Length of a side of the symmetric polygon (0.1mm)
	 */
	public void setValue0(int size) {
		this.size = size * 10;
		recalculate();
	}

	/**
	 * Change Parameter 2
	 * 
	 * @param number
	 *            Number of corners of the symmetric polygon
	 */
	public void setValue1(int number) {
		this.number = number;
		recalculate();
	}

	/**
	 * Changes sizes of the symmetric polygon to a basic form (can't really
	 * translate the mouse-dragged rectangle to a symmetric form - here we just
	 * use the average of the x and y size as length of a side)
	 * 
	 * @param newSize
	 *            average of its coordinates as length (0.1mm)
	 */
	public void setSize(Vec2D newSize) {
		this.size = (((int) ((newSize.x() + newSize.y()) / 2)) / 10) * 10;
		recalculate();
	}

	/**
	 * Set the corresponding GShape
	 * 
	 * @param shape the corresponding GShape
	 */
	public void setShape(GShape shape) {
		this.basic = shape;
	}

	/**
	 * Get the corresponding GShape
	 * 
	 * @return the corresponding GShape
	 */
	public GShape getGShape() {
		return basic;
	}

	/**
	 * Delegates check, if mouse is over this shape to the corresponding GShape
	 * 
	 * @param mousePosition
	 *            position of the mouse
	 * @return true, if mouse is over shape
	 */
	public boolean mouseOver(Vec2D mousePosition) {
		return this.basic.mouseOver(mousePosition);
	}

	/**
	 * Not used
	 * 
	 * @return null
	 */
	public Rect getBoundingBox() {
		return null;
	}

	/**
	 * Creates a copy of this shape
	 */
	public Shape copy() {
		SymmetricPolygon copy = new SymmetricPolygon(new Vec3D(
				this.basic.getPosition3D()), this.size, this.size);
		copy.setShape(this.basic.copy(copy));
		return copy;

	}
	
	/**
	 * Creates a copy of this shape with the basic values
	 */
	public Shape copyBaseForm() {
		SymmetricPolygon copy = new SymmetricPolygon(new Vec3D(
				this.basic.getPosition3D()), this.size, this.size);
		copy.setValue0(this.getValue(0));
		copy.setValue1(this.getValue(1));
		return copy;
	}
}
