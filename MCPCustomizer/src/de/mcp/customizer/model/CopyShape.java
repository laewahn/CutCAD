package de.mcp.customizer.model;

//import java.util.ArrayList;
import java.util.List;

import toxi.geom.Vec2D;
//import toxi.geom.Vec3D;

public class CopyShape extends Shape {
	private List <Vec2D> basicShape;
	private Vec2D position;
	private GShape basic;
	private static int counter = 0;

	public CopyShape(List<Vec2D> list, Vec2D position, String name)
	{
		this.basicShape = list;
		this.position = position;
		this.basic = new GShape(list, position.to3DXY(), this);
		basic.setName("Copy" + counter + "Of" + name);
		counter++;
	}

	@Override
	public GShape getShape() {
		return basic;
	}
	
	public void setShape(GShape shape)
	{
		this.basic = shape;
	}

	@Override
	public int getValue(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Shape copy()
	{
		CopyShape copy = new CopyShape(basicShape, position, basic.getName());
		copy.setShape(this.basic.copy(copy));
		return copy;
	}

	@Override
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinValueOfControl(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxValueOfControl(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNameOfControl(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
