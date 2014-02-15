package de.mcp.customizer.printdialog;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.model.primitives.Cutout;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.printdialog.lasercutter.LaserCutter;
import de.mcp.customizer.printdialog.lasercutter.LaserJobCreator;
import processing.data.XML;

/**
 * Represents a single plate of a certain material.
 * Main responsibilities are: 
 * - managing objects which are placed on the plate.
 * - Setup a laser job and adding the objects placed on the plate.
 * - SVG export and execution of the laser job
 * 
 * @author Pierre
 *
 */
public class PrintSubInstance {
	
	/**
	 * Stores the shapes with all of their properties placed on this plate
	 */
	private ArrayList<Shape> shapesPlaced;
	
	/**
	 * The laser job that is used when the plate is about to be cut
	 */
	private LaserJobCreator laserJob;
  
	/**
	 * The confirmation dialog that is called before the laser job is send
	 * to the laser cutter
	 */
	private PrintSubDialogWindow printSubDialogWindow;
	
	/**
	 * The frame containing the confirmation dialog
	 */
	private Frame printSubDialogFrame;
  
	/**
	 * The printInstance where the plate belongs to. The printInstance manages all
	 * unplaced objects of a certain material and thickness, whereas to the printSubInstance
	 * only manages one plate of this material and thickness. The printInstance manages
	 * several function of its printSubInstances. To be able to signal whether the 
	 * printSubInstance is ready, with for example cutting, it needs to know the 
	 * printInstance to who it belongs.
	 */
	private PrintInstance parent;
  
	/**
	 * Stores the DPI used for the raster, cut through and engrave vector parts.
	 */
	private int dpi;
  
	/**
	 * Creates a new object of type printSubInstance.
	 * A printSubInstance represent a plate of a certain material and thickness.
	 * As it belongs to a certain material and thickness it needs to know to which
	 * it belongs. This is done by knowing the printInstance it belongs to. 
	 * The constructor initialises the list with placed shapes as an empty list
	 * and calls the constructor that initialises its print job.
	 * 
	 * @param parent
	 * The printInstance the printSubInstance belongs to.
	 * In the printInstance the unplaced shapes for the material of the plate is stored.
	 * The printInstance manages several function of its printSubInstances.
	 * To be able to signal whether the printSubInstance is ready, with for example cutting,
	 * it needs to know the printInstance to who it belongs.
	 */
	public PrintSubInstance(PrintInstance parent) {
		this.parent = parent;
		shapesPlaced = new ArrayList<Shape>();
		laserJob = new LaserJobCreator();
	}
  
	/**
	 * Sets the laser cutter in the laser job associates with the plate which will be cut.
	 * To connect to a laser cutter it needs the type of the laser cutter and an address
	 * 
	 * @param cutter
	 * An enum emulator that entails the type of the laser cutter  
	 * 
	 * @param ipAddress
	 * The address of the laser cutter as a String. Can be a ip address, host name or port.
	 */
	public void setLaserCutter(LaserCutter cutter, String ipAddress) {
		laserJob.setLaserCutter(cutter, ipAddress);
	}
	
	/**
	 * Signals that the shape contained in the parameter has been placed on the plate.
	 * The method adds the shape contained in the parameter to the placedShapes list
	 * 
	 * @param shape
	 * The shape that has been added to the plate
	 */
	public void placeShape(Shape shape) {
		shapesPlaced.add(shape);
	}
  
	/**
	 * Signals that the shape contained in the parameter has been removed from the plate.
	 * The shape contained in the parameter is removed from the placedShapes list
	 * 
	 * @param shape
	 * The shape that has been removed from the plate
	 */
	public void unplaceShape(Shape shape) {
		shapesPlaced.remove(shape); 
	}
  
	/**
	 * Returns all the shapes that have been placed on the plate
	 * 
	 * @return
	 * A List containing all the shapes that have been placed on the plate
	 */
	public ArrayList<Shape> getPlacedShapes() {
		return shapesPlaced;
	} 
  
	/**
	 * Singals that the plate with its placed shapes should be printed.
	 * Before the laser job is send to the laser cutter a confirmation dialog
	 * is created to instruct the user and a possibility to wait until the laser cutter
	 * is ready with this plate. The confirmation window has the name and thickness of the material
	 * to be cut and the printSubInstance number of the plate.
	 * 
	 * @param printJobName
	 * The name, thickness of the material and the printSubInstance number of the plate
	 */
	public void print(String printJobName) {
		printSubDialogWindow = createPrintSubDialog(printJobName);
	}
  
