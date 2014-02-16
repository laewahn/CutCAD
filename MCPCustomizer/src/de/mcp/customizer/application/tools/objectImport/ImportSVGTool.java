package de.mcp.customizer.application.tools.objectImport;

import java.io.File;

import processing.core.PApplet;
import processing.core.PGraphics;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSVG;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.OpenFileDialog;
import de.mcp.customizer.view.Transformation;

/**
 * The ImportSVGTool is used to import Shapes from SVG-Files.
 */
public class ImportSVGTool extends Tool implements FileDialogDelegate {
	
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
		super.wasSelected();		
		
		OpenFileDialog dialog = new OpenFileDialog(this);
		dialog.showDialog("Select a SVG file to process:");
	}
	
	@Override
	public boolean canStaySelected() {
		return false;
	}

	@Override
	public void fileWasSelected(File theFile) {
		if (PApplet.checkExtension(theFile.getAbsolutePath()).equals("svg")) {
			ImportSVG svgImporter = new ImportSVG(this.objectContainer);
			svgImporter.createPathsFromSVG(theFile);
		}
	}
}
