package de.mcp.customizer.printdialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import de.mcp.customizer.model.Shape;
import toxi.geom.Vec2D;

public class PrintDialog
{ 
  private ArrayList<Shape> shapes;
  private ArrayList<PrintInstance> printInstances;
  
  private PrintDialogFrame printDialogWindow;
  private Frame f;
  
  public PrintDialog(ArrayList<Shape> shapes)
  {
    this.shapes = new ArrayList<Shape>();
    for(int i = 0; i < shapes.size(); i++)
    {
       Shape copy = shapes.get(i).copy();
       copy.getShape().setPosition2D(new Vec2D(10,10));
       this.shapes.add(copy);
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
    //printInstances.add(new PrintInstance(this.shapes)); // replace by instance per material algorithm
    for(int i = 0; i < this.shapes.size(); i++)
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
    /*if(this.shapes.size() > 2)
    {
      ArrayList<Shape> temp = new ArrayList<Shape>();
      temp.add(this.shapes.get(0));
      temp.add(this.shapes.get(1));
      printInstances.add(new PrintInstance(temp)); // replace by instance per material algorithm
      ArrayList<Shape> temp2 = new ArrayList<Shape>();
      for(int i = 2; i < this.shapes.size(); i++)
      {
        temp2.add(this.shapes.get(i));
      }
      printInstances.add(new PrintInstance(temp2)); // replace by instance per material algorithm
    } else
    {
      printInstances.add(new PrintInstance(this.shapes)); // replace by instance per material algorithm
    }*/
    
  }
  
  private PrintDialogFrame addPrintDialogFrame(int theWidth, int theHeight, ArrayList<PrintInstance> printInstances)
  {
    f = new Frame("Print dialog");
    PrintDialogFrame p = new PrintDialogFrame(this, theWidth, theHeight, printInstances);
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
