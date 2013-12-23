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
    private Vec3D position3D, angle3D;
    private boolean isSeleceted;
    private ArrayList<Vec2D> vertices;
    private ArrayList<Edge> edges;
    private ArrayList<Tenon> tenons;
    private Shapes shape;

    public GShape(ArrayList<Vec2D> initVertices, Vec2D p2D, Vec3D p3D, Vec3D a3D, int thickness, Shapes shape)
    {
        vertices = initVertices;
        edges = new ArrayList<Edge>();
        for (int i = 0; i<vertices.size(); i++) 
        {
          edges.add(new Edge(this, vertices.get(i), vertices.get((i+1)%(vertices.size()))));
        }
        
        tenons = new ArrayList<Tenon>();
        for (Edge e : edges)
        {
          tenons.add(new Tenon(e));       
        }

        
        this.position2D = p2D;
        this.position3D = p3D;
        this.angle3D = a3D;
        this.thickness = thickness;
        //this.material = material;

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
    
    public ArrayList<Vec2D> getTenons()
    {      
//ToDO Update function!!!
//        if (tenons == null) 
//        {
          tenons.clear();  
          for (Edge e : edges)
          {
            tenons.add(new Tenon(e));       
          }
//        }
//        if(((new Polygon2D((List) vertices)).getArea())>10) 
//        {
//          tenons.set(0, new Tenon(edges.get(0), edges.get(0), 90, true, true));
//          tenons.set(1, new Tenon(edges.get(1), edges.get(1), 45, true, true));
//          tenons.set(2, new Tenon(edges.get(2), edges.get(2), 30, true, true));
//          tenons.set(3, new Tenon(edges.get(3), edges.get(3), 10, true, true));
//          correctIntersections();
//        }
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
    
    public Vec3D getPosition3D()
    {
        return this.position3D;
    }
    
    public Vec3D getAngle3D()
    {
        return this.angle3D;
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
    
    public void setPosition3D(Vec3D position)
    {
        this.position3D = position;
    }
    
    public void setAngle3D(Vec3D angle)
    {
        this.angle3D = angle;
    }
    
    public ArrayList<Vec2D> getResized (int sizeX, int sizeY) {
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
  public void drawPreview(PGraphics p, int posX, int posY, int sizeX, int sizeY) {
    ArrayList<Vec2D> toDraw = this.getResized(sizeX, sizeY);
    this.createCover(p, toDraw, new Vec2D (posX, posY));
  }
  
  
  public void draw2D(PGraphics p) {
    this.createCover(p, getTenons(), position2D);
  }
  
  public void draw3D(PGraphics p) {
    this.createCover(p, getTenons(), thickness/2, position3D, angle3D);
    this.createCover(p, getTenons(), -thickness/2, position3D, angle3D);
//ToDo? Switch-off-Sides-Option for better performance (...if too many objects...)
    this.createSides(p, getTenons(), thickness, position3D, angle3D);
//ToDo: Same for cut-out sides -> external function for sides
  }
  
  private Vec3D transformVector(Vec2D vector, int distanceToBottom, Vec3D position, Vec3D angle)
  {
    Vec3D result = new Vec3D(vector.x(), vector.y(), distanceToBottom);
    result = result.rotateX((float)Math.toRadians(angle.x())).rotateY((float)Math.toRadians(angle.y())).rotateZ((float)Math.toRadians(angle.z()));
    result = result.add(position);
    return result;
  }
  
  private void createSides(PGraphics p, ArrayList<Vec2D> vectors, int distanceToBottom, Vec3D position, Vec3D angle) 
  {
    this.setFillColor(p);
    ArrayList<Vec3D> resultTop = new ArrayList<Vec3D>();
    ArrayList<Vec3D> resultBottom = new ArrayList<Vec3D>();
    for (Vec2D actVector : vectors) {
      resultTop.add(transformVector(actVector, +distanceToBottom/2, position, angle));
      resultBottom.add(transformVector(actVector, -distanceToBottom/2, position, angle));
    }

    for (int i=0; i<vectors.size()-1; i++) {
        p.beginShape();
        p.vertex(resultBottom.get(i).x(),   resultBottom.get(i).y(),   resultBottom.get(i).z());
        p.vertex(resultTop.get(i).x(),      resultTop.get(i).y(),      resultTop.get(i).z());
        p.vertex(resultTop.get(i+1).x(),    resultTop.get(i+1).y(),    resultTop.get(i+1).z());
        p.vertex(resultBottom.get(i+1).x(), resultBottom.get(i+1).y(), resultBottom.get(i+1).z());
        p.endShape(PConstants.CLOSE);
     }
     p.beginShape();
     int i = vectors.size()-1;
     p.vertex(resultBottom.get(i).x(), resultBottom.get(i).y(), resultBottom.get(i).z());
     p.vertex(resultTop.get(i).x(),    resultTop.get(i).y(),    resultTop.get(i).z());
     p.vertex(resultTop.get(0).x(),    resultTop.get(0).y(),    resultTop.get(0).z());
     p.vertex(resultBottom.get(0).x(), resultBottom.get(0).y(), resultBottom.get(0).z());
     p.endShape(PConstants.CLOSE);
  }
 
     private void setFillColor(PGraphics p) {
//Todo color dependent on material...
        if (this.isSelected()) {
            p.fill(255,0,0);
        } else {
            p.fill(255);
        }
    }
    
  private void createCover(PGraphics p, ArrayList<Vec2D> vectors, Vec2D position) {
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
  
  private void createCover(PGraphics p, ArrayList<Vec2D> vectors, int distanceToBottom, Vec3D position, Vec3D angle) {
    this.setFillColor(p);
    p.beginShape();
    for (Vec2D vector : vectors) {
      Vec3D result = transformVector(vector, distanceToBottom, position, angle);
      p.vertex(result.x(), result.y(), result.z());
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
