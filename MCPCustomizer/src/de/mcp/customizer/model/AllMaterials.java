package de.mcp.customizer.model;

import java.io.File;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.data.XML;

/**
 * Load different materials from xml files in the folder materials
 */
public class AllMaterials extends PApplet {
	private static final long serialVersionUID = -8801211971447138231L;
	private static ArrayList<Material> materials = new ArrayList<Material>();
	private static int baseMaterialIndex = 0;

	/**
	 * Useless Dummy Constructor
	 */
	public AllMaterials() {
	}

	/**
	 * import Materials from the xml files in the folder materials
	 * 
	 * @param path
	 *            path to the materials folder
	 */
	public void addMaterialsFromFile(String path) {
		if (materials.isEmpty())
			materials.add(new Material("Nothing", 5, color(255, 255, 255, 0),
					0, 100, 0, 500));
		File[] files = new File(path).listFiles();

		// ToDo: make sure load only xml-files
		for (int i = 0; i < files.length; i++) {
			XML material = loadXML(path + "/" + files[i].getName());

			XML identity = material.getChild("identity");
			String name = identity.getContent();

			int redColor = identity.getInt("red");
			int blueColor = identity.getInt("green");
			int greenColor = identity.getInt("blue");
			int alphaChannel = identity.getInt("alpha");
			int materialColor = color(redColor, blueColor, greenColor,
					alphaChannel);

			XML[] differentThickness = material.getChildren("thickness");

			for (int j = 0; j < differentThickness.length; j++) {
				int thickness = (int) (differentThickness[j].getInt("value") / 10);
				int power = differentThickness[j].getChild("cut")
						.getChild("power").getIntContent();
				int speed = differentThickness[j].getChild("cut")
						.getChild("speed").getIntContent();
				int focus = differentThickness[j].getChild("cut")
						.getChild("focus").getIntContent();
				int frequency = differentThickness[j].getChild("cut")
						.getChild("frequency").getIntContent();

				materials.add(new Material(name, thickness, materialColor,
						power, speed, focus, frequency));
			}
		}
	}

	/**
	 * ToDo: import materials from the normal visicut material files (not
	 * implemented yet)
	 */
	public void addMaterialsFromVisicutFile() {
		// TODO: get the material file from visicut and add them to materials
	}

	/**
	 * @return list of all materials
	 */
	public static ArrayList<Material> getMaterials() {
		return materials;
	}

	/**
	 * @param material to be used as a standard material (which is used for new created shapes)
	 */
	public static void setBaseMaterial(Material material) {
		baseMaterialIndex = materials.indexOf(material);
	}

	/**
	 * @return the material, which is used as a standard material (which is used for new created shapes)
	 */
	public static Material getBaseMaterial() {
		return materials.get(baseMaterialIndex);
	}
}