package de.mcp.customizer.view;

import java.io.File;

import processing.core.PApplet;

public class OpenFileDialog extends PApplet {

	private static final long serialVersionUID = -1309665774730253397L;
	
	private FileDialogDelegate delegate;
	private String dialogPrompt;
	
	public OpenFileDialog(FileDialogDelegate delegate) {
		this("Select a file to load.", delegate);
	}
	
	public OpenFileDialog(String dialogPrompt, FileDialogDelegate delegate) {
		this.delegate = delegate;
		this.dialogPrompt = dialogPrompt;
	}
	
	public void showDialog() {
		selectInput(this.dialogPrompt, "fileSelected");
	}
	
	public void fileSelected(File theFile) {
		if(theFile != null) {
			delegate.fileWasSelected(theFile);
		}
	}
}
