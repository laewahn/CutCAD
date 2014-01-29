package de.mcp.customizer.model;

import java.io.Serializable;

/**
 * Material parameter -Color, Name and lasercutter settings
 */
public class Material implements Serializable {
	
	private static final long serialVersionUID = 6426094075988228422L;
	
	private String name;
	private int thickness;
	private int materialColor;
	private int power;
	private int speed;
	private int focus;
	private int frequency;

	/**
	 * @param materialName
	 *            (Name of material)
	 * @param thickness
	 *            (0.1mm)
	 * @param materialColor
	 *            (color)
	 * @param power
	 *            (%)
	 * @param speed
	 *            (%)
	 * @param focus (whatever)
	 * @param frequency
	 *            (Hz)
	 */
	public Material(String materialName, int thickness, int materialColor,
			int power, int speed, int focus, int frequency) {
		this.name = materialName;
		this.materialColor = materialColor;
		this.thickness = thickness;
		this.power = power;
		this.speed = speed;
		this.focus = focus;
		this.frequency = frequency;
	}

	/**
	 * Get the name of the material, combined with its thickness
	 * 
	 * @return Name of the material
	 */
	public String getMaterialName() {
		return (name + " " + thickness / 10 + "," + thickness % 10 + " mm");
	}

	/**
	 * Get the thickness of the material
	 * 
	 * @return Thickness of material (0.1mm)
	 */
	public int getMaterialThickness() {
		return thickness;
	}

	/**
	 * Color of the material - A Hint of the real color, distinguish different
	 * materials
	 * 
	 * @return A color
	 */
	public int getMaterialColor() {
		return materialColor;
	}

	/**
	 * Get lasercutter setting: Power
	 * 
	 * @return power (%)
	 */
	public int getPower() {
		return power;
	}

	/**
	 * Get lasercutter setting: Speed
	 * 
	 * @return speed (%)
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Get lasercutter setting: Focus
	 * 
	 * @return focus
	 */
	public int getFocus() {
		return focus;
	}

	/**
	 * Get lasercutter setting: Frequency
	 * 
	 * @return frequency (Hz)
	 */
	public int getFrequency() {
		return frequency;
	}
}
