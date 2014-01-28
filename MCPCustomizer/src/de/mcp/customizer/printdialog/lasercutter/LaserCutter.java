package de.mcp.customizer.printdialog.lasercutter;

/**
 * This class emulates an enum class of several lasercutters. Processing does not support
 * the use of enum data types.
 */
public class LaserCutter
{
	/** 
	 * Represent which lasercutter is selected
	 */
	private int device;
  
	/**
	 * Creates a new enum emulator and sets the selected lasercutter to match
	 * the parameter of this method.  If the lasercutter in the parameter is not existent or not supported
	 * no lasercutter is selected.
	 * @param device
	 * The selected lasercutter is set to this value
	 */
	public LaserCutter(String device)
	{
		setDevice(device); 
	}
  
	/**
	 * Returns the name of the selected lasercutter.
	 * If no lasercutter was selected or a non existing one was selected,
	 * this method returns "no selected"
	 * 
	 * @return the name of the selected lasercutter
	 */
	public String returnDevice()
	{
		switch (device)
		{
			case 0: return "epilogZing";
			default: return "no selected";
		} 
	}
  
	/**
	 * Return the number associated with the selected lasercutter. 
	 * If no lasercutter was selected or a non existing one was selected,
	 * this method returns -1.
	 * 
	 * @return number associated with the selected lasercutter
	 */
	public int returnDeviceNumber()
	{
		return device;  
	}
	
	/**
	 * Sets the selected lasercutter to the value of the parameter.
	 * If the lasercutter in the parameter is not existent or not supported
	 * the value is set to -1.
	 * 
	 * @param device
	 * the lasercutter to be selected
	 */
	public void setDevice(String device)
	{
	    if(device.equals("epilogZing"))
	    {
	       this.device = 0;
	    } else
	    {
	      this.device = -1; 
	    }
	}
}
