import processing.core.PApplet;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;

public class PrintSubDialog extends PApplet
{
  private ControlP5 controlPrintSubDialog;

  private PrintSubInstance parent;
 
  private int w, h;
  private int bgColor = 255;
  
  private boolean sendConfirm;
  
  private Button sendButton;
  private Button nextButton;
  
  private PrintSubDialog() 
  {
    
  }

  public PrintSubDialog(PrintSubInstance theParent, int theWidth, int theHeight) 
  {
    parent = theParent;
    //printInstances.get(selectedInstance).placeShape(printInstances.get(selectedInstance).getUnplacedShapes().get(0));
    //printInstances.get(selectedInstance).placeShape(printInstances.get(selectedInstance).getUnplacedShapes().get(0));
    this.w = theWidth;
    this.h = theHeight;
    sendConfirm = true;
  }
  
  
  public void setup()
  {
    size(w, h);
    frameRate(25);
    controlPrintSubDialog = new ControlP5(this);
    sendButton = controlPrintSubDialog.addButton("Send to Lasercutter")
                                      .setPosition(10,300-70)
                                      .setSize(100,30)
                                      .setId(0);
    controlPrintSubDialog = new ControlP5(this);
    nextButton = controlPrintSubDialog.addButton("Next job")
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
    if(theEvent.isController() && theEvent.name().equals("Send to Lasercutter"))
    {
      this.parent.sendLaserJob(); 
    }
    if(theEvent.isController() && theEvent.name().equals("Next job"))
    {
      this.parent.nextJob(); 
    }
  }
}
