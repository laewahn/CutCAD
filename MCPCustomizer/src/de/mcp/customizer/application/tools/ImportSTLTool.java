package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSTL;
import de.mcp.customizer.view.Transformation;

public class ImportSTLTool extends Tool {

	private MCPCustomizer parent;

	public ImportSTLTool(Rect view, Properties properties, Statusbar statusbar, MCPCustomizer parent, Transformation transform) {
		super(view, properties, statusbar, transform, "ImportSTLTool");
		this.parent = parent;
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
		context.text("ImportSTL", 8, 35);

		context.endDraw();

		return context;
	}

	@Override
	public void draw2D(PGraphics p) {
	}
	
	@Override
	public void wasSelected() {
		new ImportSTL(parent);	
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
