class Material 
{
	private String name;
	private int thickness;
	private int materialColor;
	private int power;
	private int speed;
	private int focus;
	private int frequency;
  
  	Material(String materialName, int thickness, int materialColor, int power, int speed, int focus, int frequency)
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
		return (name + " " + thickness + " mm");
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

