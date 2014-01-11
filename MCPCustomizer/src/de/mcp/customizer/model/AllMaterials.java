package de.mcp.customizer.model;
import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.data.XML;

public class AllMaterials extends PApplet
{
	private static final long serialVersionUID = -8801211971447138231L;
	
	private static ArrayList<Material> materials = new ArrayList<Material>();

	public AllMaterials()
	{
		if(materials.isEmpty()) materials.add(new Material("Nothing", 5, color(255,255,255,0), 0, 100, 0, 500));
	}

	public void addMaterialsFromFile(String path)
	{
		File[] files= new File(path).listFiles();

		//make sure load only xml
		for(int i=0; i<files.length; i++) 
		{
			XML material = loadXML(path + "/" + files[i].getName());

			XML identity = material.getChild("identity");
			String      name = identity.getContent();

			int     redColor = identity.getInt("red");
			int    blueColor = identity.getInt("green");
			int   greenColor = identity.getInt("blue");
			int alphaChannel = identity.getInt("alpha");
			int materialColor = color(redColor, blueColor, greenColor, alphaChannel);

			XML[] differentThickness = material.getChildren("thickness");

			for (int j=0; j<differentThickness.length; j++)
			{
				int thickness = (int)(differentThickness[j].getInt("value")/10);
				int     power = differentThickness[j].getChild("cut").getChild("power").getIntContent();
				int     speed = differentThickness[j].getChild("cut").getChild("speed").getIntContent();
				int     focus = differentThickness[j].getChild("cut").getChild("focus").getIntContent();
				int frequency = differentThickness[j].getChild("cut").getChild("frequency").getIntContent();

				materials.add(new Material(name, thickness, materialColor, power, speed, focus, frequency));
			}
		}
	}

	public void addMaterialsFromVisicutFile()
	{
		//TODO: get the material file from visicut and add them to materials
	}

	public static ArrayList<Material> getMaterials()
	{
		return materials;
	}
}

