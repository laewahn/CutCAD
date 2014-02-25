package de.mcp.cutcad.printdialog.selectpath;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 * Handles the select a path dialog.
 * By calling the showDialog method of this class, a dialog is created to select a directory.
 * Only directories can be selected. When a directory is chosen, the path to the directory
 * is returned in a call back function to the delegate as an File object.
 * 
 * @author Pierre
 *
 */
class SelectPathDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5621437396629850511L;
	
	/**
	 * The object responsible for the select path dialog
	 */
	JFileChooser chooser;
	
	/**
	 * The object to which the callback function should be delegated to.
	 * The callback function is called when a path is chosen.
	 */
	SelectPathDialogDelegate delegate;
	
	/**
	 * The constructor for the selectPathDialog
	 * 
	 * @param delegate
	 * The object to which the selected path is delegated.
	 * The selected path is delegated as a File object by a callback function.
	 */
	SelectPathDialog(SelectPathDialogDelegate delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * This method creates the selectPathWindow. 
	 * Only directories can be selected with the created select dialog.
	 * The title bar of the window will be the value of the parameter title.
	 * When a path is selected, the path is returned as parameter of
	 * the callback function pathSelected() of the object delegate.
	 * 
	 * @param title
	 * The title the selectPathDialog should have.
	 * The title is displayed in the title bar of the window.
	 */
	void showDialog(String title) {
		
		chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle(title);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setFileFilter(new FileFilter(){

	    	public String getDescription() {
                return "Directories only";
            }

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}

        });
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) { 
	    	this.delegate.pathSelected(chooser.getSelectedFile());
	    } else {
	    	System.out.println("no directory has been chosen");
	    }
	}
}
