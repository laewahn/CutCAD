package de.mcp.cutcad.printdialog;

import java.util.ArrayList;

import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.printdialog.lasercutter.LaserCutterSettings;

class PrintDialogInstance {
	
	private ArrayList<PrintInstance> printInstances;
	
	private LaserCutterSettings laserCutterSettings;
	
	private Printer printer;
	
	private PrintDialogWindow parent;
	
	/**
	 * Stores which of the print instances (material of a certain thickness) is currently 
	 * active. 
	 */
	private int selectedInstance;
	
	PrintDialogInstance(ArrayList<PrintInstance> printInstances) {
		
		this.printInstances = printInstances;
		if(printInstances.size() > 0)
		{
			selectedInstance = 0; 
		}
		laserCutterSettings = new LaserCutterSettings();
	}
	
	LaserCutterSettings getLaserCutterSettings() {
		return this.laserCutterSettings;
	}
	
	void printComplete() {
		this.parent.printComplete();
	}
	
	
	void confirmationPrint() {
		parent.confirmationPrint();
	}
	
	String continuePrint() {
		return printer.continuePrint();
	}
	
	String print(boolean SVG) {
		
		printer = new Printer(printInstances, laserCutterSettings, this);
		
		if(SVG) {
			return printer.printSVG();
		} else {
			return printer.print();
		}
	}
	
	ArrayList<Shape> getUnplacedShapes() {
		return this.printInstances.get(selectedInstance).getUnplacedShapes();
	}
	
	void setSelectedInstance(int selectedInstance) {
		this.selectedInstance = selectedInstance;
	}
	
	int getSelectedInstance() {
		return selectedInstance;
	}
	
	boolean printInstancesNotEmpty() {
		return this.printInstances.size() > 0;
	}
	
	void placeShape(int index) {
		Shape toBePlacedShape = this.printInstances.get(selectedInstance).getUnplacedShapes().get(index);
	    this.printInstances.get(selectedInstance).placeShape(toBePlacedShape);
	}
	
	void addSubInstance() {
		this.printInstances.get(selectedInstance).addSubInstance();
	}
	
	void selectInstanceByButtonID(int objectIndex) {
		boolean instanceFound = false;
	      int instance = 0;
	      int subInstance = 0;
	      while(!instanceFound)
	      {
	        if((subInstance + printInstances.get(instance).getNumberOfSubInstances()) > objectIndex)
	        {
	         instanceFound = true;
	        } else
	        {
	          subInstance += printInstances.get(instance).getNumberOfSubInstances();
	          instance++;
	        }
	      }
	      subInstance = objectIndex-subInstance;
	      this.selectedInstance = instance;
	      printInstances.get(this.selectedInstance).setActiveSubInstance(subInstance);
	}
	
	int activeButtonNumber() {
		 int buttonNumber = 0;
		  for(int i = 0; i < selectedInstance; i++)
		  {
			 buttonNumber += printInstances.get(i).getNumberOfSubInstances();
		  }
		  buttonNumber += printInstances.get(selectedInstance).getSelectedSubInstance();
		  return buttonNumber;
	}

	ArrayList<PrintInstance> getPrintInstances() {
		return this.printInstances;
	}

	ArrayList<Shape> getPlacedShapes() {
		return this.printInstances.get(selectedInstance).getPlacedShapes();
	}
	
	void unplaceShape(Shape s) {
		this.printInstances.get(selectedInstance).unplaceShape(s);
	}

	public void setPrintDialogWindow(PrintDialogWindow parent) {
		this.parent = parent;
	}

}
