package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
import toxi.geom.Vec2D;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSTL;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;

public class ImportSTLTool extends Tool {

	private STLMesh mesh;

	public ImportSTLTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "LoadSTL.svg");
		this.mesh = customizer.meshSTL;
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
		new ImportSTL(mesh);	
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
