package de.mcp.customizer.view;

import java.io.File;

import processing.core.PApplet;

public class SaveFileDialog extends PApplet {

	private static final long serialVersionUID = 4309010062997224938L;
	
	private FileDialogDelegate delegate;
	
	public SaveFileDialog(FileDialogDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void showDialog() {
		selectOutput("Enter the name or select the file you would like to save your project into.", "fileSelected");
	}
	
	public void fileSelected(File theFile) {
		if (theFile != null) {
			delegate.fileWasSelected(theFile);
		}
	}
}
