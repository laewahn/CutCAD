package de.mcp.customizer.application.tools.fileManagement;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.printdialog.PrintDialog;

/**
 * The PrintTool is used to start the print dialog.
 * It entails the event handler when the tool was selected.
 * It main concern is to start the print dialog after the tool was selected.
 * 
 * @author Pierre
 *
 */
public class PrintTool extends Tool {
	
	/**
	 * 
	 * @param customizer 
	 * The main class of the project
	 * 
	 * @param container
	 */
	public PrintTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "Print.svg");
	}
	
	/**
	 * This method is not used in this class
	 */
    public void mouseButtonPressed(Vector2D position, int button)
    {
    }

    /**
	 * This method is not used in this class
	 */
    public void mouseButtonReleased(Vector2D position, int button)
    {
    }
    
    /**
	 * This method is not used in this class
	 */
    public void mouseMoved(Vector2D position)
    {
    }
    
    @Override
    /**
     * This method handles the event handler when the tool was selected. When this tool was
     * selected it calls methods to prepare the print dialog.
     */
    public void wasSelected() {
    	super.wasSelected();
    	PrintDialog printDialog = new PrintDialog(this.objectContainer.allShapes());
    	printDialog.preparePrintDialog();
    }
    
    @Override
	public boolean canStaySelected() {
		return false;
	}
}
