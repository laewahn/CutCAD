package de.mcp.customizer.model;

import java.util.ArrayList;

import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class Rectangle extends Shape
{
  private int sizeX, sizeY;
  private static int counter = 0;
  private GShape basic;
  private ArrayList <Vec2D> basicShape;

  public Rectangle(Vec3D position, int sizeX, int sizeY)
  {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    basicShape = new ArrayList<Vec2D>();
    basicShape.add(new Vec2D(0, 0));
    basicShape.add(new Vec2D(sizeX, 0));
    basicShape.add(new Vec2D(sizeX, sizeY));
    basicShape.add(new Vec2D(0, sizeY));
    basic = new GShape(basicShape, position, this);
    basic.setName("Rectangle " + counter);
    counter++;
  }
  
  public int getNumberOfControls()
  {
    return 2;
  }

  public int getValue(int index)
  {
    if (index==0) return sizeX/10;
    else return sizeY/10;
  }
  
  public int getControlType(int index)
  {
	  return 1;
  }
  
  public String getNameOfControl(int index)
  {
    if (index==0) return "Width";
    else return "Heigth";
  }
	
  public void recalculate() {
    basicShape.set(1, new Vec2D(sizeX, 0));
    basicShape.set(2, new Vec2D(sizeX, sizeY));
    basicShape.set(3, new Vec2D(0, sizeY));
    basic.recalculate(basicShape);
  }

  public void setValue0(int size)
  {
    this.sizeX = size*10;
    recalculate();
  }

  public void setValue1(int size)
  {
    this.sizeY = size*10;
    recalculate();
  }

  public int getSizeX()
  {
    return sizeX;
  }

  public int getSizeY()
  {
    return sizeY;
  }

  public void setSize(Vec2D newSize)
  {
    this.sizeX = (int)newSize.x();
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
    Rectangle copy = new Rectangle(new Vec3D(this.basic.getPosition3D()), this.sizeX, this.sizeY);
    copy.setShape(this.basic.copy(copy));
    return copy;
    
  }
}

