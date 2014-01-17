package de.mcp.customizer.model;

import java.util.ArrayList;
import java.util.List;

import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

//import toxi.geom.Vec3D;

public class PolygonShape extends Shape {
	
	private GShape shape;
	private static int counter = 0;
	
	public PolygonShape(List<Vec2D> vectors, Vec3D position) {
		shape = new GShape(vectors, position, this);
		this.shape.setName("PolygonShape " + counter);
		counter++;
	}
	
	@Override
	public GShape getShape() {
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
		PolygonShape copy = new PolygonShape(this.getShape().getVertices(), this.getShape().getPosition2D().to3DXY());
		copy.setShape(this.shape.copy(copy));
		return copy;
	}
	
	@Override
	public int getNumberOfControls() {
		return 0;
	}

	@Override
	public int getMinValueOfControl(int index) {
		return 0;
	}

	@Override
	public int getMaxValueOfControl(int index) {
		return 0;
	}

	@Override
	public String getNameOfControl(int index) {
		return null;
	}
}
