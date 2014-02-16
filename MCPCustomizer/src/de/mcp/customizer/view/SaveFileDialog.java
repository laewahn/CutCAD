package de.mcp.customizer.view;

import java.io.File;

public class SaveFileDialog extends FileDialog {

	private static final long serialVersionUID = 4309010062997224938L;
	
	public SaveFileDialog(FileDialogDelegate delegate) {
		super(delegate);
	}
	
	public void showDialog(String prompt) {
		selectOutput(prompt, "fileSelected");
	}
	
	public void fileSelected(File theFile) {
		super.fileSelected(theFile);
	}
}
