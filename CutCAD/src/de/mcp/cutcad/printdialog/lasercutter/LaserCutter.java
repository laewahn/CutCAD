package de.mcp.cutcad.printdialog.lasercutter;

/**
 * This class emulates an enum class of several lasercutters. Processing does not support
 * the use of enum data types. This class has the probability to return the bed width/height
 * of the lasercutter and the possible dpi settings.
 */
public class LaserCutter {
	/** 
	 * Represent which lasercutter is selected
	 */
	private int device;
  
	/**
	 * Creates a new enum emulator and sets the selected lasercutter to match
	 * the parameter of this method. If the lasercutter in the parameter is not existent or not supported
	 * no lasercutter is selected.
	 * @param device
	 * The selected lasercutter is set to this value
	 */
	public LaserCutter(String device) {
		setDevice(device); 
	}
  
	/**
	 *  Creates a new enum emulator and sets the selected lasercutter to none selected
	 */
	public LaserCutter() {
		this.device = -1;
	}

	/**
	 * Returns the name of the selected lasercutter.
	 * If no lasercutter was selected or a non existing one was selected,
	 * this method returns "no selected"
	 * 
	 * @return the name of the selected lasercutter
	 */
	public String returnDevice() {
		switch (device) {
			case 0: return "epilogZing";
			
			case 1: return "epilogHelix";
			
			case 2: return "laosCutter";
			
			case 3: return "lasersaur"; 
			
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
	public int returnDeviceNumber() {
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
	public void setDevice(String device) {
	    if(device.equals("epilogZing")) {
	    	this.device = 0;
	    } else if(device.equals("epilogHelix")) {
	    	this.device = 1;
	    } else if(device.equals("laosCutter")) {
	    	this.device = 2;
	    } else if(device.equals("lasersaur")) {
	    	this.device = 3;
	    } else {
	    	this.device = -1; 
	    }
	}
	
	/**
	 * Sets the selected lasercutter to the lasercutter associated with the int value of the parameter.
	 * To set select no lasercutter, calls this method with parameter value -1. When a value corresponding
	 * to no lasercutter is provided, the selected lasercutter is set to no selected (-1).
	 * 
	 * @param device
	 * the number associated with the lasercutter to be selected
	 */
	public void setDevice(int device) {
		if(device > 3) {
			this.device = -1;
		} else {
			this.device = device;
		}
	}
	
	/**
	 * This method returns the width of the bed of the selected laser cutter in mm.
	 * 
	 * @return
	 * The width of the selected lasercutter in mm
	 */
	public int returnBedWidth() {
		
		switch (this.device) {
			case 0: return 600;
		
			case 1: return 600;
		
			case 2: return 300;
		
			case 3: return 250; 
		
			default: return 600;
		} 
	}
	
	/**
	 * This method returns the height of the bed of the selected laser cutter in mm.
	 * 
	 * @return
	 * The height of the selected lasercutter in mm
	 */
	public int returnBedHeight() {
		
		switch (this.device) {
			case 0: return 300;
		
			case 1: return 300;
		
			case 2: return 210;
		
			case 3: return 280; 
		
			default: return 300;
		} 
	}
	
	/**
	 * This method returns the possible dpi settings of the selected laser cutter.
	 * 
	 * @return
	 * The possible dpi settings for the selected laser cutter in an array.
	 */
	public final double[] returnDPI() {
		
		switch (this.device) {
			case 0: return new double[] {100, 200, 250, 400, 500, 1000};
		
			case 1: return new double[] {75, 150, 200, 300, 400, 600, 1200};
		
			case 2: return new double[] {100, 200, 300, 500, 600, 1000, 1200};
		
			case 3: return new double[] {500};
		
			default: return new double[] {500};
		} 
	};
}
