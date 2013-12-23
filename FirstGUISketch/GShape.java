import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

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
        for (int i = 0; i<vertices.size()-1; i++) 
        {
          edges.add(new Edge(this, i, i+1));
        }
        edges.add(new Edge(this, vertices.size()-1, 0));
        
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
    
    public ArrayList<Vec2D> getTenons()
    {      
//ToDO Update function!!!
        tenons.clear();
        for (Edge e : edges)
        {
          tenons.add(new Tenon(e));       
        }
        if(((new Polygon2D((List) vertices)).getArea())>10) 
       {
          tenons.set(0, new Tenon(edges.get(0), edges.get(0), 90, true, true));
          tenons.set(1, new Tenon(edges.get(1), edges.get(1), 45, true, true));
          tenons.set(2, new Tenon(edges.get(2), edges.get(2), 30, true, true));
          tenons.set(3, new Tenon(edges.get(3), edges.get(3), 10, true, true));
        }
        // correct tenons: last/first two Vertices of neighboured tenons 
        
        // -> expand as line
        // -> intersection as new 
//        for (int i=0; i<tenons.size()-1; i++) 
//        {
//          if (tenons.get(i).getStartLine(this).intersectLine(tenons.get(i+1).getEndLine(this))!= null) {
//            out.println("intersection found");
//          //Vec2D test = (tenons.get(i).getStartLine(this).intersectLine(tenons.get(i+1).getEndLine(this))).getPos();
//          //if (test != null)out.println("Intersection: x:" + test.x() + " y:" + test.y());
//          out.println("Intersection: x:" + tenons.get(i).getStartLine(this).intersectLine(tenons.get(i+1).getEndLine(this)).getType());
//          }
//        }
      ArrayList<Vec2D> allVectors = new ArrayList<Vec2D>();
      for (Tenon t : tenons) 
      {
// skip the last vector of each list (it's the same as the next)something is strange here???
        for (int i=0; i<t.getVectors(this).size()-1; i++)
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
    this.createCover(p, getTenons(), thickness/2, position3D);
    this.createCover(p, getTenons(), -thickness/2, position3D);
//ToDo? Switch-off-Sides-Option for better performance (...if too many objects...)
    this.createSides(p, getTenons(), thickness, position3D);
//ToDo: Same for cut-out sides -> external function for sides
  }
  
//ToDo include 3D-Angles (rotation around x,y,z axis in vertex-computation...iiiiiiiiih...
  private void createSides(PGraphics p, ArrayList<Vec2D> vectors, int distanceToBottom, Vec3D position) 
  {
    this.setFillColor(p);
    for (int i=0; i<vectors.size()-1; i++) {
        p.beginShape();
        p.vertex(  vectors.get(i).x()+position.x(),   vectors.get(i).y()+position.y(), position.z() - distanceToBottom/2);
        p.vertex(  vectors.get(i).x()+position.x(),   vectors.get(i).y()+position.y(), position.z() + distanceToBottom/2);
        p.vertex(vectors.get(i+1).x()+position.x(), vectors.get(i+1).y()+position.y(), position.z() + distanceToBottom/2);
        p.vertex(vectors.get(i+1).x()+position.x(), vectors.get(i+1).y()+position.y(), position.z() - distanceToBottom/2);
        p.endShape(PConstants.CLOSE);
     }
     p.beginShape();
     int i = vectors.size()-1;
     p.vertex(vectors.get(i).x()+position.x(), vectors.get(i).y()+position.y(), position.z() - distanceToBottom/2);
     p.vertex(vectors.get(i).x()+position.x(), vectors.get(i).y()+position.y(), position.z() + distanceToBottom/2);
     p.vertex(vectors.get(0).x()+position.x(), vectors.get(0).y()+position.y(), position.z() + distanceToBottom/2);
     p.vertex(vectors.get(0).x()+position.x(), vectors.get(0).y()+position.y(), position.z() - distanceToBottom/2);
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
  
  private void createCover(PGraphics p, ArrayList<Vec2D> vectors, int distanceToBottom, Vec3D position) {
    this.setFillColor(p);
    p.beginShape();
    for (Vec2D vector : vectors) {
      p.vertex(vector.x()+position.x(), vector.y()+position.y(), position.z() + distanceToBottom);
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
