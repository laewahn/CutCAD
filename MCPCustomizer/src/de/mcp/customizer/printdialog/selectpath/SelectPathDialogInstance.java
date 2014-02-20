package de.mcp.customizer.printdialog.selectpath;

import java.io.File;

/**
 * The instance that handles selecting a path.
 * This class is the interface for selecting a path.
 * It creates the selectPathDialogWindow and receives the selected path via a callback function.
 * After a path has been selected, the selected path can be queried by the getSelectedPath function.
 * 
 * @author Pierre
 *
 */
public class SelectPathDialogInstance implements SelectPathDialogDelegate {

	/**
	 * The path which was selected from the selectPathDialog window.
	 */
	File selectedPath;
	
	/**
	 * Creates and shows the selectPathDialogWindow.
	 */
	public void showSelectPathDialog() {
		SelectPathDialog saveDialog = new SelectPathDialog(this);
		saveDialog.showDialog("Select a path to where the SVG's are exported.");
	}
	
	@Override
	public void pathSelected(File selectedFile) {
		this.selectedPath = selectedFile;	
	}
	
	/**
	 * Returns the path which was selected during the selectPathDialog
	 * 
	 * @return
	 * The path selected during the selectPathDialog.
	 */
	public File getSelectedPath() {
		return selectedPath;
	}

}
