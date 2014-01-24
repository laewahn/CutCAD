package de.mcp.customizer.printdialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.model.Shape;
import toxi.geom.Vec2D;

public class PrintDialog
{ 
  private ArrayList<Shape> shapes;
  private ArrayList<PrintInstance> printInstances;
  
  private PrintDialogFrame printDialogWindow;
  private Frame f;
  
  public PrintDialog(List<Shape> shapes)
  {
    this.shapes = new ArrayList<Shape>();
    for(int i = 0; i < shapes.size(); i++)
    {
       Shape copy = shapes.get(i).copy();
       copy.getShape().setPosition2D(new Vec2D(100,100));
       copy.getShape().setScalingFactor(1);
       this.shapes.add(copy);
       this.shapes.get(i).getShape().scale2D(0.1f);
    }
    calculateInstances();
    printDialogWindow = addPrintDialogFrame(600, 650, this.printInstances);
    for(int i = 0; i < printInstances.size(); i++)
    {
     printInstances.get(i).setParent(printDialogWindow);
    }
  }
  
  private void calculateInstances()
  {
    printInstances = new ArrayList<PrintInstance>();
    for(int i = 0; i < this.shapes.size(); i++)
    {
    	if(!this.shapes.get(i).getShape().getMaterial().getMaterialName().equals("Nothing 0,5 mm"))
    	{
    		int j = 0;
    		boolean found = false;
    		while((j < printInstances.size()) && !found)
    		{
    			if(this.printInstances.get(j).getMaterial().getMaterialName().equals(this.shapes.get(i).getShape().getMaterial().getMaterialName()))
    			{
    				this.printInstances.get(j).addShape(this.shapes.get(i));
    				found = true;
    			} 
    			j++;
    		}
    		if(!found)
			{
				this.printInstances.add(new PrintInstance(this.shapes.get(i),this.shapes.get(i).getShape().getMaterial()));
			}
    	}
    }
  }
  
  private PrintDialogFrame addPrintDialogFrame(int theWidth, int theHeight, ArrayList<PrintInstance> printInstances)
  {
    f = new Frame("Print dialog");
    PrintDialogFrame p = new PrintDialogFrame(/*this, */theWidth, theHeight, printInstances);
    f.add(p);
    p.init();
    p.setSize(theWidth,theHeight);
    f.setTitle("Print dialog");
    f.setSize(p.getWidth(), p.getHeight());
    f.setLocation(100, 100);
    f.setResizable(false);
    f.setVisible(true);
    f.addWindowListener(new WindowAdapter() 
    {
      public void windowClosing(WindowEvent evt) 
      {
        // exit the application
        printDialogWindow.destroy();
        f.setVisible(false);
      }
    });
    return p;
  }
  
}
