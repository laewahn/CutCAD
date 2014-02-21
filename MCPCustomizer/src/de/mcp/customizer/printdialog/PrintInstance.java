package de.mcp.customizer.printdialog;

import java.io.File;
import java.util.ArrayList;

import de.mcp.customizer.model.Material;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.printdialog.lasercutter.LaserCutter;

/**
 * Represents all the shapes of a certain material.
 * A material is defined by its type and thickness. A printInstances has at least one subInstance,
 * which represent a plate to be cut by a laser cutter. Main responsibilities of this class are:
 * 1) Managing the subInstances(plates) of the material the printInstance represents.
 * 2) Managing the unplaced shapes which have the material the printInstance represents.
 * 3) Setting the laser cutter (laser cutter and its address) of all its subInstances.
 * 4) Placing and unplacing a shape in a subInstance (plate).
 * 5) Initiate SVG export of all its subInstances (plates).
 * 6) Initiate the laser cutting of all its subInstances (plates)
 * 
 * @author Pierre
 *
 */
class PrintInstance
{
	/**
	 * The list of unplaced shapes of the material represented by the printInstance
	 */
	private ArrayList<Shape> shapes;
	
	/**
	 * The subInstances of the printInstance.
	 * A subInstance represent a plate to be laser cut of the material the printInstance represents.
	 */
	private ArrayList<PrintSubInstance> subInstances;
	
	/**
	 * The material the printInstance represents.
	 * Entails its type and thickness, as well as the laser cutter parameters to be used
	 * for this material.
	 */
	private Material material;
	
	/**
	 * The index of the active subInstance.
	 * When a shape is placed it is added to the subInstance with this index.
	 * The printDialogWindow shows the placed shapes of subInstance with this index.
	 */
	private int selected;
	
	/**
	 * Stores the index of the subInstance to be printed.
	 */
	private int instancesPrinted;
  
	//TODO will change after refactoring
	private Printer printer;
  
	/**
	 * Constructs a new printInstance. 
	 * A printInstance represents a material of a certain type and thickness.
	 * It is initialised with one empty subInstance, which is the initial active subInstance.
	 * Furthermore the material it represents is set and the shapes it manages as well.
	 * 	 
	 * @param shape
	 * The shapes belonging to the printInstance.
	 * The shapes have the material and thickness the printInstance represents.
	 * 
	 * @param material
	 * The material the printInstance represents.
	 * The type of the material and the thickness are entailed in this parameter.
	 */
	PrintInstance(Shape shape, Material material) {
		
	    this.shapes = new ArrayList<Shape>();
	    this.shapes.add(shape);
	    this.material = material;
	    
	    subInstances = new ArrayList<PrintSubInstance>();
	    PrintSubInstance initial = new PrintSubInstance(this);
	    
	    subInstances.add(initial);
	    selected = getSubInstanceIndex(initial);
	}
  
	/**
	 * Adds a shape to the list of unplaced shapes of the printInstance
	 * 
	 * @param shape
	 * The shape to be added as unplaced shape 
	 */
	void addShape(Shape shape) {
		this.shapes.add(shape);
	}
  
	/**
	 * Returns a list of shapes that not have been placed within the printInstance.
	 * 
	 * @return
	 * The list of unplaced shapes
	 */
	ArrayList<Shape> getUnplacedShapes() {
		return shapes; 
	}
  
	/**
	 * Returns a list of shapes that have been placed in the active subInstance.
	 * A placed shape is a shape that is placed on a plate and will be cut when the job
	 * belonging to the subInstance is send to the laser cutter.
	 * 
	 * @return
	 * The list of placed shapes of the active subInstance.
	 */
	ArrayList<Shape> getPlacedShapes() {
		return subInstances.get(selected).getPlacedShapes();
	}
  
	/**
	 * Returns the material the printInstance represents.
	 * It containts information about the type of material, thickness and laser settings
	 * which should be used for cutting this material.
	 * 
	 * @return
	 * The material the printInstance represents.
	 */
	Material getMaterial() {
		return this.material; 
	}
  
