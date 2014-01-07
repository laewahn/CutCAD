import processing.core.*;

import java.util.*;

public class PrintInstance
{
  private ArrayList<Shape> shapes;
  private ArrayList<PrintSubInstance> subInstances;
  private Material material;
  private int selected;
  private int instancesPrinted;
  
  private PrintDialogFrame parent;
  
  public PrintInstance(Shape shape, Material material) //Add material and thichkness ass both are important for settings
  {
    this.shapes = new ArrayList<Shape>();
    this.shapes.add(shape);
    subInstances = new ArrayList<PrintSubInstance>();
    PrintSubInstance initial = new PrintSubInstance(this);
    subInstances.add(initial);
    selected = getSubInstanceIndex(initial);
    this.material = material;
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
   subInstances.get(0).print(this.material.getMaterialName() + " - " + "1");
  }
  
  public void printNext()
  {
   instancesPrinted++;
   if(instancesPrinted < subInstances.size())
   {
    int name = instancesPrinted+1;
    subInstances.get(0).print(this.material.getMaterialName() + " - " + name);
   } else
   {
    this.parent.printNext(); 
   }
  }
  
  public void printSVG()
  {
   for(int i = 0; i < subInstances.size(); i++)
   {
    int name = i+1;
    subInstances.get(i).printSVG(this.material.getMaterialName() + " - " + name);
   } 
  }
  
  public void setParent(PrintDialogFrame parent)
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
}
