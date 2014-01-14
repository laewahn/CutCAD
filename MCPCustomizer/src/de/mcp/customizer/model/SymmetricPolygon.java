package de.mcp.customizer.model;

import java.util.ArrayList;

import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class SymmetricPolygon extends Shape
{
	private int size, number;
	private static int counter = 0;
	private GShape basic;
	private ArrayList <Vec2D> basicShape;

	public SymmetricPolygon(Vec3D position, int sizeX, int sizeY)
	{
		this.size = (sizeX+sizeY)/2;
		this.number=4;
		basicShape = new ArrayList<Vec2D>();
		basicShape.add(new Vec2D(0, 0));
		basicShape.add(new Vec2D(size, 0));
		basicShape.add(new Vec2D(size, size));
		basicShape.add(new Vec2D(0, size));
		basic = new GShape(basicShape, position, this);
		basic.setName("SymmetricPolygon " + counter);
		counter++;
	}

	public int getNumberOfControls()
	{
		return 2;
	}

	public int getValue(int index)
	{
		if (index==0) return size;
		else if (index==1) return number;
		else return 0;
	}

	public int getMinValueOfControl(int index)
	{
		if (index==0) return 5;
		else if (index==1) return 3;
		else return 0;
	}

	public int getMaxValueOfControl(int index)
	{
		if (index==0) return 600;
		else if (index==1) return 16;
		else return 0;
	}

	public String getNameOfControl(int index)
	{
		if (index==0) return "Length of edges";
		else if (index==1) return "Number of edges";
		else return "0";
	}
	
	public void recalculate() {
		float alpha = 2*(float)Math.PI/number;
		float beta = ((float)Math.PI-alpha)/2;
		Vec2D diameter = new Vec2D((float)(size/Math.sin(alpha)*Math.sin(beta)),0f);
		basicShape.clear();
		for(int i=0; i<number; i++)
		{
			basicShape.add((diameter.getRotated(i*alpha)).add(new Vec2D(diameter.x(), diameter.y())));
		}
		basic.recalculate(basicShape);
	}

	public void setValue0(int size)
	{
		this.size = size;
		recalculate();
	}

	public void setValue1(int number)
	{
		this.number = number;
		recalculate();
	}

	public void setSize(Vec2D newSize)
	{
		this.size = (int) ((newSize.x() + newSize.y())/2);
		recalculate();
	}

	public void setShape(GShape shape)
	{
		this.basic = shape;
	}

	public GShape getShape()
	{
		return basic;
	}

	public boolean mouseOver(Vec2D mousePosition) {
		return this.basic.mouseOver(mousePosition);
	}

	public Rect getBoundingBox()
	{
		return null;
	}

	public Shape copy()
	{
		SymmetricPolygon copy = new SymmetricPolygon(new Vec3D(this.basic.getPosition3D()), this.size, this.size);
		copy.setShape(this.basic.copy(copy));
		return copy;

	}
}