	/**
	 * Places a shape on the active subInstance.
	 * The shape to be placed is removed from the unplaced shapes of the printInstance
	 * and added to the placed shapes of the active subInstance. It is then drawn of the
	 * plate which is visible in the printDialogWindow.
	 * 
	 * @param shape
	 * Shape to be placed
	 */
	void placeShape(Shape shape) {
		shapes.remove(shape);
		subInstances.get(selected).placeShape(shape);
	}
 
	/**
	 * Unplaces a shape from the active subInstance.
	 * The shape to be removed is removed from the placed shapes of the active subInstance
	 * and added to the unplaced shapes of the printInstance. The shapes which was unplaced will '
	 * not be drawn anymore on the plate visible in printDialogWindow.
	 * 
	 * @param shape
	 * The shape to be unplaced.
	 */
	void unplaceShape(Shape shape) {
		subInstances.get(selected).unplaceShape(shape);
		shapes.add(shape);
	}
  
	/**
	 * Sets which subInstance is active by its index.
	 * When a shape is placed, it is added to the active subInstance.
	 * The placed Shapes of the active subInstance is shown in the printDialogWindow.
	 * 
	 * @param selected
	 * The subInstance to be active
	 */
	void selectSubInstance(PrintSubInstance selected) {
		this.selected = getSubInstanceIndex(selected);
	}
  
	/**
	 * Returns the index of the subInstance belonging to the printInstance.
	 * When the subInstance is not found within the printInstance, -1 is returned.
	 * Else the index of the printInstance is returned.
	 * 
	 * @param selected
	 * The subInstance from which the index is to be returned.
	 * The subInstance should belong to the printInstance.
	 * 
	 * @return
	 * The index of the subInstance.
	 * This is the index of the subInstance, when the subInstance belongs to the printInstance.
	 * Else -1 is returned.
	 */
	private int getSubInstanceIndex(PrintSubInstance selected) {
		
		for(int i = 0; i < subInstances.size(); i++) {
			
			if(subInstances.get(i) == selected) {
				return i; 
			}
		}
		
		return -1;
	}
  
	/**
	 * Initiates the laser cutting process.
	 * This method calls the print method of the first (subInstance with index 0) subInstance 
	 * of the printInstance. 
	 */
	void print() {
		
		instancesPrinted = 0;
		
		if(subInstances.get(instancesPrinted).getPlacedShapes().size() > 0 
				&& !this.material.getMaterialName().equals("Nothing 0,5 mm")) {
			
		  subInstances.get(0).print(this.material.getMaterialName() + " - " + "1");
		  
		} else {
		  printNext();
		}
	}
  
	//TODO changes after refactoring
	/**
	 * Initiates the cutting of the next subInstance. 
	 * This method is called by a subInstance of the printInstance and should not be called by
	 * any other class. When this method is called, the subInstance signals that it is done
	 * with cutting. The method will signal the next subInstance to start the cutting process.
	 * When all subInstances are done with cutting, this method signals the (controller?) that the
	 * printInstance is done with the cutting process.
	 */
	void printNext() {
		instancesPrinted++;
		
		if(instancesPrinted < subInstances.size()) {
			
			if(subInstances.get(instancesPrinted).getPlacedShapes().size() > 0 
					&& !this.material.getMaterialName().equals("Nothing 0,5 mm")) {
				
				int name = instancesPrinted+1;
				subInstances.get(instancesPrinted).print(this.material.getMaterialName() + " - " + name);
				
			} else {
			  printNext();
			}
		} else {
		  this.printer.printNext(); 
		}
	}
  
	/**
	 * Prints all the subInstances (plates) as an SVG.
	 * Drawn will be the placed shapes of an subInstance.
	 * The SVGs will be exported to the path specified in the parameter.
	 * The name of the SVG files are the material type and thickness and the number of the subInstance.
	 * This method calls the printSVG method of all its subInstances.
	 * 
	 * @param thePath
	 * The path to where the SVG files should be stored.
	 */
	void printSVG(File thePath) {
		
		for(int i = 0; i < subInstances.size(); i++) {
			
			if(subInstances.get(i).getPlacedShapes().size() > 0 
					&& !this.material.getMaterialName().equals("Nothing 0,5 mm")) {
				
				int name = i+1;
				String exportPath = thePath.toString() + this.material.getMaterialName() + " - " + name;
				subInstances.get(i).printSVG(exportPath);
			}
		} 
	}
  
