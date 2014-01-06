import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import java.util.*;

public class Rectangle extends Shape implements Drawable2D, Drawable3D, Selectable
{
  private int sizeX, sizeY;
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
    Vec3D angle3D = new Vec3D(0, 0, 0);
    basic = new GShape(basicShape, position, this);
  }

  public void changeValue(int index, int value)
  {
    if (index==0) setSizeX(value);
    if (index==1) setSizeY(value);
  }

  public int getValue(int index)
  {
    if (index==0) return getSizeX();
    else if (index==1) return getSizeY();
    else return 0;
  }

  public void setSizeX(int size)
  {
    sizeX = size;
    basicShape.set(1, new Vec2D(size, 0));
    basicShape.set(2, new Vec2D(size, sizeY));
    basic.recalculate(basicShape);
  }

  public void setSizeY(int size)
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
    this.setSizeX((int)newSize.x());
    this.setSizeY((int)newSize.y());
  }
  
  public void setShape(GShape shape)
  {
    this.basic = shape;
  }

  public GShape getShape()
  {
    return basic;
  }

  public void draw2D(PGraphics p) {
    this.basic.draw2D(p);
  }

  public void draw3D(PGraphics p) {
    this.basic.draw3D(p);
  }

  public boolean mouseOver(Vec2D mousePosition) {
    return this.basic.mouseOver(mousePosition);
  }

  public Rect getBoundingBox()
  {
    return null;
  }
}

