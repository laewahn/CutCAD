package de.mcp.customizer.application.tools;

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Line2D;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.GShape;
import de.mcp.customizer.model.PolygonShape;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation2D;

public class PolygonTool extends Tool {

	private List<Shape> shapes;

	private List<Vec2D> vertices;
	
	private Vec2D lastKnownMousePositon;

	public PolygonTool(Rect view, Properties properties,
			Transformation2D transform, List<Shape> shapes) {
		super(view, properties, transform, "PolygonTool");
		this.shapes = shapes;
	}

	@Override
	public void mouseButtonPressed(Vec2D position, int button) {

	}

	@Override
	public void mouseButtonReleased(Vec2D position, int button) {

		if (!inView(position))
			return;

		if (vertices.size() > 1 && mouseOverCloseShape())
		{
			GShape previewShape = new GShape(this.vertices, new Vec3D(), null);
			Shape newShape = new PolygonShape(previewShape);
			this.shapes.add(newShape);
			this.vertices = new ArrayList<Vec2D>();			
		}
		else
		{
			vertices.add(this.lastKnownMousePositon);			
		}
		if (button == PConstants.RIGHT) {
			this.vertices = new ArrayList<Vec2D>();
		}
	}
	
	private boolean mouseOverCloseShape()
	{
		Rect closeShapeRect = new Rect(vertices.get(0).add(-5,-5), vertices.get(0).add(5,5));
		return closeShapeRect.containsPoint(this.lastKnownMousePositon);
	}
	
	@Override
	public void mouseMoved(Vec2D position) {
		this.lastKnownMousePositon = this.positionRelativeToView(position);
	}

	@Override
	public PGraphics getIcon(PGraphics context) {
		context.beginDraw();
		context.noFill();
		context.stroke(0);
		context.strokeWeight(2);

		context.line(50, 10, 120, 14);
		context.line(120, 14, 110, 34);
		context.line(110, 34, 80, 44);
		context.line(80, 44, 50, 22);
		context.line(50, 22, 50, 10);

		context.endDraw();

		return context;
	}

	@Override
	public void draw2D(PGraphics p) {		
		if (vertices.size() > 0)
		{
			drawCloseRect(p);
			for (int i = 0; i < vertices.size() - 1; i++)
			{
				p.line(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i+1).x(), vertices.get(i+1).y());
			}
			p.line(vertices.get(vertices.size()-1).x(), vertices.get(vertices.size()-1).y(), this.lastKnownMousePositon.x(), this.lastKnownMousePositon.y());
		}
		
		super.draw2D(p);
	}

	private void drawCloseRect(PGraphics p) {
		if (mouseOverCloseShape())
		{
			p.stroke(255,0,0);
		}
		else
		{
			p.stroke(0);
		}
		p.rect(vertices.get(0).x() - 5, vertices.get(0).y() - 5, 10, 10);
		p.stroke(0);
	}
	
	@Override
	public void wasSelected() {
		super.wasSelected();
		this.vertices = new ArrayList<Vec2D>();
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}

}
