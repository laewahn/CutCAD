package de.mcp.customizer.application.tools;

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
		
		SaveFileDialog dialog = new SaveFileDialog(this);
		dialog.showDialog();
		
		this.wasUnselected();
	}

	@Override
	public void fileWasSelected(File theFile) {
		this.objectContainer.save(theFile);		
	}

}
