package de.mcp.cutcad.application.tools.fileManagement;

import javax.swing.JOptionPane;

import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ObjectContainer;

public class NewProjectTool extends Tool {

	public NewProjectTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
	}

	@Override
	public String getIconName() {
		return "New.svg";
	}
	
	@Override
	public void toolWasSelected() {
		super.toolWasSelected();
		
		int confirmation = JOptionPane.showConfirmDialog(null, "Unsaved changes will be lost. Continue?");
		
		if (confirmation == JOptionPane.OK_OPTION) {
			this.objectContainer.clear();			
		}
	}

	@Override
	public boolean canStaySelected() {
		return false;
	}
}
