package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
//import toxi.geom.Vector2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSTL;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Transformation;

/**
 * The ImportSTLTool is used to import an STL-mesh into the 3D-view.
 * Once imported, the STL-mesh can be moved and rotated with the ChangeSTLTool.
 * Only one STL-mesh can be loaded at a time.
 */
public class ImportSTLTool extends Tool {

	private STLMesh mesh;

	/**
	 * @param customizer the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ImportSTLTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "LoadSTL.svg");
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
		new ImportSTL(mesh);	
		super.wasSelected();
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
