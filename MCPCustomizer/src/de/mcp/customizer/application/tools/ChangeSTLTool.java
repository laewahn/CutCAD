package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;

/**
 * The ChangeSTLTool is used to move and rotate an imported STL mesh in the 3D-view
 */
public class ChangeSTLTool extends Tool {

	private STLMesh mesh;

	/**
	 * Creates a ChangeSTLTool
	 * 
	 * @param customizer the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
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
