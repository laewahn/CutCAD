package de.mcp.customizer.application.tools.objectImport;

import java.io.File;

import processing.core.PApplet;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import de.mcp.customizer.application.CutCADApplet;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.OpenFileDialog;

/**
 * The ImportSTLTool is used to import an STL-mesh into the 3D-view. Once
 * imported, the STL-mesh can be moved and rotated with the ChangeSTLTool. Only
 * one STL-mesh can be loaded at a time.
 */
public class ImportSTLTool extends Tool implements FileDialogDelegate {

	private STLMesh mesh;

	/**
	 * @param application
	 *            the main class of the project
	 * @param container
	 *            the currently loaded ObjectContainer
	 */
	public ImportSTLTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
		this.mesh = container.getSTLMesh();
	}

	@Override
	public String getIconName() {
		return "LoadSTL.svg";
	}
	
	@Override
	public void toolWasSelected() {
		super.toolWasSelected();

		OpenFileDialog dialog = new OpenFileDialog(this);
		dialog.showDialog("Select a STL file to process:");
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
