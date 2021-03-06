package de.mcp.cutcad.model.shapes;

import java.util.List;

import de.mcp.cutcad.model.primitives.GShape;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.model.primitives.Vector3D;

public class PolygonShape extends Shape {
	
	private static final long serialVersionUID = 1677103755318526089L;
	
	private GShape shape;
	private static int counter = 0;
	
	public PolygonShape(List<Vector2D> vectors, Vector3D position) {
		shape = new GShape(vectors, position, this);
		this.shape.setName("PolygonShape " + counter);
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
	
	@Override
	public GShape getGShape() {
		return this.shape;
	}

	@Override
	public int getValue(int index) {
		return 0;
	}
	
	public void setShape(GShape shape)
	{
		this.shape = shape;
	}

	@Override
	public Shape copy() 
	{
		PolygonShape copy = new PolygonShape(this.getGShape().getVertices(), this.getGShape().getPosition2D().to3DXY());
		copy.setShape(this.shape.copy(copy));
		return copy;
	}
	
	@Override
	public int getNumberOfControls() {
		return 0;
	}

	@Override
	public int getControlType(int index)
	{
		return 0; 
	}

	@Override
	public String getNameOfControl(int index) {
		return null;
	}
	
	/**
	 * Dummy - Nothing to recalculate
	 */
	public void recalculate() {
	}

	@Override
	public Shape copyBaseForm() {
		PolygonShape copy = new PolygonShape(this.getGShape().getVertices(), this.getGShape().getPosition2D().to3DXY());
		return copy;
	}
}
