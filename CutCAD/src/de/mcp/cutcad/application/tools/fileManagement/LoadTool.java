package de.mcp.cutcad.application.tools.fileManagement;

import java.io.File;

import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.view.FileDialogDelegate;
import de.mcp.cutcad.view.OpenFileDialog;

public class LoadTool extends Tool implements FileDialogDelegate {

	public LoadTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
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
