package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;

public class ChangeSTLTool extends Tool {

	private STLMesh mesh;

	public ChangeSTLTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "MoveSTL.svg");
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
		if(mesh.isStlImported())
		{
			this.customizer.properties.show();
			this.customizer.properties.plugTo(mesh);
		}
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
