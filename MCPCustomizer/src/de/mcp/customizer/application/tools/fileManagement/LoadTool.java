package de.mcp.customizer.application.tools.fileManagement;

import java.io.File;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.OpenFileDialog;

public class LoadTool extends Tool implements FileDialogDelegate {

	public LoadTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "Load2.svg");
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
	public void wasSelected() {
		super.wasSelected();
		
		OpenFileDialog openDialog = new OpenFileDialog(this);
		openDialog.showDialog("Select a file to load.");
	}

	@Override
	public void fileWasSelected(File theFile) {
		this.objectContainer.load(theFile);
	}
	
	@Override
	public boolean canStaySelected() {
		return false;
	}
}
