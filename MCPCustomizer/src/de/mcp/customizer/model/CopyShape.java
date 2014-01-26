package de.mcp.customizer.model;

import java.util.List;
import toxi.geom.Vec2D;

/**
 * Makes a copy of an existing shape
 */
public class CopyShape extends Shape {
	private List<Vec2D> basicShape;
	private Vec2D position;
	private GShape basic;
	private static int counter = 0;

	/**
	 * @brief Constructor
	 * 
	 *        Creates a copy of an existing shape - Set parameter corresponding
	 *        to the original shape
	 * 
	 * @param list
	 *            list of vertices of the shape, same for copy
	 * @param position
	 *            position of the original shape also as position for the copy
	 *            (update with mouse position)
	 * @param name
	 *            Name of the original shape will be modified with a "Copy"
	 *            string before
	 */
	public CopyShape(List<Vec2D> list, Vec2D position, String name) {
		this.basicShape = list;
		this.position = position;
		this.basic = new GShape(list, position.to3DXY(), this);
		basic.setName("Copy" + counter + "Of" + name);
		counter++;
	}

	/**
	 * @return the corresponding GShape
	 */
	public GShape getShape() {
		return basic;
	}

	/**
	 * Set the corresponding GShape
	 * 
	 * @param the corresponding GShape
	 */
	public void setShape(GShape shape) {
		this.basic = shape;
	}

	/**
	 * Dummy - No Controls
	 */
	public int getValue(int index) {
		return 0;
	}

	/**
	 * Creates a copy of this shape
	 */
	public Shape copy() {
		CopyShape copy = new CopyShape(basicShape, position, basic.getName());
		copy.setShape(this.basic.copy(copy));
		return copy;
	}

	/**
	 * Dummy - No Controls
	 */
	public int getNumberOfControls() {
		return 0;
	}

	/**
	 * Dummy - No Controls
	 */
	public String getNameOfControl(int index) {
		return null;
	}

	/**
	 * Dummy - No Controls
	 */
	public int getControlType(int index) {
		return 0;
	}

}
