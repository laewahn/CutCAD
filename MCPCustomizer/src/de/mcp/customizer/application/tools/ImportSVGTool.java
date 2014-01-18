package de.mcp.customizer.application.tools;

import java.util.List;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSVG;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;

public class ImportSVGTool extends Tool {

	private List<Shape> shapes;

	public ImportSVGTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes,
			Transformation transform) {
		super(view, properties, statusbar, transform, "ImportSTLTool");
		this.shapes = shapes;
	}

	@Override
	public void mouseButtonPressed(Vec2D position, int button) {

	}

	@Override
	public void mouseButtonReleased(Vec2D position, int button) {
	}
	
	@Override
	public void mouseMoved(Vec2D position) {
	}

	@Override
	public PGraphics getIcon(PGraphics context) {
		context.beginDraw();
		context.fill(0);
		context.noStroke();
		context.strokeWeight(2);
		context.textSize(26);
		context.text("ImportSVG", 8, 35);

		context.endDraw();

		return context;
	}

	@Override
	public void draw2D(PGraphics p) {
	}

	private void drawCloseRect(PGraphics p) {
	}
	
	@Override
	public void wasSelected() {
		new ImportSVG(shapes);	
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
