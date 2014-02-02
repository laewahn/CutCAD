package de.mcp.customizer.model.shapes;

import java.util.List;

import de.mcp.customizer.model.primitives.GShape;
import de.mcp.customizer.model.primitives.Shape;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class PolygonShape extends Shape {
	
	private static final long serialVersionUID = 1677103755318526089L;
	
	private GShape shape;
	private static int counter = 0;
	
	public PolygonShape(List<Vec2D> vectors, Vec3D position) {
		shape = new GShape(vectors, position, this);
		this.shape.setName("PolygonShape " + counter);
		counter++;
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