	/**
	 * Sets the DPI that is needed to create a vector or raster part for a laser job.
	 * The DPI is also needed to calculate how much pixels are needed for 1mm.
	 * mm is the unit used in the print dialog to store the coordinates.
	 * 
	 * @param dpi
	 * The dpi to be used for the cutting
	 */
	public void setDPI(int dpi) {
		this.dpi = dpi;
	}
	
	/**
	 * Signals that the plate is ready to be cut. This method is called by the print
	 * confirmation window and should not be called by any other instance.
	 * This method first creates the cutting and engraving vector parts and the raster part.
	 * All the relevant shapes (the placed shapes) are then added to the job parts and are scaled according to the dpi setting
	 * stored in the dpi variable of this class. When the parts are created the job is
	 * send to the laser cutter specified in the laser job belonging to the plate to be cut.
	 */
	public void sendLaserJob() {
		
		laserJob.newVectorPart(dpi, this.parent.getMaterial().getPower(), this.parent.getMaterial().getSpeed(), this.parent.getMaterial().getFocus(), this.parent.getMaterial().getFrequency());
		laserJob.newEngraveVectorPart(dpi, this.parent.getMaterial().getPower(), this.parent.getMaterial().getSpeed(), this.parent.getMaterial().getFocus(), this.parent.getMaterial().getFrequency());
		final float scaleFactor = (float)dpi/25.4f;
		for(int i = 0; i < shapesPlaced.size(); i++) {
			
			List<Vector2D> vertices = shapesPlaced.get(i).getGShape().getVertices();
			List<Vector2D> newVertices = new ArrayList<Vector2D>();
			
			for(int j = 0; j < vertices.size(); j++) {
				newVertices.add(new Vector2D(vertices.get(j)));
			}
			
			Vector2D position = shapesPlaced.get(i).getGShape().getPosition2D();
			
			for(int j = 0; j < newVertices.size(); j++) {
				newVertices.get(j).set((newVertices.get(j).getComponent(0)+position.getComponent(0))*scaleFactor,(newVertices.get(j).getComponent(1)+position.getComponent(1))*scaleFactor);
			}
			
			laserJob.addVerticesToVectorPart(newVertices);
			
			for(Cutout c : shapesPlaced.get(i).getGShape().getCutouts()) {
				
				newVertices = c.getVectors();
				
				for(int j = 0; j < newVertices.size(); j++) {
					
					newVertices.get(j).set((newVertices.get(j).add(position)).scale(scaleFactor));
				}
				laserJob.addVerticesToVectorPart(newVertices);
			}
			
			//TODO engrave part here later	
		}
		
		laserJob.sendLaserjob(this.parent.getMaterial().getMaterialName());
	}
  
	/**
	 * Signals that the cutting process is finished. This method is called by the print
	 * confirmation window and should not be called by any other instance. First, the 
	 * confirmation window used for this printInstance is set invisible.
	 * Then the printInstance (material) to which this printSubInstance (plate) belongs
	 * is signalled that this printSubInstance is done with cutting.
	 */
	public void nextJob() {
		printSubDialogWindow.destroy();
		printSubDialogFrame.setVisible(false);
		this.parent.printNext();
	}
  
