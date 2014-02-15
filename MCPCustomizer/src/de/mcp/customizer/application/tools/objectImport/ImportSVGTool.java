package de.mcp.customizer.application.tools.objectImport;

import processing.core.PGraphics;
//import toxi.geom.Vector2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSVG;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Transformation;

/**
 * The ImportSVGTool is used to import Shapes from SVG-Files.
 */
public class ImportSVGTool extends Tool {

	/**
	 * @param customizer the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ImportSVGTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "LoadSVG.svg");
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
		new ImportSVG(this.objectContainer);		
		super.wasSelected();
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
