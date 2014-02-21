package de.mcp.customizer.application.tools.fileManagement;

import de.mcp.customizer.application.CutCADApplet;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
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
	 * @param application 
	 * The main class of the project
	 * 
	 * @param container
	 */
	public PrintTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
	}
	
	@Override
	public String getIconName() {
		return "Print.svg";
	}
	    
    @Override
    /**
     * This method handles the event handler when the tool was selected. When this tool was
     * selected it calls methods to prepare the print dialog.
     */
    public void toolWasSelected() {
    	super.toolWasSelected();
    	PrintDialog printDialog = new PrintDialog(this.objectContainer.allShapes());
    	printDialog.preparePrintDialog();
    }
    
    @Override
	public boolean canStaySelected() {
		return false;
	}
}
