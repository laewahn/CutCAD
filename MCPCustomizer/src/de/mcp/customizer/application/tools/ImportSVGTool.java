package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
import toxi.geom.Vec2D;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSVG;
import de.mcp.customizer.model.ObjectContainer;

public class ImportSVGTool extends Tool {

	public ImportSVGTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "LoadSVG.svg");
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
	public void draw2D(PGraphics p) {
	}
	
	@Override
	public void wasSelected() {
		new ImportSVG(this.objectContainer.allShapes());	
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
