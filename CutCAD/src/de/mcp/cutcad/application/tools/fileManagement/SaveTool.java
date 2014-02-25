package de.mcp.cutcad.application.tools.fileManagement;

import java.io.File;

import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.view.FileDialogDelegate;
import de.mcp.cutcad.view.SaveFileDialog;

public class SaveTool extends Tool implements FileDialogDelegate{

	public SaveTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
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
