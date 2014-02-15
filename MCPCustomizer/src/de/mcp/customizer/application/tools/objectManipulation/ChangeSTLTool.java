package de.mcp.customizer.application.tools.objectManipulation;

import processing.core.PGraphics;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Transformation;

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
	public void mouseButtonPressed(Vector2D position, int button) {

	}

	@Override
	public void mouseButtonReleased(Vector2D position, int button) {
	}
	
	@Override
	public void mouseMoved(Vector2D position) {
	}

	@Override
	public void draw2D(PGraphics p, Transformation t) {
	}
	
	@Override
	public void wasSelected() {
		if(mesh.isStlImported())
		{
			this.customizer.properties.show();
			this.customizer.properties.plugTo(mesh);
		}
		super.wasSelected();
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
