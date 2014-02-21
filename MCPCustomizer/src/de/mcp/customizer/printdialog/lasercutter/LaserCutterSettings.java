package de.mcp.customizer.printdialog.lasercutter;

public class LaserCutterSettings {
	
	private LaserCutter selectedCutter;
	private int DPI = 0;
	private String address;

	public LaserCutterSettings() {
		this.selectedCutter = new LaserCutter();
		this.DPI = 0;
		this.address = "";
	}
	
	public LaserCutter getSelectedCutter() {
		return this.selectedCutter;
	}

	public void selectCutter(LaserCutter selectedCutter) {
		this.selectedCutter = selectedCutter;
	}
	
	public void setDPI(int DPI) {
		this.DPI = DPI;
	}
	
	public int getDPI() {
		return this.DPI;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public double[] possibleDPISelectedCutter() {
		return selectedCutter.returnDPI();
	}
	
	public String checkConstraints() {
		
		String result;
		
		if(this.selectedCutter.returnDevice().equals("no selected")) {
			  	result = "No lasercutter has been selected";
		  	} else if (this.address.equals("")) { // TODO more torough check
		  		result = "The address of the lasercutter is not specified";
		  	} else if(this.DPI == 0) {
		  		result = "No DPI setting has been selected";
		  	} else {
		  		result = "passed";
		  	}
		
		return result;
	}

}
