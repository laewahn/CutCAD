import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

class Edge
{
  private GShape shape; //know parent

  private Vec3D p3D1, p3D2; // 3D logic
  private Vec2D v1, v2; // 2D logic
  private ArrayList<Vec2D> tenons; // 2D representation

  private boolean isSelected, isLocked;
  ArrayList<Vec2D> definingPoints; // highlighting areal for selecting an edge


  public Edge(GShape shape, Vec3D p3D1, Vec3D p3D2, Vec2D v1, Vec2D v2)
  {
    this.shape = shape;
    this.p3D1 = p3D1;
    this.p3D2 = p3D2;
    this.v1 = v1;
    this.v2 = v2;
    new Tenon(this);
    this.isSelected = false;
  }

  public boolean isLocked()
  {
    return isLocked;
  }
  
  public GShape getShape()
  {
    return shape;
  }

  public boolean isSelected()
  {
    return isSelected;
  }

  public void setSelected(boolean selected)
  {
    this.isSelected = selected;
  }

  public Vec2D getV1()
  {
    return this.v1;
  }

  public Vec2D getV2()
  {
    return this.v2;
  }

  public Vec3D getP3D1()
  {
    return p3D1;
  }

  public Vec3D getP3D2()
  {
    return p3D2;
  }

  public ArrayList<Vec2D> getTenons()
  {
    return tenons;
  }

  public void setLocked(boolean locked)
  {
    this.isLocked = locked;
  }

  public void setP3D1(Vec3D v)
  {
    this.p3D1 = v;
  }

  public void setP3D2(Vec3D v)
  {
    this.p3D2 = v;
  }

  public void setV1(Vec2D v)
  {
    this.getV1().set(v);
  }

  public void setV2(Vec2D v)
  {
    this.getV2().set(v);
  }

  public void setTenons(ArrayList<Vec2D> tenons)
  {
    this.tenons = tenons;
  }

  public void drawBox(PGraphics p)
  {
    if (this.isSelected())
    {
      p.stroke(255, 0, 0);
      p.noFill();
      p.strokeWeight(2);
      p.beginShape();
      for (Vec2D vector : definingPoints)
      {
        p.vertex(vector.x()+getShape().getPosition2D().x(), vector.y()+getShape().getPosition2D().y());
      }
      p.endShape(PConstants.CLOSE);
      p.strokeWeight(1);
      p.fill(255);
      p.stroke(0);
    }
  }

  public void drawBox3D(PGraphics p)
  {
    if (this.isSelected())
    {
      Vec3D offset = this.getShape().getNormalVector().normalizeTo(this.getShape().getThickness()/2+4);
      p.stroke(255, 0, 0);
      p.noFill();
      p.strokeWeight(2);
      p.beginShape();
      Vec3D vector = p3D1.copy().add(offset);
      p.vertex(vector.x(), vector.y(), vector.z());
      vector = p3D2.copy().add(offset);
      p.vertex(vector.x(), vector.y(), vector.z());   
      vector = p3D2.copy().sub(offset);
      p.vertex(vector.x(), vector.y(), vector.z());  
      vector = p3D1.copy().sub(offset);
      p.vertex(vector.x(), vector.y(), vector.z());
      p.endShape(PConstants.CLOSE);
      p.strokeWeight(1);
      p.fill(255);
      p.stroke(0);
    }
  }

  public Vec2D getMid()
  {
    return new Vec2D((this.getV1().x() + this.getV2().x()) / 2, (this.getV1().y() + this.getV2().y()) / 2);
  }

  // Checks whether the mousepointer is within a certain area around the edge
  // only checking if the mousepointer is ON the edge would result in bad usability
  // since the user would have to precisely point to a line that is one pixel wide.
  public boolean mouseOver(Vec2D position)
  {
    // create a vector that is perpendicular to the edge
    Vec2D perpendicularVector = this.getV2().sub(this.getV1()).perpendicular();

    // with the perpendicular vector, calculate the defining points of a rectangle around the edge
    definingPoints = new ArrayList<Vec2D>();
    definingPoints.add(this.getV1().sub(perpendicularVector.getNormalizedTo(4)));
    definingPoints.add(this.getV2().sub(perpendicularVector.getNormalizedTo(4)));
    definingPoints.add(this.getV2().add(perpendicularVector.getNormalizedTo(4)));
    definingPoints.add(this.getV1().add(perpendicularVector.getNormalizedTo(4)));

    // create a rectangle around the edge
    Polygon2D borders = new Polygon2D(definingPoints);

    // check if the mousePointer is within the created rectangle
    return borders.containsPoint(position.sub(this.shape.getPosition2D()));
  }

  public boolean mouseOver(int mouseX, int mouseY, int view2DPosX, int view2DPosY)
  {
    Vec2D mousePointer = new Vec2D(mouseX-view2DPosX-shape.getPosition2D().x(), mouseY-view2DPosY-shape.getPosition2D().y());
    return this.mouseOver(mousePointer);
  }
}

