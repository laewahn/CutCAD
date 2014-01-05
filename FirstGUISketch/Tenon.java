import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;
import static java.lang.System.*;

import java.util.*;

public class Tenon 
{
  private int relationTenonLength = 4; //to determine length of a tenon corresponding to thickness of shapes

  public Tenon(Edge edge1)
  {
    ArrayList<Vec2D> basic = new ArrayList<Vec2D>();
    basic.add(edge1.getV1());
    basic.add(edge1.getV2());
    edge1.setTenons(basic);
  }

  public Tenon(Edge edgeMaster, Edge edgeSlave)
  {
    Vec3D p1 = edgeMaster.getShape().get3Dperpendicular(edgeMaster.getP3D1(), edgeMaster.getP3D2());
    Vec3D p2 = edgeSlave.getShape().get3Dperpendicular(edgeSlave.getP3D1(), edgeSlave.getP3D2());
    float angle = p1.angleBetween(p2, true);

    float edgeLength = edgeMaster.getV1().distanceTo(edgeMaster.getV2());

    int thicknessMaster = edgeMaster.getShape().getThickness();
    int thicknessSlave = edgeSlave.getShape().getThickness();

    int numberOfTenons = (int)((2 * edgeLength) / (relationTenonLength * (thicknessMaster +thicknessSlave)));
    numberOfTenons = (numberOfTenons/2) * 2;
    // allways odd number, minimum 1
    numberOfTenons = numberOfTenons + 1;

    float lengthOfATenon = edgeLength/numberOfTenons;

    float masterTenonHeight = getHeight(lengthOfATenon, thicknessMaster, thicknessSlave, angle);
    float masterTenonDepth = masterTenonHeight; // still can't believe that
    float slaveTenonHeight = getHeight(lengthOfATenon, thicknessSlave, thicknessMaster, angle);
    float slaveTenonDepth = slaveTenonHeight; // still can't believe that
    // tenon intrusions to big (longer than tenonSize)
    // Problem: did not work for small angles...
    // user has to modify the tenons afterwards (sandpaper :-)
    // allow bigger intrusions has the inert problem that this may interfere with neighbour tenons
    if (masterTenonDepth>2*thicknessMaster) {
      masterTenonDepth= 2*thicknessMaster;
      slaveTenonHeight = thicknessMaster*masterTenonDepth*(float)Math.sin(angle);
    }
    if (slaveTenonDepth>2*thicknessSlave) {
      slaveTenonDepth = 2*thicknessSlave;
      masterTenonHeight = thicknessSlave*slaveTenonDepth*(float)Math.sin(angle);
    }
    edgeMaster.setTenons(createTenons(edgeMaster, lengthOfATenon, masterTenonHeight, masterTenonDepth, numberOfTenons, true));
    edgeSlave.setTenons(createTenons(edgeSlave, lengthOfATenon, slaveTenonHeight, slaveTenonDepth, numberOfTenons, false));
  }

  private float getHeight(float lengthOfATenon, int thicknessMaster, int thicknessSlave, float angle)
  {
    if (angle == (float)0 || angle == (float)Math.PI) 
    {
      // 180° angle (and 0°, but that shouldn't happen)
      // just use the intrusion of one side as a extrusion for the other
      return thicknessMaster/2;
    }
    else if (angle == (float)Math.PI/2 || angle == (float)Math.PI*3/2) {
      // 90°, -90° angle
      // since the logical edge is in the middle of the thickness we have to add half the thickness the other (connected) shape
      // as extrusion as well as intrusion
      return thicknessSlave/2;
    }
    else if ((angle >(float)0 && angle < (float)Math.PI/2) || (angle > (float)Math.PI && angle < (float)Math.PI*3/2))
    {
      // acute angle: Each edge has to be intruded by 
      // - the other shapes thickness/sin(angle) (triangle: opposite = thickness/2 -> hypotenuse
      // - the own thickness/tan(angle) (triangle: opposite = thickness/2 -> adjacent
      return (thicknessSlave/2)/(float)Math.sin(angle)+(thicknessMaster/2)/(float)Math.tan(angle);
    }
    else
    {
      // obtuse angle: Each edge has to be intruded by 
      // - the other shapes thickness/cos(angle) (triangle: adjacent = thickness/2 -> hypotenuse
      // - the own thickness/tan(angle) (triangle: opposite = thickness/2 -> adjacent
      angle = angle-(float)Math.PI/2; 
      return (thicknessSlave/2)/(float)Math.cos(angle)+(thicknessMaster/2)*(float)Math.tan(angle);
    }
  }

  private ArrayList<Vec2D> createTenons(Edge edge, float lengthOfATenon, float tenonHeight, float tenonDepth, int numberOfTenons, boolean beginWithExtrusion) 
  {
    // Ech single tenon consists of two vectors at two points along the edge.
    // Each tenon has a length of modTXlength
    //  -> move each vector along the edge by either i (start of this tenon) or i+1 (end) * modTXlength
    // The tenon is either extruded or intruded (add or substract the corresponding values)
    // which is done opposite for both edges in each step
    // int compare = extr1start ? 0 : 1;
    Vec2D edgeDirection = edge.getV2().sub(edge.getV1()).getNormalized();
    Vec2D tenonDirection = edgeDirection.copy().perpendicular();

    ArrayList<Vec2D> listTenons = new ArrayList<Vec2D>();
    Vec2D currentTenon = edge.getV1().copy();

    int startExtruded = beginWithExtrusion ? 0 : 1;

    for (int i = 0; i<numberOfTenons; i++) 
    {
      float offset = ((i % 2) == startExtruded) ? tenonHeight : tenonDepth*(-1);
      currentTenon = currentTenon.add(tenonDirection.scale(offset));
      listTenons.add(currentTenon);
      currentTenon = currentTenon.add(edgeDirection.scale(lengthOfATenon));
      listTenons.add(currentTenon);
      currentTenon = currentTenon.sub(tenonDirection.scale(offset));
    }
    return listTenons;
  }
}

