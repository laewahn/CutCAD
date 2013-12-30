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
  private int tenonNumber;
  private float angle, edgeLength, tenonLength, scalingLength, scaling0, scaling1, lengthIntr, lengthIntrNew;
  private boolean extr1start, extr1end, isIndependant;

  private int relationTenonLength = 4;

  public Tenon(Edge edge1, Edge edge2, float angle, boolean extr1start, boolean extr1end)
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

    determineAngle();
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
    Vec2D modT0S1 = (end[0].sub(start[0])).perpendicular().normalizeTo(scaling1/2);
    Vec2D modT1S0 = (end[1].sub(start[1])).perpendicular().normalizeTo(scaling0/2);

    //extrusion of the tenons ("get out of base shape")
    Vec2D modT0S0 = (end[0].sub(start[0])).perpendicular().normalizeTo(scaling0/2);
    Vec2D modT1S1 = (end[1].sub(start[1])).perpendicular().normalizeTo(scaling1/2);

    Vec2D modT0extr, modT1extr, modT0intr, modT1intr;

    // acute angle
    float modASin = (float)(1/Math.sin(angle));
    float modATan = (float)(1/Math.tan(angle));

    // obtuse angle
    float modTan = (float)(Math.tan(angle-(float)Math.PI/2));
    float modSin = (float)(Math.sin(angle-(float)Math.PI/2));
    float modACos = (float)(1/Math.cos(angle-(float)Math.PI/2));

    if (angle == (float)0 || angle == (float)Math.PI) {
      modT0intr = modT0S1;
      modT1intr = modT1S0;

      modT0extr = modT0S0;
      modT1extr = modT1S1;
    }
    else if (angle == (float)Math.PI/2 || angle == (float)Math.PI*3/2) {
      modT0intr = modT0S1.scale(modASin);
      modT1intr = modT1S0.scale(modASin);

      modT0extr = modT0S1.scale(modASin);
      modT1extr = modT1S0.scale(modASin);
    }
    else if ((angle >(float)0 && angle < (float)Math.PI/2) || (angle > (float)Math.PI && angle < (float)Math.PI*3/2))
    {
      // acute angle
      out.println("test2");
      modT0intr = modT0S1.scale(modASin).addSelf(modT0S0.scale(modATan));
      modT1intr = modT1S0.scale(modASin).addSelf(modT1S1.scale(modATan));

      modT0extr = modT0S1.scale(modASin).addSelf(modT0S0.scale(modATan));
      modT1extr = modT1S0.scale(modASin).addSelf(modT1S1.scale(modATan));
      modSin = (float)(Math.sin(angle)); // for tenon intrusion to big
    }
    else
    {
      // obtuse angle
      modT0intr = modT0S1.scale(modACos).addSelf(modT0S0.scale(modTan));
      modT1intr = modT1S0.scale(modACos).addSelf(modT1S1.scale(modTan));

      modT0extr = modT0S1.scale(modACos).addSelf(modT0S0.scale(modTan));
      modT1extr = modT1S0.scale(modACos).addSelf(modT1S1.scale(modTan));
    }

    
    // tenon intrusions to big (longer than tenonSize)
    // Problem: did not work for small angles...
    // user has to modify the tenons afterwards
    // allow bigger intrusions has the inert problem, that this may interfere with neighbour tenons
    lengthIntr = modT0intr.magnitude(); 
    if (lengthIntr>2*thickness[0]) {
      modT0intr = modT0intr.normalizeTo(2*thickness[0]);
      lengthIntrNew = modT0intr.magnitude(); 
      modT1extr = modT1S0.normalizeTo(lengthIntrNew).scale(modSin);
    }
    lengthIntr = modT1intr.magnitude();
    if (lengthIntr>2*thickness[1]) {
      modT1intr = modT1intr.normalizeTo(2*thickness[1]);
      lengthIntrNew = modT1intr.magnitude(); 
      modT0extr = modT0S1.normalizeTo(lengthIntrNew).scale(modSin);
    }

    ArrayList<Vec2D> list0 = new ArrayList<Vec2D>();
    ArrayList<Vec2D> list1 = new ArrayList<Vec2D>();

    int compare = extr1start ? 0 : 1;
    for (int i = 0; i<tenonNumber; i++) 
    {
      if ((i%2)==compare) 
      {
        list0.add(start[0].add(((modT0length.scale(i)).sub(modT0extr))));
        list0.add(start[0].add(((modT0length.scale(i+1)).sub(modT0extr))));
        list1.add(start[1].add(((modT1length.scale(i)).add(modT1intr))));
        list1.add(start[1].add(((modT1length.scale(i+1)).add(modT1intr))));
      } 
      else 
      {
        list0.add(start[0].add(((modT0length.scale(i)).add(modT0intr))));
        list0.add(start[0].add(((modT0length.scale(i+1)).add(modT0intr))));
        list1.add(start[1].add(((modT1length.scale(i)).sub(modT1extr))));
        list1.add(start[1].add(((modT1length.scale(i+1)).sub(modT1extr))));
      }
    }
    tenon1 = list0;
    tenon2 = list1;
  }

  private void determineAngle() {
    this.angle = shape[0].getNormalVector().angleBetween(shape[1].getNormalVector(), true);
  }

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

