package de.mcp.customizer.model;
import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Drawable3D;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Line2D;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class GShape implements Drawable2D, Drawable3D
{
  private int numberOfConnections;
  private Vec2D position2D;
  private Vec3D position3D;
  private boolean isSelected, isActive;
  private List<Vec2D> vertices;
  private List<Vec3D> vertices3D;
  private List<Cutout> cutouts = new ArrayList<Cutout>();
  private List<Edge> edges;
  private Shape shape;
  private Material material;
  private String name;

  public GShape(List<Vec2D> initVertices, Vec3D position, Shape shape)
  {
    this.position2D = position.to2DXY();
    this.position3D = position;
    this.isSelected = false;
    this.isActive = false;
    this.shape = shape;
    this.numberOfConnections = 0;
    this.material = AllMaterials.getBaseMaterial();

    vertices = initVertices;
    edges = new ArrayList<Edge>();
    vertices3D = new ArrayList<Vec3D>();
    
    for (Vec2D v : vertices)
    {
      vertices3D.add(v.add(position2D).to3DXY());
    }
    for (int i = 0; i<vertices.size(); i++) 
    {
      edges.add(new Edge(this, vertices3D.get(i), vertices3D.get((i+1)%(vertices.size())), vertices.get(i), vertices.get((i+1)%(vertices.size()))));
    }
  }
  
  public void recalculate(List<Vec2D> basicShape)
  {
    if(this.numberOfConnections == 0)
    {
      vertices = basicShape;
      edges.clear();
      vertices3D.clear();
      
      for (Vec2D v : vertices)
      {
        vertices3D.add(v.add(position2D).to3DXY());
      }
      for (int i = 0; i<vertices.size(); i++) 
      {
        edges.add(new Edge(this, vertices3D.get(i), vertices3D.get((i+1)%(vertices.size())), vertices.get(i), vertices.get((i+1)%(vertices.size()))));
      }
    }
  }
  
  public void addCutout(GShape cutout)
  {
	cutouts.add(new Cutout (this, cutout));
  }
  
  public void removeCutout(Cutout cutout)
  {
	  cutouts.remove(cutout);
  }

  public int getNumberOfConnections() 
  {
    return this.numberOfConnections;
  }

  public void addNumberOfConnections(int connections)
  {
    this.numberOfConnections = this.numberOfConnections+connections;
  }

  public Shape getParent()
  {
    return this.shape;
  }

  public int getThickness()
  {
    return this.material.getMaterialThickness();
  }

  public Material getMaterial()
  {
    return this.material;
  }

  public Vec3D getPosition3D()
  {
    return this.position3D;
  }

  public boolean isSelected() {
    return this.isSelected;
  }

  public List<Edge> getEdges()
  {
    return this.edges;
  }

  public List<Vec2D> getVertices()
  {
    return this.vertices;
  }
  
  public boolean overlapsWith(GShape s)
  {
      if (noLineIntersections(this.getVerticesIncludingPosition2D(), s.getVerticesIncludingPosition2D()))
      {
          if (this.containsAtLeastOnePointFromList(s.getVerticesIncludingPosition2D()) || s.containsAtLeastOnePointFromList(this.getVerticesIncludingPosition2D()))
          {
              return true;
          }
          else
          {
              return false;
          }
      }
      else
      {
          return true;
      }
  }
  
  private boolean noLineIntersections(List<Vec2D> vectors1, List<Vec2D> vectors2)
  {
      List<Line2D> lines1 = createListOfLines(vectors1);
      List<Line2D> lines2 = createListOfLines(vectors2);
      
      for (Line2D x : lines1)
      {
          for (Line2D y : lines2)
          {
              if (x.intersectLine(y).getType().equals(Line2D.LineIntersection.Type.valueOf("INTERSECTING")))
              {
                  return false;
              }
          }
      }        
      return true;
  }
  
  private List<Line2D> createListOfLines(List<Vec2D> vectors)
  {
      ArrayList<Line2D> lines = new ArrayList<Line2D>();
      
      for (int i = 0; i < vectors.size() - 1; i++)
      {
          lines.add(new Line2D(vectors.get(i), vectors.get(i+1)));
      }
      lines.add(new Line2D(vectors.get(vectors.size()-1), vectors.get(0)));
      
      return lines;        
  }   
  
  private boolean containsAtLeastOnePointFromList(List<Vec2D> points)
  {
      Polygon2D thisShape = new Polygon2D(this.getVerticesIncludingPosition2D());
      for (Vec2D v : points)
      {
          if (thisShape.containsPoint(v)) return true;
      }
      return false;
  }
  
  private List<Vec2D> getVerticesIncludingPosition2D()
  {
	  ArrayList<Vec2D> vectors = new ArrayList<Vec2D>();
	  for (Vec2D v: this.getVertices())
	  {
		  vectors.add(v.add(this.position2D));
	  }
	  return vectors;
  }

  private Vec2D correctIntersection(Edge edge)
  {
    int index = edges.indexOf(edge);

    index = (index == 0) ? edges.size()-1 : index-1;

    Edge edge1 = edges.get(index);
    Edge edge2 = edge;

    Vec2D firstVectorOfEdge1 = edge1.getTenons().get(0);
    Vec2D secondVectorOfEdge1 = edge1.getTenons().get(1);
    Vec2D firstVectorOfEdge2 = edge2.getTenons().get(1);
    Vec2D secondVectorOfEdge2 = edge2.getTenons().get(0);

    Line2D firstTenonOfEdge1 = new Line2D(firstVectorOfEdge1, secondVectorOfEdge1);
    firstTenonOfEdge1 = firstTenonOfEdge1.toRay2D().toLine2DWithPointAtDistance(10000);
    Line2D firstTenonOfEdge2 = new Line2D(firstVectorOfEdge2, secondVectorOfEdge2);
    firstTenonOfEdge2 = firstTenonOfEdge2.toRay2D().toLine2DWithPointAtDistance(10000);

    if (String.valueOf(firstTenonOfEdge1.intersectLine(firstTenonOfEdge2).getType()).equals("INTERSECTING")) 
    {
      return firstTenonOfEdge1.intersectLine(firstTenonOfEdge2).getPos();
    }
    return firstVectorOfEdge2;
  }

  public ArrayList<Vec2D> getTenons()
  {
    ArrayList<Vec2D> allVectors = new ArrayList<Vec2D>();
    for (Edge e : edges)
    {
      
      allVectors.add(correctIntersection(e));
      for(int i=1; i<e.getTenons().size()-1; i++)
      {
        allVectors.add(e.getTenons().get(i));
      }
      
    }
    allVectors.add(allVectors.get(allVectors.size()-1)); //?????????????????????????????????????????????
    // (not needed for normal drawing, but for drawing of the copy - otherwise this last vector is missing 
    // (the first shape, which is drawn, appears for a short time in correct form, but then this vector
    // disappears?????
    return allVectors;
  }

  public ArrayList<Vec3D> transformTo3D(boolean top, ArrayList<Vec2D> vectors2D)
	{      
		// Use the algorithm for connecting two shapes to align logical 2D and 3D view
		// Therefore produce a new GShape with the 2D positions (for the 3D represenation)
		// 
		GShape helperShape = new GShape(vertices, position3D, this.getParent());

		GShape master = this;
		GShape slave = helperShape;
		Edge masterEdge = this.getEdges().get(0);
		Edge slaveEdge = helperShape.getEdges().get(0);

		Vec3D masterEdgeDirection = masterEdge.getP3D2().sub(masterEdge.getP3D1());
		Vec3D slaveEdgeDirection = slaveEdge.getP3D2().sub(slaveEdge.getP3D1());

		float angleBetweenEdges = safeAngleBetween(slaveEdgeDirection,masterEdgeDirection);

		Vec3D normalVector = slaveEdgeDirection.cross(masterEdgeDirection).getNormalized();
		while (normalVector.equals(new Vec3D(0,0,0)))
		{
			normalVector = masterEdgeDirection.cross(new Vec3D((float)Math.random(), (float)Math.random(), (float)Math.random())).getNormalized();
		}
		slave.rotateAroundAxis(normalVector, angleBetweenEdges);
		
		slaveEdgeDirection = slaveEdge.getP3D2().sub(slaveEdge.getP3D1());
//		if (Math.abs(slaveEdgeDirection.angleBetween(masterEdgeDirection, true)) < 0.1f)
//		{
//			// do nothing
//		}
//		else if (Math.abs(slaveEdgeDirection.angleBetween(masterEdgeDirection, true)) > Math.PI - 0.1f && Math.abs(slaveEdgeDirection.angleBetween(masterEdgeDirection, true)) < Math.PI + 0.1f)
//		{
//			angleBetweenEdges = (float)Math.PI;
//		}
//		else 
//		{
//			slave.rotateAroundAxis(normalVector, -2*angleBetweenEdges);
//			angleBetweenEdges = -angleBetweenEdges;
//		} 

		Vec3D toOrigin = slaveEdge.getP3D1().scale(-1).copy();

		slave.translate3D(toOrigin);

		Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();

		float angleBetweenNormals = calculateAngleBetweenNormals(master, slave);
		slave.rotateAroundAxis(rotationAxis, angleBetweenNormals);

		if (calculateAngleBetweenNormals(master, slave) > 0.001f)
		{
//		{
//			// Do Nothing
////			System.out.println("Right Angle");
//		}
//		else if (Math.abs(calculateAngleBetweenNormals(master, slave)) > Math.PI-0.0001f)
//		{
////			System.out.println("Rotate additional 180 degree");
//			angleBetweenNormals = (float) Math.PI;
//		}
//		else
//		{
////			System.out.println("Rotate in the other direction");
			slave.rotateAroundAxis(rotationAxis, (float) -2.0 * angleBetweenNormals);
			angleBetweenNormals = -angleBetweenNormals;
		}
		
		Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1()).copy();
//		slave.translate3D(toMaster);
//		if(slave.getEdges().get(1).getP3D2().distanceTo(master.getEdges().get(1).getP3D2()) > 1f)
//		{
//			angleBetweenNormals = (float) (angleBetweenNormals + Math.PI);
//		}


		// Now we know everything, apply the same Translations to the outline Vec2D array
		// Translated by thickness/2 to top or bottom
		int offsetZ;
		if (top)
		{
			offsetZ = this.getThickness()/2;
		}
		else
		{
			offsetZ = -this.getThickness()/2;
		}
		
		ArrayList<Vec3D> vectors3D = new ArrayList<Vec3D>();
		for (Vec2D v : vectors2D)
		{
			vectors3D.add(v.to3DXY().add(position3D).addSelf(new Vec3D(0, 0, offsetZ)));
		}
//		System.out.println("AngleEdges" + angleBetweenEdges);
//		System.out.println("AngleNormals" + angleBetweenNormals);
//		System.out.println("toOrigin" + toOrigin);
//		System.out.println("toMaster" + toMaster);
		for (int i=0; i<vectors3D.size(); i++)
		{
			vectors3D.set(i, vectors3D.get(i).rotateAroundAxis(normalVector, angleBetweenEdges));
			vectors3D.set(i, vectors3D.get(i).addSelf(toOrigin));
			vectors3D.set(i, vectors3D.get(i).rotateAroundAxis(rotationAxis, angleBetweenNormals));
			vectors3D.set(i, vectors3D.get(i).addSelf(toMaster));
		}
		return vectors3D;
	}
  
  	private float safeAngleBetween(Vec3D masterEdgeDirection,
			Vec3D slaveEdgeDirection) {
		float angle = slaveEdgeDirection.angleBetween(masterEdgeDirection, true);
	    if (Float.isNaN(angle))
		{
	    	if(slaveEdgeDirection.add(masterEdgeDirection).equalsWithTolerance(new Vec3D(0,0,0), 0.1f))
	    	{
	    		angle = (float)Math.PI;
	    	}
	    	else
	    	{
	    		angle = 0;
	    	}
	    }
		return angle;
	}

	private float calculateAngleBetweenNormals(GShape master, GShape slave)
	{
		Vec3D masterNormal = master.getNormalVector();
		Vec3D slaveNormal = slave.getNormalVector();
	    return safeAngleBetween(masterNormal,slaveNormal);
	}

  public Vec2D get2Dperpendicular(Vec2D v1, Vec2D v2)
  {
    return v2.sub(v1).getPerpendicular().add(v1).normalize();
  }

  public Vec3D get3Dperpendicular(Vec3D v1, Vec3D v2)
  {
    for (Vec3D v3 : vertices3D)
    {
      if (!v3.sub(v1).cross(v2.sub(v1)).isZeroVector())
      {
        Vec3D normal = v3.sub(v1).cross(v2.sub(v1));
        return normal.cross(v2.sub(v1)).invert().add(v1).normalize();
      }
    }
    return new Vec3D (0, 0, 1);
  }

  public Vec2D getPosition2D()
  {
    return this.position2D;
  }

  public void setMaterial(Material material)
  {
	this.material = material;
    if(this.getNumberOfConnections() > 0)
    {
       for(Edge e : edges)
       {
         for (Connection c : Connection.getConnections())
         {
           if(c.getMasterEdge() == e) Tenon.createOutlineOfEdge(c.getSlaveEdge(), e);
           else if (c.getSlaveEdge() == e) Tenon.createOutlineOfEdge(c.getMasterEdge(), e);
         }
       }
    }
  }

  public void setSelected(boolean selected) {
    this.isSelected = selected;
  }

  public void setPosition2D(Vec2D position)
  {
    this.position2D = position;
  }

  public void translate2D(Vec2D direction)
  {
    this.position2D.addSelf(direction);
  }

  public void rotateAroundAxis(Vec3D rotationAxis, float theta)
  {
    for (Vec3D v : vertices3D)
    {
      v.rotateAroundAxis(rotationAxis, theta);
    }
  }

  public void translate3D(Vec3D translationVector)
  {
    for (Vec3D v : vertices3D)
    {
      v.addSelf(translationVector);
    }
  }

  public Vec3D getNormalVector()
  {
    return edges.get(0).getP3D1().sub(edges.get(0).getP3D2()).cross(edges.get(1).getP3D1().sub(edges.get(1).getP3D2())).getNormalized();
  }

  // Just to display the basic shape in a certain areal (e.g. within a button):
