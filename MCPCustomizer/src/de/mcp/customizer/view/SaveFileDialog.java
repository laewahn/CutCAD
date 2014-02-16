package de.mcp.customizer.view;

import java.io.File;

import processing.core.PApplet;

public class SaveFileDialog extends PApplet {

	private static final long serialVersionUID = 4309010062997224938L;
	
	private FileDialogDelegate delegate;
	private String dialogPrompt;
	
	public SaveFileDialog(String dialogPrompt, FileDialogDelegate delegate) {
		this.delegate = delegate;
		this.dialogPrompt = dialogPrompt;
	}
	
	public void showDialog() {
		selectOutput(this.dialogPrompt, "fileSelected");
	}
	
	public void fileSelected(File theFile) {
		if (theFile != null) {
			delegate.fileWasSelected(theFile);
		}
	}
}
