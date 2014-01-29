package de.mcp.customizer.application.tools;

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.PolygonShape;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;

public class PolygonTool extends Tool {
	
	private List<Vec2D> vertices;
	
	private Vec2D lastKnownMousePositon;
	private float scalingFactor, boundingBoxSize;


	public PolygonTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "DrawPolygon.svg");
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
			this.customizer.displayStatus("Shape finished! If you want to create another shape, click the left mousebutton anywhere on the 2D view");
			Shape newShape = new PolygonShape(this.vertices, new Vec3D());
			this.objectContainer.addShape(newShape);
			this.vertices = new ArrayList<Vec2D>();			
		}
		else
		{
			this.customizer.displayStatus("Point added! To add another point, click anywhere on the 2D view. To finish the shape, click on the first point of the shape");
			vertices.add(this.lastKnownMousePositon);			
		}
		if (button == PConstants.RIGHT) {
			this.vertices = new ArrayList<Vec2D>();
		}
	}
	
	private boolean mouseOverCloseShape()
	{
		Rect closeShapeRect = new Rect(vertices.get(0).add(-boundingBoxSize,-boundingBoxSize), vertices.get(0).add(boundingBoxSize,boundingBoxSize));
		return closeShapeRect.containsPoint(this.lastKnownMousePositon);
	}
	
	@Override
	public void mouseMoved(Vec2D position) {
		this.lastKnownMousePositon = this.positionRelativeToView(position);
        this.customizer.displayMousePosition(lastKnownMousePositon.scale(0.1f));
	}

	@Override
	public void draw2D(PGraphics p, Transformation t) {	
		scalingFactor = t.getScale();

		boundingBoxSize = 4/scalingFactor;
		if (vertices.size() > 0)
		{
			drawCloseRect(p);
			for (int i = 0; i < vertices.size() - 1; i++)
			{
				p.line(vertices.get(i).scale(scalingFactor).x(), vertices.get(i).scale(scalingFactor).y(), vertices.get(i+1).scale(scalingFactor).x(), vertices.get(i+1).scale(scalingFactor).y());
			}
			p.line(vertices.get(vertices.size()-1).scale(scalingFactor).x(), vertices.get(vertices.size()-1).scale(scalingFactor).y(), this.lastKnownMousePositon.scale(scalingFactor).x(), this.lastKnownMousePositon.scale(scalingFactor).y());
		}
		
		super.draw2D(p, t);
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
		p.noFill();
		p.rect(vertices.get(0).scale(scalingFactor).x() - boundingBoxSize, vertices.get(0).scale(scalingFactor).y() - boundingBoxSize, 10, 10);
		p.stroke(0);
	}
	
	@Override
	public void wasSelected() {
		this.customizer.displayStatus("To start drawing a shape, click the left mousebutton anywhere on the 2D view");
		this.vertices = new ArrayList<Vec2D>();
		super.wasSelected();
	}
	
	@Override
	public void wasUnselected() {
		this.customizer.displayStatus("");
		super.wasUnselected();
	}

}
