package de.mcp.customizer.model;

import java.util.ArrayList;

import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class Trapezium extends Shape
{
	private int sizeXTop, sizeXBottom, sizeY;
	private static int counter = 0;
	private GShape basic;
	private ArrayList <Vec2D> basicShape;

	public Trapezium(Vec3D position, int sizeX, int sizeY)
	{
		this.sizeXTop = sizeX;
		this.sizeXBottom = sizeX;
		this.sizeY = sizeY;
		basicShape = new ArrayList<Vec2D>();
		basicShape.add(new Vec2D(0, 0));
		basicShape.add(new Vec2D(sizeX, 0));
		basicShape.add(new Vec2D(sizeX, sizeY));
		basicShape.add(new Vec2D(0, sizeY));
		basic = new GShape(basicShape, position, this);
		basic.setName("Trapezium " + counter);
		counter++;
	}

	public int getNumberOfControls()
	{
		return 3;
	}

	public int getValue(int index)
	{
		if (index==0) return sizeXTop/10;
		else if (index==1) return sizeXBottom/10;
		else return sizeY/10;
	}
	  
	public int getControlType(int index)
	{
		return 1;
	}

	public String getNameOfControl(int index)
	{
		if (index==0) return "Top";
		else if (index==1) return "Bottom";
		else return "Side";
	}

	public void setValue0(int size)
	{
		sizeXTop = size*10;
		this.recalculate();
	}

	public void setValue1(int size)
	{
		sizeXBottom = size*10;
		this.recalculate();
	}
	
	public void setValue2(int size)
	{
		sizeY = size*10;
		this.recalculate();
	}
	
	public void recalculate()
	{
		basicShape.set(1, new Vec2D(sizeXTop, 0));
		basicShape.set(2, new Vec2D(((float)sizeXTop/2+sizeXBottom/2), (float)(Math.sqrt(Math.pow(sizeY,2) - Math.pow((sizeXBottom-sizeXTop)/2, 2)))));
		basicShape.set(3, new Vec2D(((float)sizeXTop-sizeXBottom)/2, (float)(Math.sqrt(Math.pow(sizeY,2) - Math.pow((sizeXBottom-sizeXTop)/2, 2)))));
		basic.recalculate(basicShape);
	}

	public void setSize(Vec2D newSize)
	{
		this.sizeXTop = (int)newSize.x();
		this.sizeXBottom = (int)newSize.x();
		this.sizeY = (int)newSize.y();
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
		Trapezium copy = new Trapezium(new Vec3D(this.basic.getPosition3D()), this.sizeXTop, this.sizeY);
		copy.setShape(this.basic.copy(copy));
		return copy;

	}
}

