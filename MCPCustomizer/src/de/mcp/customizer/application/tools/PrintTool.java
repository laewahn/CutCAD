package de.mcp.customizer.application.tools;

import toxi.geom.Vec2D;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.printdialog.PrintDialog;

public class PrintTool extends Tool {
	
	public PrintTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "Print.svg");
	}
	
    public void mouseButtonPressed(Vec2D position, int button)
    {
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
    }
    
    public void mouseMoved(Vec2D position)
    {
    }
    
    @Override
    public void wasSelected() {
    	super.wasSelected();
    	PrintDialog printDialog = new PrintDialog(this.objectContainer.allShapes());
    	printDialog.preparePrintDialog();
    }
}
