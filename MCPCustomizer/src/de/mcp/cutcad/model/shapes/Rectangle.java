package de.mcp.cutcad.model.shapes;

import java.io.Serializable;
import java.util.ArrayList;

import de.mcp.cutcad.model.primitives.GShape;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.model.primitives.Vector3D;
import toxi.geom.Rect;

/**
 * Creates a rectangle as a shape
 */
public class Rectangle extends Shape implements Serializable {
	
	private static final long serialVersionUID = -6511783713655746647L;
	
	private int sizeX, sizeY;
	private static int counter = 0;
	private GShape basic;
	private ArrayList<Vector2D> basicShape;

	/**
	 *        Creates a rectangle. Automatic generation of a individual name by
	 *        a static counter of all instances
	 * 
	 * @param position
	 *            Position within the coordinate system
	 * @param sizeX
	 *            Width of the rectangle (0.1mm)
	 * @param sizeY
	 *            Length of the rectangle (0.1mm)
	 */
	public Rectangle(Vector3D position, int sizeX, int sizeY) {
		this.sizeX = (sizeX / 10) * 10;
		this.sizeY = (sizeY / 10) * 10;
		basicShape = new ArrayList<Vector2D>();
		basicShape.add(new Vector2D(0, 0));
		basicShape.add(new Vector2D(sizeX, 0));
		basicShape.add(new Vector2D(sizeX, sizeY));
		basicShape.add(new Vector2D(0, sizeY));
		basic = new GShape(basicShape, position, this);
		basic.setName("Rectangle " + counter);
		counter++;
	}
	
	
	/**
	 * Sets the name of this shape to name
	 * @param name the name
	 */
	public void setName(String theValue)
	{
		this.getGShape().setName(theValue);
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
			return sizeX / 10;
		else
			return sizeY / 10;
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
		return 1;
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
			return "Width";
		else
			return "Heigth";
	}

	/**
	 * Recalculates the vectors of the rectangle based on the input parameter
	 */
	public void recalculate() {
		basicShape.set(1, new Vector2D(sizeX, 0));
		basicShape.set(2, new Vector2D(sizeX, sizeY));
		basicShape.set(3, new Vector2D(0, sizeY));
		basic.recalculate(basicShape);
	}

	/**
	 * Change Parameter 1
	 * 
	 * @param size
	 *            Width of the rectangle (0.1mm)
	 */
	public void setValue0(int size) {
		this.sizeX = size * 10;
		recalculate();
	}

	/**
	 * Change Parameter 2
	 * 
	 * @param size
	 *            Height of the rectangle (0.1mm)
	 */
	public void setValue1(int size) {
		this.sizeY = size * 10;
		recalculate();
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	/**
	 * Changes sizes of the rectangle
	 * 
	 * @param newSize
	 *            Length and Height of the rectangle (0.1mm)
	 */
	public void setSize(Vector2D newSize) {
		this.sizeX = ((int) newSize.x() / 10) * 10;
		this.sizeY = ((int) newSize.y() / 10) * 10;
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
	public boolean mouseOver(Vector2D mousePosition) {
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
		Rectangle copy = new Rectangle(new Vector3D(this.basic.getPosition3D()),
				this.sizeX, this.sizeY);
		copy.setShape(this.basic.copy(copy));
		return copy;
	}
	
	/**
	 * Creates a copy of this shape with the basic values
	 */
	public Shape copyBaseForm() {
		Rectangle copy = new Rectangle(new Vector3D(
				this.basic.getPosition3D()), this.sizeX, this.sizeY);
		copy.setValue0(this.getValue(0));
		copy.setValue1(this.getValue(1));
		return copy;
	}
}
