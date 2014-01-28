package de.mcp.customizer.printdialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.model.Shape;
import de.mcp.customizer.printdialog.lasercutter.LaserCutter;
import de.mcp.customizer.printdialog.lasercutter.LaserJobCreator;
import processing.data.XML;
import toxi.geom.Vec2D;

public class PrintSubInstance
{
  private ArrayList<Shape> shapesPlaced;
  private LaserJobCreator laserJob;
  
  private PrintSubDialog printSubDialog;
  private Frame f;
  
  private PrintInstance parent;
  
  public PrintSubInstance(PrintInstance parent)
  {
    this.parent = parent;
    shapesPlaced = new ArrayList<Shape>();
    laserJob = new LaserJobCreator();
    //laserJob.setLaserCutter(new LaserCutter("epilogZing"),"127.1.1.1");
    laserJob.newVectorPart(500, this.parent.getMaterial().getPower(), this.parent.getMaterial().getSpeed(), this.parent.getMaterial().getFocus(), this.parent.getMaterial().getFrequency());
  }
  
  public void setLaserCutter(LaserCutter cutter, String ipAddress)
  {
	  laserJob.setLaserCutter(cutter, ipAddress);
  }
  
  public void placeShape(Shape shape)
  {
    shapesPlaced.add(shape);
  }
  
  public void unplaceShape(Shape shape)
  {
    shapesPlaced.remove(shape); 
  }
  
  public ArrayList<Shape> getPlacedShapes()
  {
    return shapesPlaced;
  } 
  
  public void print(String printJobName)
  {
    printSubDialog = createPrintSubDialog(printJobName);
  }
  
  public void sendLaserJob()
  {
	  final float scaleFactor = 19.6850393700787f;
	  for(int i = 0; i < shapesPlaced.size(); i++)
	  {
		  List<Vec2D> vertices = shapesPlaced.get(i).getGShape().getVertices();
		  List<Vec2D> newVertices = new ArrayList<Vec2D>();
		  for(int j = 0; j < vertices.size(); j++)
		  {
			  newVertices.add(new Vec2D(vertices.get(j)));
		  }
		  Vec2D position = shapesPlaced.get(i).getGShape().getPosition2D();
		  for(int j = 0; j < newVertices.size(); j++)
		  {
			  newVertices.get(j).set((newVertices.get(j).getComponent(0)+position.getComponent(0))*scaleFactor,(newVertices.get(j).getComponent(1)+position.getComponent(1))*scaleFactor);
			  System.out.println(newVertices.get(j).getComponent(0) + " " + newVertices.get(j).getComponent(1));
		  }
		  laserJob.addVerticesToVectorPart(newVertices);
	  }
	  laserJob.sendLaserjob(this.parent.getMaterial().getMaterialName());
  }
  
  public void nextJob()
  {
	  printSubDialog.destroy();
	  f.setVisible(false);
	  this.parent.printNext();
  }
  
  private PrintSubDialog createPrintSubDialog(String printJobName)
  {
    f = new Frame(printJobName);
    PrintSubDialog p = new PrintSubDialog(this, 200,200);
    f.add(p);
    p.init();
    p.setSize(300,300);
    f.setTitle(printJobName);
    f.setSize(p.getWidth(), p.getHeight());
    f.setLocation(100, 100);
    f.setResizable(false);
    f.setVisible(true);
    f.addWindowListener(new WindowAdapter() 
    {
      public void windowClosing(WindowEvent evt) 
      {
        printSubDialog.destroy();
        f.setVisible(false);
      }
    });
    return p;
  }
  
  public void printSVG(String printJobName)
  {
	  final double widthInPx = 600;
	  final double heightInPx = 300;
	  String output = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + widthInPx + "mm\" height=\"" + heightInPx + "mm\">";
	  for(int i = 0; i < shapesPlaced.size(); i++)
	  {
		  List<Vec2D> vertices = shapesPlaced.get(i).getGShape().getVertices();
		  Vec2D position = shapesPlaced.get(i).getGShape().getPosition2D();
		  for(int j = 0; j < vertices.size(); j++)
		  {
			  if(j == (vertices.size()-1))
			  {
				  System.out.println(position.getComponent(0) + " " + position.getComponent(1));
				  output += "<line x1=\"" + (vertices.get(j).getComponent(0)+position.getComponent(0)) + "mm\" y1=\"" + (vertices.get(j).getComponent(1)+position.getComponent(1)) + "mm\" x2= \"" + (vertices.get(0).getComponent(0)+position.getComponent(0)) + "mm\" y2= \"" + (vertices.get(0).getComponent(1)+position.getComponent(1)) + "mm\" style=\"stroke:rgb(0,0,0);stroke-width:1mm\" />"; 
			  } else
			  {
				  output += "<line x1=\"" + (vertices.get(j).getComponent(0)+position.getComponent(0)) + "mm\" y1=\"" + (vertices.get(j).getComponent(1)+position.getComponent(1)) + "mm\" x2= \"" + (vertices.get(j+1).getComponent(0)+position.getComponent(0)) + "mm\" y2= \"" + (vertices.get(j+1).getComponent(1)+position.getComponent(1)) + "mm\" style=\"stroke:rgb(0,0,0);stroke-width:1mm\" />";
			  }
		  }
	  }
	  output += "</svg>";
	  try
	  {
		  XML xml = XML.parse(output);
		  xml.save(new File("C:/" + printJobName + ".svg"),"");
	  } catch (Exception e)
	  {
		  System.out.println(e);
	  }
  }
  
  public boolean checkOverlap()
  {
	  for(int i = 0; i < shapesPlaced.size(); i++)
	  {
		  for(int j = i+1; j < shapesPlaced.size(); j++)
		  {
			  if(shapesPlaced.get(i).getGShape().overlapsWith(shapesPlaced.get(j).getGShape()))
			  {
				  return true;
			  }
		  }
	  }
	  return false;
  }
}
