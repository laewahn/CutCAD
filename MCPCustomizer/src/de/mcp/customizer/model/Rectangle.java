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
    if (index==0) return getSizeX();
    else if (index==1) return getSizeY();
    else return 0;
  }
  
  public int getMinValueOfControl(int index)
  {
    if (index==0) return 5;
    else if (index==1) return 5;
    else return 0;
  }
  
  public int getMaxValueOfControl(int index)
  {
    if (index==0) return 600;
    else if (index==1) return 600;
    else return 0;
  }
  
  public String getNameOfControl(int index)
  {
    if (index==0) return "Width";
    else if (index==1) return "Heigth";
    else return "0";
  }

  public void setValue0(int size)
  {
    sizeX = size;
    basicShape.set(1, new Vec2D(size, 0));
    basicShape.set(2, new Vec2D(size, sizeY));
    basic.recalculate(basicShape);
  }

  public void setValue1(int size)
  {
    sizeY = size;
    basicShape.set(2, new Vec2D(sizeX, size));
    basicShape.set(3, new Vec2D(0, size));
    basic.recalculate(basicShape);
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
    this.setValue0((int)newSize.x());
    this.setValue1((int)newSize.y());
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

