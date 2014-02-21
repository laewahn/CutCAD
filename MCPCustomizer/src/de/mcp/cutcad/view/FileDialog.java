package de.mcp.cutcad.view;

import java.io.File;

import processing.core.PApplet;

public abstract class FileDialog extends PApplet {

	private static final long serialVersionUID = 1L;
	
	private FileDialogDelegate delegate;
	
	public FileDialog(FileDialogDelegate delegate) {
		this.delegate = delegate;
	}
	
	public FileDialogDelegate getDelegate() {
		return this.delegate;
	}
	
	public abstract void showDialog(String prompt);
	
	protected void fileSelected(File theFile) {
		if (theFile != null) {
			delegate.fileWasSelected(theFile);
		}
	}
}
