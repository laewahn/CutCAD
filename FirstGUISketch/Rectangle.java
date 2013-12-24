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
    basic = new GShape(rect, position2D, position3D, thickness, this);
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
    recalculateEdges();
  }

  public void setSizeY(int size)
  {
    sizeY = size;
    recalculateEdges();
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
  
  private void recalculateEdges()
  {
    //not necessary: Vec2D vector0 = new Vec2D(0, 0);
    Vec2D vector1 = new Vec2D(sizeX, 0);
    Vec2D vector2 = new Vec2D(sizeX, sizeY);
    Vec2D vector3 = new Vec2D(0, sizeY);
    
    // Vertices Shape
    //not necessary: basic.setVector(0, vector0);
    basic.setVector(1, vector1);
    basic.setVector(2, vector2);
    basic.setVector(3, vector3);
    
    // Vertices Edge 2D
    //not necessary: basic.getEdges().get(0).setV1(vector0);
    basic.getEdges().get(0).setV2(vector1);
    basic.getEdges().get(1).setV1(vector1);
    basic.getEdges().get(1).setV2(vector2);
    basic.getEdges().get(2).setV1(vector2);
    basic.getEdges().get(2).setV2(vector3);
    basic.getEdges().get(3).setV1(vector3);
    //not necessary: basic.getEdges().get(3).setV2(vector0);
    
    float posX = basic.getPosition3D().x();
    float posY = basic.getPosition3D().y();
    float posZ = basic.getPosition3D().z();
    Vec3D vector3D0 = new Vec3D(posX, posY, posZ);
    Vec3D vector3D1 = new Vec3D(sizeX+posX, posY, posZ);
    Vec3D vector3D2 = new Vec3D(sizeX+posX, sizeY+posY, posZ);
    Vec3D vector3D3 = new Vec3D(posX, sizeY+posY, posZ);
    
    // Vertices Edge 3D
    basic.getEdges().get(0).setP3D1(vector3D0);
    basic.getEdges().get(0).setP3D2(vector3D1);
    basic.getEdges().get(1).setP3D1(vector3D1);
    basic.getEdges().get(1).setP3D2(vector3D2);
    basic.getEdges().get(2).setP3D1(vector3D2);
    basic.getEdges().get(2).setP3D2(vector3D3);
    basic.getEdges().get(3).setP3D1(vector3D3);
    basic.getEdges().get(3).setP3D2(vector3D0);
  }
}

