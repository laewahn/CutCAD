package de.mcp.cutcad.application.tools.objectImport;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ImportSVG;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.model.primitives.Vector3D;
import de.mcp.cutcad.view.FileDialogDelegate;
import de.mcp.cutcad.view.OpenFileDialog;
import de.mcp.cutcad.view.Transformation;

/**
 * The ImportSVGTool is used to import Shapes from SVG-Files.
 */
public class ImportSVGTool extends Tool implements FileDialogDelegate {
	
	private ArrayList<Shape> shapes;
	private Vector2D originalMousePosition;
	
	/**
	 * @param application the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ImportSVGTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
		this.shapes = new ArrayList<Shape>();
		this.originalMousePosition = new Vector2D(0, 0);
	}
	
	@Override
	public String getIconName() {
		return "LoadSVG.svg";
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
		this.toolWasUnselected();
	}

	@Override
	public void mouseMoved(Vector2D position) {
		Vector2D currentMousePosition = view.positionRelativeToView(position);
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
	public void toolWasSelected() {
		super.toolWasSelected();		
		
		OpenFileDialog dialog = new OpenFileDialog(this);
		dialog.showDialog("Select a SVG file to process:");
	}
	
	@Override
	public void toolWasUnselected()
	{
		this.shapes.clear();
		this.originalMousePosition = new Vector2D(0,0);
		super.toolWasUnselected();
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
