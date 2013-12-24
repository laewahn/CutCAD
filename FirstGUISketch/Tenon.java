import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;
import static java.lang.System.*;

import java.util.*;

public class Tenon 
{
  private ArrayList<Vec2D> tenon1;
  private ArrayList<Vec2D> tenon2;
  private Vec2D[] start = new Vec2D[2];
  private Vec2D[] end = new Vec2D[2];
  private GShape[] shape = new GShape[2];
  private int[] thickness = new int[2];
  private int angle, tenonNumber;
  private float edgeLength, tenonLength, scalingLength, scaling0, scaling1, lengthIntr, lengthIntrNew;
  private boolean extr1start, extr1end, isIndependant;

  private int relationTenonLength = 4;

  public Tenon(Edge edge1, Edge edge2, int angle, boolean extr1start, boolean extr1end)
    //ToDO extr1start, extr1end per ....containsPoint()
  {
    this.angle = angle;
    this.extr1start = extr1start;
    this.extr1end = extr1end;
    this.thickness[0] = edge1.getShape().getThickness();
    this.thickness[1] = edge2.getShape().getThickness();
    this.start[0] = edge1.getV1();
    this.start[1] = edge2.getV1();
    this.end[0] = edge1.getV2();
    this.end[1] = edge2.getV2();
    this.shape[0] = edge1.getShape();
    this.shape[1] = edge2.getShape();
    this.isIndependant = false;
    determineNumberOfTenons();

    determineSizeOfTenons();
    makeTenons();
  }

  public Tenon(Edge edge1) //unconnected edge
  {
    this.shape[0] = edge1.getShape();
    this.shape[1] = edge1.getShape();
    this.isIndependant = true;
    ArrayList<Vec2D> basic = new ArrayList<Vec2D>();
    basic.add(edge1.getV1());
    basic.add(edge1.getV2());
    tenon1 = basic;
    tenon2 = basic;
  }

  private void determineNumberOfTenons() {
    edgeLength = (new Line2D(start[0], end[0])).getLength();
    tenonNumber = (int)((2 * edgeLength)/ (relationTenonLength * (thickness[0] + thickness[1])));
    tenonNumber = (tenonNumber/2) * 2;
    if (extr1start == extr1end) tenonNumber = tenonNumber + 1;
    if (tenonNumber == 0) tenonNumber = 2;
  }

  private void determineSizeOfTenons() {
    tenonLength = edgeLength/tenonNumber;
  }

  private void makeTenons() {
    //base vector parallel to base shape
    scalingLength = (tenonLength/edgeLength);
    scaling1 = thickness[1];
    scaling0 = thickness[0];

    Vec2D modT0length = (end[0].sub(start[0])).scale(scalingLength);
    Vec2D modT1length = (end[1].sub(start[1])).scale(scalingLength);

    //intrusion of the tenons ("go inside the base shape")
    Vec2D modT0intr = (end[0].sub(start[0])).perpendicular().normalizeTo(scaling1);
    Vec2D modT1intr = (end[1].sub(start[1])).perpendicular().normalizeTo(scaling0);

    //extrusion of the tenons ("get out of base shape")
    Vec2D modT0extr = (end[0].sub(start[0])).perpendicular().normalizeTo(scaling0);
    Vec2D modT1extr = (end[1].sub(start[1])).perpendicular().normalizeTo(scaling1);

    //Special case:
    if ((angle == 0) || (angle == 90) || (angle == 180) || (angle == 270)) {
      modT0extr = new Vec2D(0, 0);
      modT1extr = new Vec2D(0, 0);
    } 
    else {
      float modSin = (float)(1/Math.sin(Math.toRadians(180-angle)));
      modT0intr = modT0intr.scale(modSin);
      modT1intr = modT1intr.scale(modSin);

      float modTan = (float)(1/Math.tan(Math.toRadians(180-angle)));
      modT0extr = modT0extr.scale(modTan);
      modT1extr = modT1extr.scale(modTan);
    }

    //tenon intrusions to big (longer than tenonSize)
    lengthIntr = modT0intr.magnitude(); 
    if (lengthIntr>2*thickness[0]) {
      modT0intr = modT0intr.normalizeTo(2*thickness[0]);
      lengthIntrNew = modT0intr.magnitude(); 
      modT1extr = modT1extr.scale(lengthIntrNew/lengthIntr);
    }
    lengthIntr = modT1intr.magnitude();
    if (lengthIntr>2*thickness[1]) {
      modT1intr = modT1intr.normalizeTo(2*thickness[1]);
      lengthIntrNew = modT1intr.magnitude(); 
      modT0extr = modT1extr.scale(lengthIntrNew/lengthIntr);
    }

    ArrayList<Vec2D> list0 = new ArrayList<Vec2D>();
    ArrayList<Vec2D> list1 = new ArrayList<Vec2D>();

    int compare = extr1start ? 0 : 1;
    for (int i = 0; i<tenonNumber; i++) 
    {
      if ((i%2)==compare) 
      {
        list0.add(start[0].add(((modT0length.scale(i)).add(modT0extr))));
        list0.add(start[0].add(((modT0length.scale(i+1)).add(modT0extr))));
        list1.add(start[1].add(((modT1length.scale(i)).add(modT1intr))));
        list1.add(start[1].add(((modT1length.scale(i+1)).add(modT1intr))));
      } 
      else 
      {
        list0.add(start[0].add(((modT0length.scale(i)).add(modT0intr))));
        list0.add(start[0].add(((modT0length.scale(i+1)).add(modT0intr))));
        list1.add(start[1].add(((modT1length.scale(i)).add(modT1extr))));
        list1.add(start[1].add(((modT1length.scale(i+1)).add(modT1extr))));
      }
    }
    tenon1 = list0;
    tenon2 = list1;
  }

  //  private void determineAngle() {
  ////ToDo!!!!!    
  //    /*
  //    firstShape.getAngle() //object oriantation in 3D
  //    Math.tan((firstShape.getVertexIY(shape1v1)-firstShape.getVertexIY(shape1v2))/(firstShape.getVertexIX(shape1v1)-firstShape.getVertexIX(shape1v2)));
  //    // edge oriantation within object
  //    //-> combine booth
  //    analog. secondShape.getAngle()...
  //    */
  //                                
  //    angle = 90;
  //  }

  private boolean testShape(GShape shape)
  {
    return (this.shape[0] == shape);
  }

  public boolean isIndependant() {
    return isIndependant;
  }

  public ArrayList<Vec2D> getVectors(GShape shape) 
  {
    if (testShape(shape)) return tenon1;
    else return tenon2;
  }

  public Line2D getStartLine(GShape shape)
  {
    if (testShape(shape)) return new Line2D(tenon1.get(1), tenon1.get(0));
    else return new Line2D(tenon2.get(1), tenon2.get(0));
  }

  public Line2D getEndLine(GShape shape)
  {
    if (testShape(shape)) return new Line2D(tenon1.get(tenon1.size()-2), tenon1.get(tenon1.size()-1));
    else return new Line2D(tenon2.get(tenon2.size()-2), tenon2.get(tenon2.size()-1));
  }

  public void correctStartEdge(GShape shape, Vec2D vector) 
  {
    if (testShape(shape)) tenon1.set(0, vector);
    else tenon2.set(0, vector);
  }

  public void correctEndEdge(GShape shape, Vec2D vector) 
  {
    if (testShape(shape)) tenon1.set(tenon1.size()-1, vector);
    else tenon2.set(tenon2.size()-1, vector);
  }
}

