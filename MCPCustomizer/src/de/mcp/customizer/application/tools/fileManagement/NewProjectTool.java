package de.mcp.customizer.application.tools.fileManagement;

import javax.swing.JOptionPane;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;

public class NewProjectTool extends Tool {

	public NewProjectTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "New.svg");
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
