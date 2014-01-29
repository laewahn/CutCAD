package de.mcp.customizer.printdialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.printdialog.lasercutter.LaserCutter;
import de.mcp.customizer.printdialog.lasercutter.LaserJobCreator;
import processing.data.XML;
import toxi.geom.Vec2D;

public class PrintSubInstance {
	
	private ArrayList<Shape> shapesPlaced;
	private LaserJobCreator laserJob;
  
	private PrintSubDialogWindow printSubDialogWindow;
	private Frame printSubDialogFrame;
  
	private PrintInstance parent;
  
	private int dpi;
  
	public PrintSubInstance(PrintInstance parent) {
		this.parent = parent;
		shapesPlaced = new ArrayList<Shape>();
		laserJob = new LaserJobCreator();
	}
  
	public void setLaserCutter(LaserCutter cutter, String ipAddress) {
		laserJob.setLaserCutter(cutter, ipAddress);
	}
  
	public void placeShape(Shape shape) {
		shapesPlaced.add(shape);
	}
  
	public void unplaceShape(Shape shape) {
		shapesPlaced.remove(shape); 
	}
  
	public ArrayList<Shape> getPlacedShapes() {
		return shapesPlaced;
	} 
  
	public void print(String printJobName) {
		printSubDialogWindow = createPrintSubDialog(printJobName);
	}
  
	public void setDPI(int dpi) {
		this.dpi = dpi;
	}
  
	public void sendLaserJob() {
		
		dpi = 500;
		laserJob.newVectorPart(dpi, this.parent.getMaterial().getPower(), this.parent.getMaterial().getSpeed(), this.parent.getMaterial().getFocus(), this.parent.getMaterial().getFrequency());
		laserJob.newEngraveVectorPart(dpi, this.parent.getMaterial().getPower(), this.parent.getMaterial().getSpeed(), this.parent.getMaterial().getFocus(), this.parent.getMaterial().getFrequency());
		final float scaleFactor = (float)dpi/25.4f;
		for(int i = 0; i < shapesPlaced.size(); i++) {
			
			List<Vec2D> vertices = shapesPlaced.get(i).getGShape().getVertices();
			List<Vec2D> newVertices = new ArrayList<Vec2D>();
			
			for(int j = 0; j < vertices.size(); j++) {
				newVertices.add(new Vec2D(vertices.get(j)));
			}
			
			Vec2D position = shapesPlaced.get(i).getGShape().getPosition2D();
			
			for(int j = 0; j < newVertices.size(); j++) {
				newVertices.get(j).set((newVertices.get(j).getComponent(0)+position.getComponent(0))*scaleFactor,(newVertices.get(j).getComponent(1)+position.getComponent(1))*scaleFactor);
			}
			
			for(Cutout c : shapesPlaced.get(i).getGShape().getCutouts()) {
				
				newVertices = c.getVectors();
				
				for(int j = 0; j < newVertices.size(); j++) {
					
					newVertices.get(j).set((newVertices.get(j).add(position)).scale(scaleFactor));
				}
				laserJob.addVerticesToVectorPart(newVertices);
			}
			
			laserJob.addVerticesToVectorPart(newVertices);
		}
		
		laserJob.sendLaserjob(this.parent.getMaterial().getMaterialName());
	}
  
	public void nextJob() {
		printSubDialogWindow.destroy();
		printSubDialogFrame.setVisible(false);
		this.parent.printNext();
	}
  
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
  
	public void printSVG(String printJobName) {
	  
		final double widthInPx = 600;
		final double heightInPx = 300;
		String output = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + widthInPx + "mm\" height=\"" + heightInPx + "mm\">";
	  
		for(int i = 0; i < shapesPlaced.size(); i++) {
		  
			List<Vec2D> vertices = shapesPlaced.get(i).getGShape().getVertices();
			Vec2D position = shapesPlaced.get(i).getGShape().getPosition2D();
		  
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
			
			//Genereate engrave part here later
		}
	  
		output += "</svg>";
	  
		try {
			XML xml = XML.parse(output);
			xml.save(new File("C:/" + printJobName + ".svg"),""); // Use file dialog?
		} catch (Exception e) {
			System.out.println(e);
		}
	}
  
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
