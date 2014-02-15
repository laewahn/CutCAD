package de.mcp.customizer.view;

import java.io.File;

import processing.core.PApplet;

public class OpenFileDialog extends PApplet {

	private static final long serialVersionUID = -1309665774730253397L;
	
	private FileDialogDelegate delegate;
	
	public OpenFileDialog(FileDialogDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void showDialog() {
		selectInput("Select a file to load.", "fileSelected");
	}
	
	public void fileSelected(File theFile) {
		if(theFile != null) {
			delegate.fileWasSelected(theFile);
		}
	}
}
