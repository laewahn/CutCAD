import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;
import java.util.*;

class Blueprint2D {

	private List<DummyEdge> edges;
	Vec2D twoDeePosition;
	
	public static Blueprint2D createRectangle(Vec2D position, int width, int height)
	{
		DummyEdge north = new DummyEdge(new Vec2D(), new Vec2D(width, 0));
		north.setNumberOfTennons(10);
		north.setTenonDepth(3);
		north.setTenonHeight(5);

		DummyEdge east = new DummyEdge(new Vec2D(width, 0), new Vec2D(width, height));
		
		east.setNumberOfTennons(4);
		east.setTenonDepth(3);
		east.setTenonHeight(5);

		DummyEdge south = new DummyEdge(new Vec2D(width, height), new Vec2D(0, height));
		DummyEdge west = new DummyEdge(new Vec2D(0, height), new Vec2D());

		ArrayList<DummyEdge> rectDummyEdges = new ArrayList<DummyEdge>();
		rectDummyEdges.add(north);
		rectDummyEdges.add(east);
		rectDummyEdges.add(south);
		rectDummyEdges.add(west);

		return new Blueprint2D(rectDummyEdges, position);
	}

	public Blueprint2D(List<DummyEdge> edges, Vec2D position)
	{
		this.edges = edges;
		this.twoDeePosition = position;
	}

	public void draw2D(PGraphics context)
	{
		context.beginShape();

		for (DummyEdge currentDummyEdge : this.edges) {
			List<Vec2D> vertices = currentDummyEdge.getVertices();

			for(Vec2D vertex : vertices) {
				Vec2D absoluteVertex = vertex.add(this.twoDeePosition);
				context.vertex(absoluteVertex.x(), absoluteVertex.y());
			}
		}

		context.endShape(PConstants.CLOSE);
	}

}