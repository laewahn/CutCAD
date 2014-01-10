import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;
import java.util.*;

import static java.lang.System.*;

class DummyEdge
{
  
  Vec2D startCoord, endCoord;
  Vec2D perpendicularVector;

  int numberOfTennons;
  float tenonDepth, tenonHeight;

  boolean firstTenonRising;

  public DummyEdge(Vec2D startCoord, Vec2D endCoord, int numberOfTennons, float tenonDepth, float tenonHeight, boolean firstTenonRising)
  {
    this.startCoord = startCoord;
    this.endCoord = endCoord;
    this.numberOfTennons = numberOfTennons;
    this.tenonDepth = tenonDepth;
    this.tenonHeight = tenonHeight;
    this.firstTenonRising = firstTenonRising;
  }

  public DummyEdge(Vec2D startCoord, Vec2D endCoord)
  {
    this(startCoord, endCoord, 0, 0, 0, false);
  }

  public List<Vec2D> getVertices() 
  {
    ArrayList<Vec2D> vertices = new ArrayList<Vec2D>();
    
    if (this.getNumberOfTennons() == 0) {    
      vertices.add(startCoord);
      vertices.add(endCoord);
      return vertices;
    }

    float edgeLength = this.startCoord.distanceTo(this.endCoord);
    float lengthOfATenon = edgeLength / (float)this.getNumberOfTennons();

    Vec2D edgeDirection = this.endCoord.sub(this.startCoord).normalize();
    Vec2D tenonDirection = edgeDirection.getPerpendicular();
    
    Vec2D currentVertex = this.startCoord.copy();

    for (int i = 0; i < this.getNumberOfTennons(); i++) {
      
      float offset = ((i % 2) == 0) ? this.getTenonHeight() : this.getTenonDepth() * -1;

      currentVertex = currentVertex.add(tenonDirection.scale(offset));
      vertices.add(currentVertex);

      currentVertex = currentVertex.add(edgeDirection.scale(lengthOfATenon));
      vertices.add(currentVertex);

      currentVertex = currentVertex.sub(tenonDirection.scale(offset));
      vertices.add(currentVertex);
    }

    return vertices;
  }

  
  public void setNumberOfTennons(int numberOfTennons)
  {
    this.numberOfTennons = numberOfTennons;
  }

  public int getNumberOfTennons()
  {
    return this.numberOfTennons;
  }

  public void setTenonDepth(float tenonDepth)
  {
    this.tenonDepth = tenonDepth;
  }

  public float getTenonDepth()
  {
    return this.tenonDepth;
  }

  public void setTenonHeight(float tenonHeight)
  {
    this.tenonHeight = tenonHeight;
  }

  public float getTenonHeight()
  {
    return this.tenonHeight;
  }

  public void setFirstTenonRising(boolean firstTenonRising)
  {
    this.firstTenonRising = firstTenonRising;
  }

  public boolean getFirstTenonRising()
  {
    return this.firstTenonRising;
  }

}

