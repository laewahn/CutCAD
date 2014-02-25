package de.mcp.cutcad.printdialog;

import de.mcp.cutcad.printdialog.lasercutter.LaserCutterSettings;
import processing.core.PApplet;
import processing.data.XML;

class SettingsLoader extends PApplet {

	XML printSettings;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4340621454472534519L;
	
	void loadSettingFile () {
		printSettings = loadXML("printsettings.xml");
	}
	
	boolean settingsExists () {
		
		if(printSettings != null) {
			System.out.println("settings existent");
			return true;
		} else {
			System.out.println("no settings existent");
			return false;
		}
	}
	
	void persistSettings (LaserCutterSettings settingsToSafe) {
		
		if(!settingsToSafe.getSelectedCutter().returnDevice().equals("no selected")) {
			System.out.println("start persisting");
			String output = "<printsettings><lasercutter>";
			output += settingsToSafe.getSelectedCutter().returnDevice();
			output += "</lasercutter>";
			output += "<address>";
			output += settingsToSafe.getAddress();
			output += "</address>";
			output += "<dpi>";
			output += settingsToSafe.getDPI();
			output += "</dpi>";
			output += "</printsettings>";
			printSettings = parseXML(output);
			System.out.println(output);
			saveXML(printSettings, "printsettings.xml");
		} else {
			System.out.println("no settings to safe");
		}
	}

	LaserCutterSettings loadSettings() {
		LaserCutterSettings laserCutterSettings = new LaserCutterSettings();
		    laserCutterSettings.getSelectedCutter().setDevice(printSettings.getChild("lasercutter").getContent());
		    laserCutterSettings.setAddress(printSettings.getChild("address").getContent());
		    laserCutterSettings.setDPI((int)(printSettings.getChild("dpi").getIntContent()));
		  
		  return laserCutterSettings;
		
	}

}
