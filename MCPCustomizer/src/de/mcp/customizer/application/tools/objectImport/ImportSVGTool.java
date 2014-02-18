package de.mcp.customizer.application.tools.objectImport;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSVG;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.model.primitives.Vector3D;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.OpenFileDialog;
import de.mcp.customizer.view.Transformation;

/**
 * The ImportSVGTool is used to import Shapes from SVG-Files.
 */
public class ImportSVGTool extends Tool implements FileDialogDelegate {
	
	private ArrayList<Shape> shapes;
	private Vector2D originalMousePosition;
	
	/**
	 * @param customizer the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ImportSVGTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "LoadSVG.svg");
		this.shapes = new ArrayList<Shape>();
		this.originalMousePosition = new Vector2D(0, 0);
	}
	
	@Override
	public void mouseButtonPressed(Vector2D position, int button) {
		if (!shapes.isEmpty())
		{
			for (Shape s : shapes)
			{
				this.objectContainer.addShape(s);
			}
		}
		this.wasUnselected();
	}

	@Override
	public void mouseButtonReleased(Vector2D position, int button) {
	}
	
	@Override
	public void mouseMoved(Vector2D position) {
		Vector2D currentMousePosition = this.positionRelativeToView(position);
		Vector2D translationVector = currentMousePosition.sub(originalMousePosition);
		for (Shape s : shapes)
		{
			s.getGShape().translate2D(translationVector);
			s.getGShape().translate3D(new Vector3D(translationVector.x(), translationVector.y(), 0));
		}
		originalMousePosition.set(currentMousePosition);
	}
	
	@Override
	public void draw2D(PGraphics p, Transformation t) {
		for (Shape s : shapes)
		{
			s.draw2D(p, t);
		}
	}
	
	@Override
	public void wasSelected() {
		super.wasSelected();		
		
		OpenFileDialog dialog = new OpenFileDialog(this);
		dialog.showDialog("Select a SVG file to process:");
	}
	
	
	
	@Override
	public void wasUnselected()
	{
		this.shapes.clear();
		this.originalMousePosition = new Vector2D(0,0);
		super.wasUnselected();
	}

	@Override
	public boolean canStaySelected() {
		return true;
	}

	@Override
	public void fileWasSelected(File theFile) {
		if (PApplet.checkExtension(theFile.getAbsolutePath()).equals("svg")) {
			ImportSVG svgImporter = new ImportSVG();
			shapes.addAll(svgImporter.createPathsFromSVG(theFile));
		}
	}
}
