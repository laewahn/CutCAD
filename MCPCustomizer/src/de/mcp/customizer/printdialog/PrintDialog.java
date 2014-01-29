package de.mcp.customizer.printdialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.model.Shape;
import toxi.geom.Vec2D;

/**
 * Prepares the print dialog. Preparation consists of making a new window and processing applet.
 * Furthermore, the shapes should be copied to be free of side effects while working with them
 * in the print dialog. Another step needed for preparation is the calculation of the print instances
 */
public class PrintDialog {
	
	/**
	 * Contains the shapes that have to be copied and altered to be compatible with the print dialog
	 */
	private List<Shape> shapes; 
	/**
	 * Contains the shapes that are suitable to be used in the print dialog. 
	 */
	private ArrayList<Shape> preparedShapes; 
	/**
 	 * Contains the print instances to be used in the print dialog
 	 */
	private ArrayList<PrintInstance> printInstances;  
  
	/**
	 * Contains the object that manages the print dialog window
	 */
	private PrintDialogWindow printDialogWindow; 
	/**
	 * Contains the frame of the print dialog window
	 */
	private Frame printDialogFrame; 
  
	/** 
	 * Creates an object that initialises a print dialog and calculates the objects
	 * which are necessary for the print dialog
	 * 
	 * @param shapes
	 * Contains all the shapes which should be printed
	 */
	public PrintDialog(List<Shape> shapes) {
		this.shapes = shapes;
	}
  
	/** 
	 * Prepares the print dialog. It copies the shapes such that no side effects
	 * can occur with the original shapes. Some properties of the shapes are altered
	 * to be able to use them in the print dialog. The window of the print dialog is
	 * created here. 
	 */
	public void preparePrintDialog() {
		copyShapes();
		calculateInstances();
		this.printDialogWindow = addPrintDialogFrame(1100, 650, this.printInstances);
		
		for(int i = 0; i < this.printInstances.size(); i++) {
			this.printInstances.get(i).setParent(this.printDialogWindow);
		}
	}
  
	/** 
	 * This method copies the shapes stored in shapes and stores the copies in preparedShapes.
	 * It furthermore alters the position to the upper left corner and sets the scalingfactor 
	 * to be compatible with the print dialog. Lastly, the shape is scaled such that it size 
	 * is correctly displayed in the print dialog. 
	 */
	private void copyShapes() {
		this.preparedShapes = new ArrayList<Shape>();
		
		for(int i = 0; i < this.shapes.size(); i++) {
	       Shape copy = this.shapes.get(i).copy();
	       copy.getGShape().setPosition2D(new Vec2D(100,100));
	       copy.getGShape().scale2D(0.1f);
	       copy.getGShape().setScalingFactor(1);
	       this.preparedShapes.add(copy);
		}
	}
  
	/** 
	 * Determines the print instances. They depend on the materials used by the shapes.
	 * Every material type and thickness has its own print instance. The calculated instances
	 * are stored in printInstances.
	 */
	private void calculateInstances() {
		
		printInstances = new ArrayList<PrintInstance>();
		
		for(int i = 0; i < this.preparedShapes.size(); i++) {
			
			if(!this.preparedShapes.get(i).getGShape().getMaterial().getMaterialName().equals("Nothing 0,5 mm")) {
				int j = 0;
				boolean found = false;
				
				while((j < printInstances.size()) && !found) {
					
					if(this.printInstances.get(j).getMaterial().getMaterialName().equals(this.preparedShapes.get(i).getGShape().getMaterial().getMaterialName())) {
						this.printInstances.get(j).addShape(this.preparedShapes.get(i));
						found = true;
					} 
					j++;
				}
				
				if(!found) {
					this.printInstances.add(new PrintInstance(this.preparedShapes.get(i),this.preparedShapes.get(i).getGShape().getMaterial()));
				}
			}
		}
	}
  
	/** 
 	* Creates a new frame and registers a new PrintDialogFrame with it.
 	* The PrintDialogFrame is a processing applet that handles the print dialog UI
 	* @param theWidth
 	* Sets the width of the print dialog window in Px
 	* 
 	* @param theHeight
 	* Sets the height of the print dialog window in Px
 	* 
 	* @param printInstances
 	* The printInstances to be used for the print dialog
 	*/
	private PrintDialogWindow addPrintDialogFrame(int theWidth, int theHeight, ArrayList<PrintInstance> printInstances) {
		
		this.printDialogFrame = new Frame("Print dialog");
		PrintDialogWindow tempPrintDialogWindow = new PrintDialogWindow(theWidth, theHeight, printInstances);
		this.printDialogFrame.add(tempPrintDialogWindow);
		
		tempPrintDialogWindow.init();
		tempPrintDialogWindow.setSize(theHeight,theWidth);
		
		this.printDialogFrame.setTitle("Print dialog");
		this.printDialogFrame.setSize(tempPrintDialogWindow.getWidth(), tempPrintDialogWindow.getHeight());
		this.printDialogFrame.setLocation(100, 100);
		this.printDialogFrame.setResizable(false);
		this.printDialogFrame.setVisible(true);
		
		this.printDialogFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				printDialogWindow.destroy();
				printDialogFrame.setVisible(false);
			}
		});
		
		return tempPrintDialogWindow;
	}
  
}