	//TODO will be changed after refactoring
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}
	//void setParent(PrintDialogInstance parent) {
		//this.printer = parent; 
	//}
  
	/**
	 * Returns the number of subInstances the printInstance possesses.
	 * 
	 * @return
	 * Number of subInstances in possession
	 */
	int getNumberOfSubInstances() {
		return subInstances.size();
	}
  
	/**
  	* Add a new subInstance to this printInstance.
  	* A subInstance represents a plate to be lasercut.
  	* The active subInstance is set to the new subInstance.
  	*/
	void addSubInstance() {
		PrintSubInstance newInstance =  new PrintSubInstance(this);
		subInstances.add(newInstance);
		selected = getSubInstanceIndex(newInstance);
	}
  
	/**
	 * Sets which subInstance is active by its index.
	 * When a shape is placed, it is added to the active subInstance.
	 * The placed Shapes of the active subInstance is shown in the printDialogWindow.
	 * 
	 * @param index
	 * The index of the to be active subInstance
	 */
	void setActiveSubInstance(int index) {
		this.selected = index; 
	}
  
	/**
	 * Returns the index of the subInstance which is active.
	 * The methods to place and unplace shapes have effect on the subInstance with this index.
	 * The placed shapes of the subInstance with this index is shown in the PrintDialogWindow.
	 * 
	 * @return
	 * the index of the active subInstance
	 */
	int getSelectedSubInstance() {
		return this.selected;
	}
  
	/**
	 * This method checks whether there is a subInstances with placed shapes.
	 * Returns true when there is at least one subInstance with a least one placed shape.
	 * Returns false otherwise.
	 * 
	 * @return
	 * true when there is a subIstance with a placed shape.
	 * false otherwise
	 */
	boolean checkPlacedShapes() {
		
		boolean shapePlaced = false;
	  
		for(int i = 0; i < subInstances.size(); i++) {
		  
			if(subInstances.get(i).getPlacedShapes().size() > 0) {
				shapePlaced = true;
			}
		}
		return shapePlaced;
	}
  
	/**
	 * This method checks whether placed shapes in its subInstances overlap.
	 * When there are overlapping shapes, the name of the subInstance where the overlap occured
	 * is returned. When there was no overlap, "no overlap" is returned.
	 * 
	 * @return
	 * A string containing the name of subclasses where overlap(s) occurred.
	 * When no overlap occurred, then "no overlap" is returned.
	 */
	String checkOverlap() {
		
		boolean overlapped = false;
		String result = "";
		
		for(int i = 0; i < this.subInstances.size(); i++) {
			
			if(this.subInstances.get(i).checkOverlap()) {
			  
				overlapped = true;
				int name = i + 1;
			  
				if(!result.equals("")) {
					result = result + ", overlap in " + this.material.getMaterialName() + name;
				} else {
					result = result + "overlap in " + this.material.getMaterialName() + " - " + name;
				}
			}
		}
		if(overlapped) {
			return result; 
		} else {
			return "no overlap";
		}
	}
  
	/**
	 * Sets which laser setter to be used and its address.
	 * This method passes the laser cutter to be used and its address to all its subInstances.
	 * 
	 * @param cutter
	 * The laser cutter to be used for laser cutting.
	 * 
	 * @param ipAddress
	 * The address of the laser cutter to be used for laser cutting.
	 * Can be an IP address or a port.
	 */
	void setLaserCutter(LaserCutter cutter, String ipAddress) {
		
		for(int i = 0; i < subInstances.size(); i++) {
			subInstances.get(i).setLaserCutter(cutter,ipAddress);
		}
	}
  
	/**
	 * Sets the DPI setting to be used.
	 * This method passes the selected DPI setting to all its subInstances.
	 * 
	 * @param dpi
	 * The DPI setting to be used for laser cutting.
	 */
	void setDPI(int dpi) {
	  
		for(int i = 0; i < subInstances.size(); i++) {
			subInstances.get(i).setDPI(dpi);
		}
	}
}