	/**
	 * Creates the print confirmation window for this printSubInstance (plate).
	 * From the confirmation window commands can be send to signal to start cutting
	 * and signal that the cutting is finished
	 * 
	 * @param printJobName
	 * The name of the job the print confirmation window belongs to. The name consists
	 * of the name and thickness of the material the plate belongs to and the
	 * printSubInstance number of the plate
	 * 
	 * @return
	 * The print confirmation window created for this printSubInstance (plate)
	 */
	private PrintSubDialogWindow createPrintSubDialog(String printJobName) {
		
		printSubDialogFrame = new Frame(printJobName);
		PrintSubDialogWindow tempPrintSubDialog = new PrintSubDialogWindow(this, 200,200);
		printSubDialogFrame.add(tempPrintSubDialog);
		
		tempPrintSubDialog.init();
		tempPrintSubDialog.setSize(300,300);
		
		printSubDialogFrame.setTitle(printJobName);
		printSubDialogFrame.setSize(tempPrintSubDialog.getWidth(), tempPrintSubDialog.getHeight());
		printSubDialogFrame.setLocation(100, 100);
		printSubDialogFrame.setResizable(false);
		printSubDialogFrame.setVisible(true);
		
		printSubDialogFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				printSubDialogWindow.destroy();
				printSubDialogFrame.setVisible(false);
			}
		});
		
		return tempPrintSubDialog;
	}
  
	/**
	 * This method exports the layout of the placed shapes on the plate as SVG.
	 * The name of the SVG file is the name transmitted by the parameter printJobName
	 * 
	 * @param printJobName
	 * The name of the exported SVG file
	 */
	public void printSVG(String printJobName) {
	  
		final double widthInPx = 600;
		final double heightInPx = 300;
		String output = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + widthInPx + "mm\" height=\"" + heightInPx + "mm\">";
	  
		for(int i = 0; i < shapesPlaced.size(); i++) {
		  
			List<Vector2D> vertices = shapesPlaced.get(i).getGShape().getVertices();
			Vector2D position = shapesPlaced.get(i).getGShape().getPosition2D();
		  
			for(int j = 0; j < vertices.size(); j++) {
			  
				if(j == (vertices.size()-1)) {
					output += "<line x1=\"" + (vertices.get(j).getComponent(0)+position.getComponent(0)) + "mm\" y1=\"" + (vertices.get(j).getComponent(1)+position.getComponent(1)) + "mm\" x2= \"" + (vertices.get(0).getComponent(0)+position.getComponent(0)) + "mm\" y2= \"" + (vertices.get(0).getComponent(1)+position.getComponent(1)) + "mm\" style=\"stroke:rgb(0,0,0);stroke-width:1mm\" />"; 
				} else {
					output += "<line x1=\"" + (vertices.get(j).getComponent(0)+position.getComponent(0)) + "mm\" y1=\"" + (vertices.get(j).getComponent(1)+position.getComponent(1)) + "mm\" x2= \"" + (vertices.get(j+1).getComponent(0)+position.getComponent(0)) + "mm\" y2= \"" + (vertices.get(j+1).getComponent(1)+position.getComponent(1)) + "mm\" style=\"stroke:rgb(0,0,0);stroke-width:1mm\" />";
				}
			}
			
			for(Cutout c : shapesPlaced.get(i).getGShape().getCutouts()) {
				
				vertices = c.getVectors();
				
				for(int j = 0; j < vertices.size(); j++) {
					
					if(j == (vertices.size()-1)) {
						output += "<line x1=\"" + (vertices.get(j).getComponent(0)+position.getComponent(0)) + "mm\" y1=\"" + (vertices.get(j).getComponent(1)+position.getComponent(1)) + "mm\" x2= \"" + (vertices.get(0).getComponent(0)+position.getComponent(0)) + "mm\" y2= \"" + (vertices.get(0).getComponent(1)+position.getComponent(1)) + "mm\" style=\"stroke:rgb(0,0,0);stroke-width:1mm\" />"; 
					} else {
						output += "<line x1=\"" + (vertices.get(j).getComponent(0)+position.getComponent(0)) + "mm\" y1=\"" + (vertices.get(j).getComponent(1)+position.getComponent(1)) + "mm\" x2= \"" + (vertices.get(j+1).getComponent(0)+position.getComponent(0)) + "mm\" y2= \"" + (vertices.get(j+1).getComponent(1)+position.getComponent(1)) + "mm\" style=\"stroke:rgb(0,0,0);stroke-width:1mm\" />";
					}
				}
			}
			
			//TODO Generate engrave part here later
		}
	  
		output += "</svg>";
	  
		try {
			XML xml = XML.parse(output);
			xml.save(new File("C:/" + printJobName + ".svg"),""); // Use file dialog?
		} catch (Exception e) {
			System.out.println(e);
		}
	}
  
	/**
	 * Checks whether shapes placed on the plate do not overlap.
	 * This functions returns true when shapes are overlapping otherwise false
	 * 
	 * @return
	 * true when overlapping, false when not
	 */
	public boolean checkOverlap() {
		
		for(int i = 0; i < shapesPlaced.size(); i++) {
			
			for(int j = i+1; j < shapesPlaced.size(); j++) {
				
				if(shapesPlaced.get(i).getGShape().overlapsWith(shapesPlaced.get(j).getGShape())) {
					return true;
				}
			}
		}
		
		return false;
	}
}
