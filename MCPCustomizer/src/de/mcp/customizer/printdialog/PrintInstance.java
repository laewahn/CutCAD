package de.mcp.customizer.printdialog;
import java.util.ArrayList;

import de.mcp.customizer.model.Material;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.printdialog.lasercutter.LaserCutter;

public class PrintInstance
{
  private ArrayList<Shape> shapes;
  private ArrayList<PrintSubInstance> subInstances;
  private Material material;
  private int selected;
  private int instancesPrinted;
  
  private PrintDialogWindow parent;
  
  public PrintInstance(Shape shape, Material material) 
  {
    this.shapes = new ArrayList<Shape>();
    this.shapes.add(shape);
    this.material = material;
    subInstances = new ArrayList<PrintSubInstance>();
    PrintSubInstance initial = new PrintSubInstance(this);
    subInstances.add(initial);
    selected = getSubInstanceIndex(initial);
  }
  
  public void addShape(Shape shape)
  {
    this.shapes.add(shape);
  }
  
  public ArrayList<Shape> getUnplacedShapes()
  {
   return shapes; 
  }
  
  public ArrayList<Shape> getPlacedShapes()
  {
   return subInstances.get(selected).getPlacedShapes();
  }
  
  public Material getMaterial()
  {
   return this.material; 
  }
  
  public void placeShape(Shape shape)
  {
    shapes.remove(shape);
    subInstances.get(selected).placeShape(shape);
  }
  
  public void unplaceShape(Shape shape)
  {
    subInstances.get(selected).unplaceShape(shape);
    shapes.add(shape);
  }
  
  public void selectSubInstance(PrintSubInstance selected)
  {
    this.selected = getSubInstanceIndex(selected);
  }
  
  private int getSubInstanceIndex(PrintSubInstance selected)
  {
    for(int i = 0; i < subInstances.size(); i++)
    {
      if(subInstances.get(i) == selected)
      {
        return i; 
      }
    }
    return -1;
  }
  
  public void print()
  {
	  instancesPrinted = 0;
	  if(subInstances.get(instancesPrinted).getPlacedShapes().size() > 0 && !this.material.getMaterialName().equals("Nothing 0,5 mm"))
	  {
		  subInstances.get(0).print(this.material.getMaterialName() + " - " + "1");
	  } else
	  {
		  printNext();
	  }
  }
  
  public void printNext()
  {
	  instancesPrinted++;
	  if(instancesPrinted < subInstances.size())
	  {
		  if(subInstances.get(instancesPrinted).getPlacedShapes().size() > 0 && !this.material.getMaterialName().equals("Nothing 0,5 mm"))
		  {
			  int name = instancesPrinted+1;
			  subInstances.get(instancesPrinted).print(this.material.getMaterialName() + " - " + name);
		  } else
		  {
			  printNext();
		  }
	  } else
	  {
		  this.parent.printNext(); 
	  }
  }
  
  public void printSVG()
  {
	  for(int i = 0; i < subInstances.size(); i++)
	  {
		  if(subInstances.get(i).getPlacedShapes().size() > 0 && !this.material.getMaterialName().equals("Nothing 0,5 mm"))
		  {
			  int name = i+1;
			  subInstances.get(i).printSVG(this.material.getMaterialName() + " - " + name);
		  }
	  } 
  }
  
  public void setParent(PrintDialogWindow parent)
  {
   this.parent = parent; 
  }
  
  public int getNumberOfSubInstances()
  {
   return subInstances.size();
  }
  
  public void addSubInstance()
  {
    PrintSubInstance newInstance =  new PrintSubInstance(this);
    subInstances.add(newInstance);
    selected = getSubInstanceIndex(newInstance);
  }
  
  public void setActiveSubInstance(int index)
  {
   this.selected = index; 
  }
  
  public int getSelectedSubInstance()
  {
	  return this.selected;
  }
  
  public boolean checkPlacedShapes()
  {
	  boolean shapePlaced = false;
	  for(int i = 0; i < subInstances.size(); i++)
	  {
		  if(subInstances.get(i).getPlacedShapes().size() > 0)
		  {
			  shapePlaced = true;
		  }
	  }
	  return shapePlaced;
  }
  
  public String checkOverlap()
  {
	  boolean overlapped = false;
	  String result = "";
	  for(int i = 0; i < this.subInstances.size(); i++)
	  {
		  if(this.subInstances.get(i).checkOverlap())
		  {
			  overlapped = true;
			  int name = i + 1;
			  if(!result.equals(""))
			  {
				  result = result + ", overlap in " + this.material.getMaterialName() + name;
			  } else
			  {
				  result = result + "overlap in " + this.material.getMaterialName() + " - " + name;
			  }
		  }
	  }
	  if(overlapped)
	  {
		 return result; 
	  } else
	  {
		  return "no overlap";
	  }
  }
  
  public void setLaserCutter(LaserCutter cutter, String ipAddress)
  {
	  for(int i = 0; i < subInstances.size(); i++)
	  {
		  subInstances.get(i).setLaserCutter(cutter,ipAddress);
	  }
  }
  
  public void setDPI(int dpi)
  {
	  for(int i = 0; i < subInstances.size(); i++)
	  {
		  subInstances.get(i).setDPI(dpi);
	  }
  }
}
