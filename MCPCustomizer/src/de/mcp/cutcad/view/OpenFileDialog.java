package de.mcp.cutcad.view;

import java.io.File;

public class OpenFileDialog extends FileDialog {

	private static final long serialVersionUID = -1309665774730253397L;
	
	public OpenFileDialog(FileDialogDelegate delegate) {
		super(delegate);
	}
	
	public void showDialog(String prompt) {
		selectInput(prompt, "fileSelected");
	}
	
	public void fileSelected(File theFile) {
		super.fileSelected(theFile);
	}
}
