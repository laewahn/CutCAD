package de.mcp.customizer.application.tools;

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
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

	private GShape previewShape;

	private List<Vec2D> vertices;

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

		vertices.add(this.positionRelativeToView(position));
		this.previewShape = new GShape(this.vertices, new Vec3D(), null);

		if (button == PConstants.RIGHT) {
			Shape newShape = new PolygonShape(this.previewShape);
			this.shapes.add(newShape);
			this.vertices = new ArrayList<Vec2D>();
		}
	}

	@Override
	public void mouseMoved(Vec2D position) {

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
		if (this.previewShape != null) {
			this.previewShape.draw2D(p);
		}
		super.draw2D(p);
	}
	
	@Override
	public void wasSelected() {
		super.wasSelected();
		this.vertices = new ArrayList<Vec2D>();
		this.previewShape = null;
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
		Shape newShape = new PolygonShape(this.previewShape);
		this.shapes.add(newShape);
	}

}
