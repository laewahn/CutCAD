package de.mcp.customizer.printdialog.selectpath;

import java.io.File;

/**
 * This is a interface containing a callback function for the SelectPathDialog class.
 * The selected path is returned as parameter of the callback function.
 * 
 * @author Pierre
 *
 */
interface SelectPathDialogDelegate {
	
	/**
	 * The callback function with which the selected path from SelectPathDialog is returned.
	 * The selected path is contained in the parameter selectedFile.
	 * 
	 * @param selectedFile
	 */
	public void pathSelected(File selectedFile);

}
