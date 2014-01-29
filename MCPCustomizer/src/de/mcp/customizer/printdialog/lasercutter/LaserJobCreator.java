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
	
	/**
	 * Stores the vector part of the laserjob, not yet used.
	 */
	private RasterPart rp = null;
	
	/**
	 * Stores the cut through vector part of the laserjob. 
	 * This part is assembled within this class
	 */
	private VectorPart vp = null;
	
	/**
	 * Stores the engrave vector part of the laserjob. 
	 * This part is assembled within this class
	 */
	private VectorPart engravevp = null;
  
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
  
	/**
	 * Creates a new object that entails vector commands which can be send to a laser cutter
	 * to cut through material. The vector parts needs to have the setting for the lasercutter to cut
	 * through the material. Furthermore it needs the accuracy in DPI with which will be cut.
	 * 
	 * @param DPI
	 * the DPI setting with which this vectorpart will be cut
	 * 
	 * @param power
	 * the power needed to cut through the material
	 * 
	 * @param speed
	 * the speed suitable for the material to be cut
	 * 
	 * @param focus
	 * the focus setting suitable for the material to be cut
	 * 
	 * @param frequency
	 * the frequency of the laser suitable to cut through the material
	 */
	public void newVectorPart(int DPI, int power, int speed, float focus, int frequency) {
		PowerSpeedFocusFrequencyProperty psffProperty = new PowerSpeedFocusFrequencyProperty();
	    psffProperty.setProperty("power", power);
	    psffProperty.setProperty("speed", speed);
	    psffProperty.setProperty("focus", focus);
	    psffProperty.setProperty("frequency", frequency);
	    vp = new VectorPart(psffProperty,DPI); 
	}
	

	/**
	 * Creates a new object that entails vector commands which can be send to a laser cutter
	 * to engrave material. The vector parts needs to have the setting for the lasercutter to 
	 * engrace the material. Furthermore it needs the accuracy in DPI with which will be engraved.
	 * 
	 * @param DPI
	 * the DPI setting with which this vectorpart will be engraved
	 * 
	 * @param power
	 * the power suitable for engraving the material
	 * 
	 * @param speed
	 * the speed suitable for the material to be engraved
	 * 
	 * @param focus
	 * the focus setting suitable for the material to be engraved
	 * 
	 * @param frequency
	 * the frequency of the laser suitable to engrave the material
	 */
	public void newEngraveVectorPart(int DPI, int power, int speed, float focus, int frequency) {
		PowerSpeedFocusFrequencyProperty psffProperty = new PowerSpeedFocusFrequencyProperty();
	    psffProperty.setProperty("power", power);
	    psffProperty.setProperty("speed", speed);
	    psffProperty.setProperty("focus", focus);
	    psffProperty.setProperty("frequency", frequency);
	    engravevp = new VectorPart(psffProperty,DPI); 
	}
  
	/**
	 * Adds the vertices to the cut through vector part of the laser job. The vertices
	 * will result in a close shape, as lines between two consecutive vertices and the first
	 * and last vertex will be drawn.
	 * 
	 * @param newVertices
	 * A list of vertices which will be added to the cut through vector part as close shape
	 */
	public void addVerticesToVectorPart(List<Vec2D> newVertices) {
		if(newVertices.get(0) != null) {
			vp.moveto((int)newVertices.get(0).getComponent(0),(int)newVertices.get(0).getComponent(1));
			for(int i = 1; i < newVertices.size(); i++) {
				vp.lineto((int)newVertices.get(i).getComponent(0),(int)newVertices.get(i).getComponent(1));
	        }
        	vp.lineto((int)newVertices.get(0).getComponent(0),(int)newVertices.get(0).getComponent(1));
		}
	}
  
	/**
	 * Sends the laserjob to the lasercutter. To which lasercutter it is send, depends
	 * on which lasercutter has been set. It sends the raserpart, the cut through vector
	 * part and the engrave vector part.
	 * 
	 * @param name
	 * the name of the laser job as displayed on the laser cutter
	 */
	public void sendLaserjob(String name) {
		LaserJob job = new LaserJob(name, "", "MCPCustomizer");
		if (rp != null) {
			job.addPart(rp);
		}
		if (vp != null) {
			job.addPart(vp);
		}
		if(engravevp != null) {
			job.addPart(engravevp);
		}
		try {
			int deviceNumber = device.returnDeviceNumber();
			switch(deviceNumber) {
			case 0: System.out.println("Cutting on Epilog Zing"); 
    				epilogZing.sendJob(job);
    				break;
    			
			case 1: System.out.println("Cutting on Epilog Helix"); 
    				epilogHelix.sendJob(job);
    				break;
    			
			case 2: System.out.println("Cutting on LAOS"); 
    				laosCutter.sendJob(job);
    				break;
    			
			case 3: System.out.println("Cutting on lasersaur"); 
    				lasersaur.sendJob(job);
    				break;
    	}
     
    }
		catch(IllegalJobException ije) {
			System.out.println(ije.toString());
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
}
