package de.mcp.cutcad.model.shapes;

import java.util.ArrayList;

import de.mcp.cutcad.model.primitives.GShape;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.model.primitives.Vector3D;
import toxi.geom.Rect;

/**
 * Creates a trapezium as a shape
 */
public class Trapezium extends Shape {
	
	private static final long serialVersionUID = 8534996513132236907L;
	
	private int sizeXTop, sizeXBottom, sizeY;
	private static int counter = 0;
	private GShape basic;
	private ArrayList<Vector2D> basicShape;

	/**
	 *        Creates a trapezium. For the basic shape both upper and lower side
	 *        have the same dimension, which result in a rectangle as special
	 *        trapezium. Automatic generation of a individual name by a static counter
	 *            of all instances
	 * 
	 * @param position
	 *            Position within the coordinate system(0.1mm)
	 * @param sizeX
	 *            length of upper and lower side of the trapezium (0.1mm)
	 * @param sizeY
	 *            length of the side of the trapezium (0.1mm)
	 */
	public Trapezium(Vector3D position, int sizeX, int sizeY) {
		this.sizeXTop = (sizeX / 20) * 10;
		this.sizeXBottom = (sizeX / 10) * 10;
		this.sizeY = ((int)(Math.sqrt(Math.pow((sizeY),2)+ Math.pow((sizeXBottom-sizeXTop)/2, 2))))/10*10;
		basicShape = new ArrayList<Vector2D>();
		basicShape.add(new Vector2D(0, 0));
		basicShape.add(new Vector2D(sizeX, 0));
		basicShape.add(new Vector2D(sizeX, sizeY));
		basicShape.add(new Vector2D(0, sizeY));
		basic = new GShape(basicShape, position, this);
		basic.setName("Trapezium " + counter);
		counter++;
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
	 * Sets the name of this shape to name
	 * @param name the name
	 */
	public void setName(String theValue)
	{
		this.getGShape().setName(theValue);
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
			return sizeXTop / 10;
		else if (index == 1)
			return sizeXBottom / 10;
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
	 * @return Type of parameter (0 angle(0..360), 1 position(0..6000), 2 position(-300..300), 3 number(3..16))
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
			return "Top";
		else if (index == 1)
			return "Bottom";
		else
			return "Side";
	}

	/**
	 * Change Parameter 1
	 * 
	 * @param size
	 *            Length of the top of the trapezium (0.1mm)
	 */
	public void setValue0(int size) {
		sizeXTop = size * 10;
		this.recalculate();
	}

	/**
	 * Change Parameter 2
	 * 
	 * @param size
	 *            Length of the bottom of the trapezium (0.1mm)
	 */
	public void setValue1(int size) {
		sizeXBottom = size * 10;
		this.recalculate();
	}

	/**
	 * Change Parameter 3
	 * 
	 * @param size
	 *            Length of the sides of the trapezium (0.1mm)
	 */
	public void setValue2(int size) {
		sizeY = size * 10;
		this.recalculate();
	}

	/**
	 * Recalculates the vectors of the trapezium based on the input parameter
	 */
	public void recalculate() {
		if (Math.abs(sizeXTop-sizeXBottom)/2+1 > sizeY) sizeY = Math.abs(sizeXTop-sizeXBottom)/2+1;
		basicShape.set(1, new Vector2D(sizeXTop, 0));
		basicShape.set(
				2,
				new Vector2D(((float) sizeXTop / 2 + sizeXBottom / 2),
						(float) (Math.sqrt(Math.pow(sizeY, 2)
								- Math.pow((sizeXBottom - sizeXTop) / 2, 2)))));
		basicShape.set(
				3,
				new Vector2D(((float) sizeXTop - sizeXBottom) / 2, (float) (Math
						.sqrt(Math.pow(sizeY, 2)
								- Math.pow((sizeXBottom - sizeXTop) / 2, 2)))));
		basic.recalculate(basicShape);
	}

	/**
	 * Changes sizes of the trapezium to a basic rectangle form with
	 * 
	 * @param newSize
	 *            Length and Height of the trapezium (0.1mm)
	 */
	public void setSize(Vector2D newSize) {
		this.sizeXTop = ((int) newSize.x() / 20) * 10;
		this.sizeXBottom = ((int) newSize.x() / 10) * 10;
		this.sizeY = ((int)(Math.sqrt(Math.pow(((int) newSize.y()),2)+ Math.pow((sizeXBottom-sizeXTop)/2, 2))))/10*10;
		recalculate();
	}

	/**
	 * Set the corresponding GShape
	 * 
	 * @param gshape the corresponding GShape
	 */
	public void setShape(GShape gshape) {
		this.basic = gshape;
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
		Trapezium copy = new Trapezium(new Vector3D(this.basic.getPosition3D()),
				this.sizeXTop, this.sizeY);
		copy.setShape(this.basic.copy(copy));
		return copy;
	}
	
	/**
	 * Creates a copy of this shape with the basic values
	 */
	public Shape copyBaseForm() {
		Trapezium copy = new Trapezium(new Vector3D(
				this.basic.getPosition3D()), this.sizeXTop, this.sizeY);
		copy.setValue0(this.getValue(0));
		copy.setValue1(this.getValue(1));
		copy.setValue2(this.getValue(2));
		return copy;
	}
}
