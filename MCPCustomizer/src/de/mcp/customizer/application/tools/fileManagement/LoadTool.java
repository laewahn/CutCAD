package de.mcp.customizer.application.tools.fileManagement;

import java.io.File;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.OpenFileDialog;

public class LoadTool extends Tool implements FileDialogDelegate {

	public LoadTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container);
	}

	@Override
	public String getIconName() {
		return "Load2.svg";
	}
		
	@Override
	public void toolWasSelected() {
		super.toolWasSelected();
		
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
