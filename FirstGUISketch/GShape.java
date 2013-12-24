import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;
import toxi.math.*;

import java.util.*;
import static java.lang.System.*;

public class GShape
{
  private int thickness;
  //private Material material;
  private Vec2D position2D;
  private boolean isSeleceted;
  private ArrayList<Vec2D> vertices;
  private ArrayList<Edge> edges;
  private ArrayList<Tenon> tenons;
  private Shapes shape;

  public GShape(ArrayList<Vec2D> initVertices, Vec2D p2D, int thickness, Shapes shape)
  {
    vertices = initVertices;
    edges = new ArrayList<Edge>();
    for (int i = 0; i<vertices.size(); i++) 
    {
      edges.add(new Edge(this, vertices.get(i).add(p2D).to3DXY(), vertices.get((i+1)%(vertices.size())).add(p2D).to3DXY(), vertices.get(i), vertices.get((i+1)%(vertices.size()))));
    }

    tenons = new ArrayList<Tenon>();
    for (Edge e : edges)
    {
      tenons.add(new Tenon(e));
    }

    this.position2D = p2D;
    this.thickness = thickness;

    this.isSeleceted = false;
    this.shape = shape;
  }

  public Vec2D getVector(int i) 
  {
    return this.vertices.get(i);
  }

  public int getThickness()
  {
    return this.thickness;
  }

  public void setVector(int i, Vec2D v) 
  {
    this.vertices.set(i, v);
  }

  public boolean isSelected() {
    return this.isSeleceted;
  }

  public ArrayList<Edge> getEdges()
  {
    return this.edges;
  }

  public ArrayList<Vec2D> getVertices()
  {
    return this.vertices;
  }

  public void correctIntersections ()
  {
    for (int i=0; i<tenons.size(); i++) 
    {
      Line2D test1 = tenons.get((i+1)%(tenons.size())).getStartLine(this).toRay2D().toLine2DWithPointAtDistance(10000);
      Line2D test2 = tenons.get(i).getEndLine(this).toRay2D().toLine2DWithPointAtDistance(10000);
      if (String.valueOf(test1.intersectLine(test2).getType()).equals("INTERSECTING")) 
      {
        Vec2D intersection = test1.intersectLine(test2).getPos();
        tenons.get((i+1)%(tenons.size())).correctStartEdge(this, intersection);
        tenons.get(i).correctEndEdge(this, intersection);
      }
    }
  }

  public void setTenon(Edge edge, Tenon tenon) 
  {
    int i = edges.indexOf(edge);
    tenons.set(i, tenon);
  }

  public ArrayList<Vec2D> getTenons()
  {      
    for (int i=0; i<tenons.size(); i++)
    {
      Tenon t = tenons.get(i);
      if (t.isIndependant()) {
        tenons.set(i, new Tenon(edges.get(i)));
      }
    }
    correctIntersections ();
    ArrayList<Vec2D> allVectors = new ArrayList<Vec2D>();
    for (Tenon t : tenons) 
    {
      for (int i=0; i<t.getVectors(this).size(); i++)
      {
        allVectors.add(t.getVectors(this).get(i));
      }
    }
    return allVectors;
  }

  public Vec2D getPosition2D()
  {
    return this.position2D;
  }

  public void setThickness(int thickness)
  {
    this.thickness = thickness;
  }

  public void setSelected(boolean selected) {
    this.isSeleceted = selected;
  }

  public void setPosition2D(Vec2D position)
  {
    this.position2D = position;
  }

  public void translate2D(Vec2D direction)
  {
    this.position2D.addSelf(direction);
  }

  public ArrayList<Vec2D> getResized (int sizeX, int sizeY) 
  {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = 0;
    int maxY = 0;
    for (Vec2D vector : vertices) {
      if ((int)vector.x() > maxX) maxX = (int)vector.x();
      if ((int)vector.x() < minX) minX = (int)vector.x();
      if ((int)vector.y() > maxY) maxY = (int)vector.y();
      if ((int)vector.y() < minY) minY = (int)vector.y();
    }
    ArrayList<Vec2D> resized = new ArrayList<Vec2D>();
    for (Vec2D vector : vertices) {
      resized.add(new Vec2D(vector.x()*sizeX/(maxX-minX), vector.y()*sizeY/(maxY-minY)));
    }
    return resized;
  }

  // drawing routines
  public void drawPreview(PGraphics p, int posX, int posY, int sizeX, int sizeY) 
  {
    ArrayList<Vec2D> toDraw = this.getResized(sizeX, sizeY);
    this.createCover2D(p, toDraw, new Vec2D (posX, posY));
  }


  public void draw2D(PGraphics p) 
  {
    this.createCover2D(p, getTenons(), position2D);
    for (Edge e: edges) //not good... but i've no better idea
    {
      e.drawBox(p);
    }
  }

  public void draw3D(PGraphics p) 
  {
    this.setFillColor(p);
    Vec3D pV = edges.get(0).getP3D1().sub(edges.get(0).getP3D2()).cross(edges.get(1).getP3D1().sub(edges.get(1).getP3D2())).getNormalized().scale(thickness);
    p.beginShape();
    for (Edge e : edges) {
      p.vertex(e.getP3D1().add(pV).x(), e.getP3D1().add(pV).y(), e.getP3D1().add(pV).z());
    }
    p.endShape(PConstants.CLOSE);
    p.beginShape();
    for (Edge e : edges) {
      p.vertex(e.getP3D1().x(), e.getP3D1().y(), e.getP3D1().z());
    }
    p.endShape(PConstants.CLOSE);
    for (Edge e : edges) {
      p.beginShape();
      p.vertex(e.getP3D1().x(), e.getP3D1().y(), e.getP3D1().z());
      p.vertex(e.getP3D2().x(), e.getP3D2().y(), e.getP3D2().z());
      p.vertex(e.getP3D2().add(pV).x(), e.getP3D2().add(pV).y(), e.getP3D2().add(pV).z());
      p.vertex(e.getP3D1().add(pV).x(), e.getP3D1().add(pV).y(), e.getP3D1().add(pV).z());
      p.endShape(PConstants.CLOSE);
    }
  }

  private void setFillColor(PGraphics p) {
    //Todo color dependent on material...
    if (this.isSelected()) {
      p.fill(255, 0, 0);
    } 
    else {
      p.fill(255);
    }
  }

  private void createCover2D(PGraphics p, ArrayList<Vec2D> vectors, Vec2D position) 
  {
    this.setFillColor(p);
    p.beginShape();
    for (Vec2D vector : vectors) {
      p.vertex(vector.x()+position.x(), vector.y()+position.y());
    }
    p.beginContour();
    //ToDo: add additional figures for cut-outs
    p.endContour();
    p.endShape(PConstants.CLOSE);
  }

  public boolean mouseOver(int mouseX, int mouseY, int view2DPosX, int view2DPosY)
  {
    Polygon2D test = new Polygon2D((List)vertices);
    if (test.containsPoint(new Vec2D(mouseX-view2DPosX-position2D.x(), mouseY-view2DPosY-position2D.y())))
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}

