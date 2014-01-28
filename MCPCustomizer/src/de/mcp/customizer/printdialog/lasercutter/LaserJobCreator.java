package de.mcp.customizer.printdialog.lasercutter;
import java.util.List;

import toxi.geom.Vec2D;

import com.t_oster.liblasercut.IllegalJobException;
import com.t_oster.liblasercut.LaserJob;
import com.t_oster.liblasercut.PowerSpeedFocusFrequencyProperty;
import com.t_oster.liblasercut.RasterPart;
import com.t_oster.liblasercut.VectorPart;
import com.t_oster.liblasercut.drivers.EpilogZing;
import com.t_oster.liblasercut.drivers.EpilogHelix;
import com.t_oster.liblasercut.drivers.LaosCutter;
import com.t_oster.liblasercut.drivers.Lasersaur;

/**
 * This class handles the preparation of a laser job. The preparation includes setting
 * the settings of the lasercutter accordingly and creating a vector and eventually a raster part.
 * 
 * @author Pierre
 *
 */
public class LaserJobCreator {
	/**
	 * If an Epilog Zing is used, this object is instantiated and used to send to print job
	 */
	private EpilogZing epilogZing = null;
	
	/**
	 * If an Epilog Helix is used, this object is instantiated and used to send to print job
	 */
	private EpilogHelix epilogHelix = null;
	
	/**
	 * If a Laos cutter is used, this object is instantiated and used to send to print job
	 */
	private LaosCutter laosCutter = null;
	
	/**
	 * If a lasersaur is used, this object is instantiated and used to send to print job
	 */
	private Lasersaur lasersaur = null;
	
	/**
	 * Stores the lasercutter to be used for this print job
	 */
	LaserCutter device;
	
	/*/**
	 * Stores the power, speed, focus, frequency property of the laserjob
	 */
	//private PowerSpeedFocusFrequencyProperty psffProperty = null;
	
	/**
	 * Stores the vector part of the laserjob, not yet used.
	 */
	private RasterPart rp = null;
	
	/**
	 * Stores the vector part of the laserjob, this part is calculated within this class
	 */
	private VectorPart vp = null;
	
	/*/**
	 * Stores the DPI settings of the laserjob
	 */
	//private int DPI;
  
	/**
	 * This method sets the lasercutter and its address for the printjob.
	 * This method should be called once before sendLaserJob(String name) is called
	 * 
	 * @param device
	 * A simulation of an enum containing the lasercutter to be used
	 * 
	 * @param ipAddress
	 * This is the address of the lasercutter. This can be an hostname, ipaddress or an port
	 */
	public void setLaserCutter(LaserCutter device, String ipAddress) {
		this.device = device;
		int deviceNumber = device.returnDeviceNumber();
		switch(deviceNumber) {
			case 0: epilogZing = new EpilogZing(ipAddress); 
					break;
			
			case 1: epilogHelix = new EpilogHelix(ipAddress);
					break;
			
			case 2: laosCutter = new LaosCutter();
					laosCutter.setHostname(ipAddress);
					break;
					
			case 3: lasersaur = new Lasersaur();
					break;
		}
	}
  
	/*/** 
	 * This method sets the power, speed, focus and frequency options of the
	 * lasercutter job. They usually depend on the material and thickness of the material.
	 * 
	 * @param power
	 * @param speed
	 * @param focus
	 * @param frequency
	 */
	/*public void setPsffProperty(int power, int speed, float focus, int frequency) {
	    psffProperty = new PowerSpeedFocusFrequencyProperty();
	    psffProperty.setProperty("power", power);
	    psffProperty.setProperty("speed", speed);
	    psffProperty.setProperty("focus", focus);
	    psffProperty.setProperty("frequency", frequency);
	}*/
  
  /*private void setDPI(int DPI) {
    this.DPI = DPI; 
  }*/
  
  public void newVectorPart(int DPI, int power, int speed, float focus, int frequency) {
	  PowerSpeedFocusFrequencyProperty psffProperty = new PowerSpeedFocusFrequencyProperty();
	    psffProperty.setProperty("power", power);
	    psffProperty.setProperty("speed", speed);
	    psffProperty.setProperty("focus", focus);
	    psffProperty.setProperty("frequency", frequency);
    vp = new VectorPart(psffProperty,DPI); 
  }
  
  public void addVerticesToVectorPart(List<Vec2D> newVertices) {
     if(newVertices.get(0) != null) {
        vp.moveto((int)newVertices.get(0).getComponent(0),(int)newVertices.get(0).getComponent(1));
        for(int i = 1; i < newVertices.size(); i++) {
          vp.lineto((int)newVertices.get(i).getComponent(0),(int)newVertices.get(i).getComponent(1));
        }
        vp.lineto((int)newVertices.get(0).getComponent(0),(int)newVertices.get(0).getComponent(1));
     }
  }
  
  public void sendLaserjob(String name) {
    LaserJob job = new LaserJob(name, "", "MCPCustomizer");//title, name, user
    System.out.println(name);
    if (rp != null) {
      job.addPart(rp);
    }
    if (vp != null) {
      job.addPart(vp);
    }
    try {
    	int deviceNumber = device.returnDeviceNumber();
    	switch(deviceNumber) {
    	case 0: System.out.println("printed to right cutter"); 
    			epilogZing.sendJob(job);
    			break;
    			
    	case 1:  epilogHelix.sendJob(job);
    			break;
    			
    	case 2: laosCutter.sendJob(job);
    			break;
    			
    	case 3: lasersaur.sendJob(job);
    			break;
    	}
     
    }
    catch(IllegalJobException ije) {
      System.out.println(ije.toString());
    }
    catch(Exception e) {
      System.out.println(e.toString());
    }
  }
}
