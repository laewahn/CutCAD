package de.mcp.customizer.application.tools.fileManagement;

import java.io.File;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.SaveFileDialog;

public class SaveTool extends Tool implements FileDialogDelegate{

	public SaveTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "Save2.svg");
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
		
		SaveFileDialog dialog = new SaveFileDialog("Enter the name or select the file you would like to save your project into.", this);
		dialog.showDialog();
	}

	@Override
	public void fileWasSelected(File theFile) {
		this.objectContainer.save(theFile);		
	}

	@Override
	public boolean canStaySelected() {
		return false;
	}
}
