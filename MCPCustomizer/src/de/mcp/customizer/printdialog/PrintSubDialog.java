package de.mcp.customizer.printdialog;
import processing.core.PApplet;
import controlP5.ControlEvent;
import controlP5.ControlP5;

public class PrintSubDialog extends PApplet
{
  private static final long serialVersionUID = -8457197443384630485L;

  private ControlP5 controlPrintSubDialog;

  private PrintSubInstance parent;
 
  private int w, h;
  private int bgColor = 255;

  public PrintSubDialog(PrintSubInstance theParent, int theWidth, int theHeight) 
  {
    parent = theParent;
    this.w = theWidth;
    this.h = theHeight;
  }
  
  
  public void setup()
  {
    size(w, h);
    frameRate(25);
    controlPrintSubDialog = new ControlP5(this);
    controlPrintSubDialog.addButton("Send to Lasercutter")
                         .setPosition(10,300-70)
                         .setSize(100,30)
                         .setId(0);
    controlPrintSubDialog = new ControlP5(this);
    controlPrintSubDialog.addButton("Next job")
                         .setPosition(120,300-70)
                         .setSize(100,30)
                         .setId(1);
  } 

  public void draw() 
  {
      background(bgColor);
  }
  
  public ControlP5 control() 
  {
    return controlPrintSubDialog;
  } 
  
  public void setSize(int theHeight, int theWidth)
  {
    this.w = theWidth;
    this.h = theHeight;
  }
  
  public int getWidth()
  {
   return this.w; 
  }
  
  public int getHeight()
  {
   return this.h; 
  }
  
  public void controlEvent(ControlEvent theEvent) 
  {
    if(theEvent.isController() && theEvent.getName().equals("Send to Lasercutter"))
    {
      this.parent.sendLaserJob(); 
    }
    if(theEvent.isController() && theEvent.getName().equals("Next job"))
    {
      this.parent.nextJob(); 
    }
  }
}
