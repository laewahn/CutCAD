package de.mcp.customizer.model;

import java.io.File;

import processing.core.PApplet;
import toxi.geom.mesh.*;

public class ImportSTL extends PApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	STLMesh mesh;

	public ImportSTL(STLMesh mesh) 
	{
		this.mesh = mesh;
		selectInput("Select a STL file to process:", "fileSelected");
	}

	public void fileSelected(File selection) 
	{
		if (selection == null) 
		{
			println("No file selected");
		} else {
			if (checkExtension(selection.getAbsolutePath()).equals("stl"))
			{
				System.out.println(selection.toString());
				this.mesh.setSTLMesh(((TriangleMesh)new STLReader().loadBinary(selection.toString(),STLReader.TRIANGLEMESH)).scale(20));
			}
			else
			{
				println("NoSTL");  
			}
		}
	}





}
