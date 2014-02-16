package de.mcp.customizer.application.tools.objectImport;

import java.io.File;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.OpenFileDialog;
import de.mcp.customizer.view.Transformation;

/**
 * The ImportSTLTool is used to import an STL-mesh into the 3D-view. Once
 * imported, the STL-mesh can be moved and rotated with the ChangeSTLTool. Only
 * one STL-mesh can be loaded at a time.
 */
public class ImportSTLTool extends Tool implements FileDialogDelegate {

	private STLMesh mesh;

	/**
	 * @param customizer
	 *            the main class of the project
	 * @param container
	 *            the currently loaded ObjectContainer
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
		super.wasSelected();

		OpenFileDialog dialog = new OpenFileDialog("Select a STL file to process:", this);
		dialog.showDialog();
	}

	@Override
	public boolean canStaySelected() {
		return false;
	}

	@Override
	public void fileWasSelected(File theFile) {
		if (theFile != null) {
			if (PApplet.checkExtension(theFile.getAbsolutePath()).equals("stl")) {
				String filePath = theFile.getPath();
				TriangleMesh loadedMesh = importMeshFromPath(filePath);
				this.mesh.setSTLMesh(loadedMesh.scale(20));
			}
		}
	}
	
	private TriangleMesh importMeshFromPath(String thePath) {
		STLReader stlReader = new STLReader();
		TriangleMesh theMesh = (TriangleMesh) stlReader.loadBinary(thePath, STLReader.TRIANGLEMESH);
		
		return theMesh;
	}
}