//  public ArrayList<Vec2D> getResized (int sizeX, int sizeY) 
//  {
//    int minX = Integer.MAX_VALUE;
//    int minY = Integer.MAX_VALUE;
//    int maxX = 0;
//    int maxY = 0;
//    for (Vec2D vector : vertices) {
//      if ((int)vector.x() > maxX) maxX = (int)vector.x();
//      if ((int)vector.x() < minX) minX = (int)vector.x();
//      if ((int)vector.y() > maxY) maxY = (int)vector.y();
//      if ((int)vector.y() < minY) minY = (int)vector.y();
//    }
//    ArrayList<Vec2D> resized = new ArrayList<Vec2D>();
//    for (Vec2D vector : vertices) {
//      resized.add(new Vec2D(vector.x()*sizeX/(maxX-minX), vector.y()*sizeY/(maxY-minY)));
//    }
//    return resized;
//  }

  // drawing routines
//  public void drawPreview(PGraphics p, int posX, int posY, int sizeX, int sizeY) 
//  {
//    ArrayList<Vec2D> toDraw = this.getResized(sizeX, sizeY);
//    this.createCover2D(p, toDraw, new Vec2D (posX, posY));
//  }


  public void draw2D(PGraphics p) 
  {
    this.createCover2D(p, getTenons(), position2D);

    for (Edge e: edges) //not good... but i've no better idea
    {
      e.draw2D(p);
    }
  }

  public void draw3D(PGraphics p) 
  {
    this.setFillColor(p);
    createCover3D(p,true);
    createCover3D(p, false);
    createSides(p, getTenons());
    for (Cutout cutout : cutouts)
    {
    	createSides(p, cutout.getVectors());
    }
    
    // if we want to show the "Logic" shape:
    ///*
    p.noFill();
    p.stroke(0,0,255);
    p.beginShape();
    for (Edge e : edges) {
      p.vertex(e.getP3D1().x(), e.getP3D1().y(), e.getP3D1().z());
    }
    p.endShape(PConstants.CLOSE);
    //*/
    
    for (Edge e: edges) //not good... but i've no better idea...still no better version
    {
      e.draw3D(p);
    }
  }
  
  private void createSides(PGraphics p, ArrayList<Vec2D> vectors)
  {
    ArrayList<Vec3D> top = transformTo3D(true, vectors);
	ArrayList<Vec3D> bottom = transformTo3D(false, vectors);

	for (int i=0; i<top.size()-1; i++) {
	  p.beginShape();
	  p.vertex(top.get(i).x(), top.get(i).y(), top.get(i).z());
	  p.vertex(top.get(i+1).x(), top.get(i+1).y(), top.get(i+1).z());
	  p.vertex(bottom.get(i+1).x(), bottom.get(i+1).y(), bottom.get(i+1).z());
	  p.vertex(bottom.get(i).x(), bottom.get(i).y(), bottom.get(i).z());
	  p.endShape(PConstants.CLOSE);
	}
	p.beginShape();
	p.vertex(top.get(top.size()-1).x(), top.get(top.size()-1).y(), top.get(top.size()-1).z());
	p.vertex(top.get(0).x(), top.get(0).y(), top.get(0).z());
	p.vertex(bottom.get(0).x(), bottom.get(0).y(), bottom.get(0).z());
	p.vertex(bottom.get(top.size()-1).x(), bottom.get(top.size()-1).y(), bottom.get(top.size()-1).z());
	p.endShape(PConstants.CLOSE);
  }

  private void createCover3D(PGraphics p, boolean b) 
  {
	p.beginShape();
	for (Vec3D vector : transformTo3D(b, getTenons())) {
	  p.vertex(vector.x(), vector.y(), vector.z());
	}
	    
	for(Cutout cutout : cutouts)
	{
	  p.beginContour();
	  for(Vec3D vector : transformTo3D(b, cutout.getVectors()))
	  {
	    p.vertex(vector.x(), vector.y(), vector.z());
	  }
	  p.endContour();
	}
	p.endShape(PConstants.CLOSE);
  }

  private void createCover2D(PGraphics p, ArrayList<Vec2D> vectors, Vec2D position)
  {
    this.setFillColor(p);
    p.beginShape();
    for (Vec2D vector : getTenons()) {
      p.vertex(vector.x()+getPosition2D().x(), vector.y()+getPosition2D().y());
    }
    
    for(Cutout cutout : cutouts)
    {
      p.beginContour();
      for(Vec2D vector : cutout.getVectors())
      {
    	p.vertex(vector.x()+getPosition2D().x(), vector.y()+getPosition2D().y());
      }
      p.endContour();
    }
    p.endShape(PConstants.CLOSE);
  }

  private void setFillColor(PGraphics p) 
  {
    if (this.isSelected()) {
      p.stroke(255,0,0);
    }
    else if (this.isActive)
    {
      p.stroke(125,0,0);
    }
    else
    {
      p.stroke(0);
    }
    p.fill(getMaterial().getMaterialColor());
  }

  public boolean mouseOver(Vec2D mousePosition)
  {
    Polygon2D test = new Polygon2D((List<Vec2D>)vertices);
    return test.containsPoint(mousePosition.sub(position2D));
  }
  
  public GShape copy(Shape shape)
  {
    GShape copy = new GShape(getTenons(), new Vec3D(position3D), shape); 
    copy.setMaterial(this.material);
    copy.setName(this.getName());
    copy.removeAllCutouts();
    for(Cutout c : this.cutouts)
    {
      copy.addCutout(new GShape(c.getVectors(), new Vec3D(0,0,0), shape));
      Cutout.getAllCutouts().remove(Cutout.getAllCutouts().size()-1);
    }
    return copy;
  }
  
  public Shape copyCompleteStructure()
  {
	Shape copy = this.getParent().copy();
	copy.getShape().recalculate(this.vertices);
    copy.getShape().setMaterial(this.material);
    copy.getShape().setName("CopyOf" + this.getName());
    copy.getShape().removeAllCutouts();
    for(Cutout c : this.cutouts)
    {
      copy.getShape().addCutout(c.copyFor(copy.getShape()));
    }
    return copy;
  }

  private void removeAllCutouts() {
	  this.cutouts.clear();	
  }

  public void addCutout(Cutout cutout)
  {
	cutouts.add(cutout);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
	return isActive;
  }

  public void setActive(boolean isActive) {
	this.isActive = isActive;
  }
}

