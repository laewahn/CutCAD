import java.util.*;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.*;

import toxi.geom.*;

public class PrintDialog
{ 
  private ArrayList<Shape> shapes;
  private ArrayList<PrintInstance> printInstances;
  
  private PrintDialogFrame printDialogWindow;
  private Frame f;
  
  public PrintDialog(ArrayList<Shape> shapes)
  {
    // Sadly needs to check for all types of shapes as contstuctor is not global, wenn no copy "call of reference" is used
    this.shapes = new ArrayList<Shape>();
    for(int i = 0; i < shapes.size(); i++)
    {
       this.shapes.add(shapes.get(i).copy());
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
    printInstances.add(new PrintInstance(this.shapes)); // replace by instance per material algorithm
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
