package de.mcp.customizer.application.tools.fileManagement;

import java.io.File;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.view.FileDialogDelegate;
import de.mcp.customizer.view.SaveFileDialog;

public class SaveTool extends Tool implements FileDialogDelegate{

	public SaveTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container);
	}
	
	@Override
	public String getIconName() {
		return "Save2.svg";
	}
	
	@Override
	public void toolWasSelected() {
		super.toolWasSelected();
		
		SaveFileDialog dialog = new SaveFileDialog(this);
		dialog.showDialog("Enter the name or select the file you would like to save your project into.");
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
