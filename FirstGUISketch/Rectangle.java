import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import java.util.*;

public class Rectangle extends Shapes
{
  private int sizeX, sizeY;
  private GShape basic;

  public Rectangle(int posX, int posY, int posZ, int sizeX, int sizeY, int thickness)
  {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    ArrayList <Vec2D> rect = new ArrayList<Vec2D>();
    rect.add(new Vec2D(0, 0));
    rect.add(new Vec2D(sizeX, 0));
    rect.add(new Vec2D(sizeX, sizeY));
    rect.add(new Vec2D(0, sizeY));
    Vec2D position2D = new Vec2D(posX, posY);
    Vec3D position3D = new Vec3D(posX, posY, posZ);
    Vec3D angle3D = new Vec3D(0, 0, 0);
    basic = new GShape(rect, position2D, thickness, this);
  }

  public void changeValue(int index, int value)
  {
    if (index==0) setSizeX(value);
    if (index==1) setSizeY(value);
    if (index==2) setThickness(value);
  }

  public int getValue(int index)
  {
    if (index==0) return getSizeX();
    else if (index==1) return getSizeY();
    else if (index==2) return getThickness();
    else return 0;
  }

  public void setSizeX(int size)
  {
    sizeX = size;
    basic.setVector(1, new Vec2D(sizeX, 0));
    basic.setVector(2, new Vec2D(sizeX, sizeY));
  }

  public void setSizeY(int size)
  {
    sizeY = size;
    basic.setVector(2, new Vec2D(sizeX, sizeY));
    basic.setVector(3, new Vec2D(0, sizeY));
  }

  public void setThickness(int size)
  {
    basic.setThickness(size);
  }

  public int getSizeX()
  {
    return sizeX;
  }

  public int getSizeY()
  {
    return sizeY;
  }

  public int getThickness()
  {
    return basic.getThickness();
  }

  public GShape getShape()
  {
    return basic;
  }
}

