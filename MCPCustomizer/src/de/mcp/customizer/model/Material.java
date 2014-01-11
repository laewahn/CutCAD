package de.mcp.customizer.model;
public class Material 
{
	private String name;
	private int thickness;
	private int materialColor;
	private int power;
	private int speed;
	private int focus;
	private int frequency;
  
  	public Material(String materialName, int thickness, int materialColor, int power, int speed, int focus, int frequency)
	{
		this.name = materialName;
		this.materialColor = materialColor;
		this.thickness = thickness;
		this.power = power;
		this.speed = speed;
		this.focus = focus;
		this.frequency = frequency;
	}

	public String getMaterialName()
	{
		return (name + " " + thickness/10 + "," + thickness%10 + " mm");
	}

	public int getMaterialThickness()
	{
		return thickness;
	}

	public int getMaterialColor()
	{
		return materialColor;
	}

	public int getPower()
	{
	 	return power;
	}

	public int getSpeed()
	{
	 	return speed;
	}

	public int getFocus()
	{
	 	return focus;
	}

	public int getFrequency()
	{
	 	return frequency;
	}
}

