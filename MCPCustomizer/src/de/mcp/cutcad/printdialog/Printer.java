package de.mcp.cutcad.printdialog;

import java.io.File;
import java.util.ArrayList;

import de.mcp.cutcad.printdialog.lasercutter.LaserCutterSettings;
import de.mcp.cutcad.printdialog.selectpath.SelectPathDialogInstance;

class Printer {

	ArrayList<PrintInstance> toBePrintedInstances;
	
	LaserCutterSettings laserCutterSettings;
	
	PrintDialogInstance parent;
	
	private int printer;
	private int printCounter;
	private int confirmLevel;
	
	Printer(ArrayList<PrintInstance> toBePrintedInstances, LaserCutterSettings laserCutterSettings, PrintDialogInstance parent) {
		this.toBePrintedInstances = toBePrintedInstances;
		this.parent = parent;
		this.laserCutterSettings = laserCutterSettings;
		this.printCounter = 0;
	}
	
	private void setLaserCutter() {
			
		for(int i = 0; i < toBePrintedInstances.size(); i++) {
				
			toBePrintedInstances.get(i).setLaserCutter(this.laserCutterSettings.getSelectedCutter(),
						  							   this.laserCutterSettings.getAddress());
		}
	}
	
	private void setDPI() {
			
		for(int i = 0; i < toBePrintedInstances.size(); i++) {
				
			toBePrintedInstances.get(i).setDPI(this.laserCutterSettings.getDPI());
		}
	}
	
	String print() {
		
		printer = 0;
		PrintConstraintChecker checker = new PrintConstraintChecker(this.toBePrintedInstances, this);
		String result = checker.checkPrintConstraints();
		
		if(result.equals("passed")) {
			return startPrint();
		}
		 
		return result;
	}
	
	void printNext() {
		
		printCounter++;
		if(printCounter < this.toBePrintedInstances.size()) {
			this.toBePrintedInstances.get(printCounter).setPrinter(this);
			this.toBePrintedInstances.get(printCounter).print();
		} else {
			parent.printComplete();
		}
	}
	
	String printSVG() {
		
		printer = 1;
		PrintConstraintChecker checker = new PrintConstraintChecker(this.toBePrintedInstances, this);
		String result = checker.checkPrintConstraints();
		
		if(result.equals("passed")) {
			return startPrint();
		}
		
		return result;
	} 
	
	void confirmationPrint(int confirmLevel) {
		this.confirmLevel = confirmLevel;
		this.parent.confirmationPrint();	
	}
	
	String continuePrint() {
		
		if(confirmLevel == 0) {
			
			PrintConstraintChecker checker = new PrintConstraintChecker(this.toBePrintedInstances, this);
			String result = checker.checkUnplacedShapes();
			
			if(result.equals("passed")) {
				return startPrint();
			} else {
				return result;
			}
			
    	} else if(confirmLevel == 1)
    	{
    		return startPrint();
    	}
		return "passed";
	}

	
	private String startPrint() {
		
		if(printer == 0) {
			
			String result =  this.laserCutterSettings.checkConstraints();
			
			if(result.equals("passed")) {
				setLaserCutter();
			  	setDPI();
			  	this.printCounter = 0;
			  	this.toBePrintedInstances.get(printCounter).setPrinter(this);
			  	this.toBePrintedInstances.get(printCounter).print();
			} else {
				return result;
			}
		} else if(printer == 1) {
			SelectPathDialogInstance selectPathInstance = new SelectPathDialogInstance();
		  	selectPathInstance.showSelectPathDialog();
		  	exportSVG(selectPathInstance.getSelectedPath());
		}
		
		return "passed";
	}
	 
	private void exportSVG(File thePath) {
		 
		for(int i = 0; i < toBePrintedInstances.size(); i++) {
			toBePrintedInstances.get(i).printSVG(thePath);
		}
	}
}
