package de.mcp.customizer.printdialog;
import processing.core.PApplet;
import controlP5.ControlEvent;
import controlP5.ControlP5;

/**
 * This class represents the dialogwindow used to send the lasercut job.
 * The window belongs to the printSubInstance which called it. The printSubDialogWindow
 * signals the printSubInstance to which it belongs whether a button is pressed.
 * 
 * @author Pierre
 *
 */
class PrintSubDialogWindow extends PApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8457197443384630485L;

	/**
	 * Contains the name of the print job to be confirmed by this window.
	 * The name consists of the type of material, thickness and number of the subInstance
	 */
	String printJobName;
	
	/**
	 * The control element of ControlP5 needed for the widgets
	 */
	private ControlP5 controlPrintSubDialog;

	/**
	 * The printSubInstance which creates this Window.
	 * The printSubDialogWindow needs to know the instance which created it,
	 * because it needs to signal the instance whether buttons are pressed.
	 */
	private PrintSubInstance parent;
 
	/**
	 * The width of the printSubDialogWindow
	 */
	private int w;
  
	/**
	 * The height of the printSubDialogWindow
	 */
	private int h;

  /**
   * Creates an instance witch contains the printSubDialogWindow.
   * Then constructor sets the parent of the printSubDialogWindow.
   * The parent is a printSubInstance which created the printSubDialogWindow
   * Furthermore, the height and width of the printSubDialogWindow is set and
   * the name of the print job to be confirmed by the printSubDialogWindow
   * 
   * @param theParent
   * The printSubInstance which created this printSubDialog
   * 
   * @param printJobName
   * The name of the print job to be confirmed by this window.
   * The name consists of the type of material, thickness and number of the subInstance
   * 
   * @param theWidth
   * The intended width of the printSubDialogWindow
   * 
   * @param theHeight
   * The intended height of the printSubDialogWindow
   */
  PrintSubDialogWindow(PrintSubInstance theParent, String printJobName, int theWidth, int theHeight) 
  {
    parent = theParent;
    this.printJobName = printJobName;
    this.w = theWidth;
    this.h = theHeight;
  }
  
  @Override
  public void setup()
  {
    size(h, w);
    frameRate(25);
    controlPrintSubDialog = new ControlP5(this);
    controlPrintSubDialog.addButton("Send to Lasercutter")
                         .setPosition(10, this.h-70)
                         .setSize(95,30)
                         .setColorBackground(color(128,128,128))
                         .setColorForeground(color(0,0,0))
                         .setColorCaptionLabel(color(255,255,255))
                         .setId(0);
    controlPrintSubDialog.addButton("Next job")
                         .setPosition(w-55, this.h-70)
                         .setSize(45,30)
                         .setColorBackground(color(128,128,128))
                         .setColorForeground(color(0,0,0))
                         .setColorCaptionLabel(color(255,255,255))
                         .setId(1);
    controlPrintSubDialog.addTextlabel("instructionLabel")
    					 .setPosition(10,10)
    					 .setColorValueLabel(color(0,0,0))
    					 .setFont(createFont("Georgia",16))
    					 .setText("Please put a sheet off:");
    int cutIndex = printJobName.indexOf("-");
    String material = printJobName.substring(0,cutIndex-1);
    controlPrintSubDialog.addTextlabel("instructionLabel2")
	 					 .setPosition(10,35)
	 					 .setColorValueLabel(color(0,0,0))
    					 .setFont(createFont("Georgia",16))
	 					 .setText(material);
    controlPrintSubDialog.addTextlabel("instructionLabel3")
	 					 .setPosition(10,60)
	 					 .setColorValueLabel(color(0,0,0))
	 					 .setFont(createFont("Georgia",16))
	 					 .setText("on the laserbed of the laser cutter.");
    controlPrintSubDialog.addTextlabel("instructionLabel4")
	 					 .setPosition(10,80)
	 					 .setColorValueLabel(color(0,0,0))
	 					 .setFont(createFont("Georgia",16))
	 					 .setText("Afterwards push the send to");
    controlPrintSubDialog.addTextlabel("instructionLabel5")
	 					 .setPosition(10,100)
	 					 .setColorValueLabel(color(0,0,0))
	 					 .setFont(createFont("Georgia",16))
	 					 .setText("lasercutter button. After cutting");
    controlPrintSubDialog.addTextlabel("instructionLabel6")
	 					 .setPosition(10,120)
	 					 .setColorValueLabel(color(0,0,0))
	 					 .setFont(createFont("Georgia",16))
	 					 .setText("is finished. Push the Next job");
    controlPrintSubDialog.addTextlabel("instructionLabel7")
	 					 .setPosition(10,140)
	 					 .setColorValueLabel(color(0,0,0))
	 					 .setFont(createFont("Georgia",16))
	 					 .setText("button.");
  } 

  @Override
  public void draw() 
  {
      background(255);
  }
  
  @Override
  public void setSize(int theHeight, int theWidth)
  {
    this.w = theWidth;
    this.h = theHeight;
  }
  
  @Override
  public int getWidth()
  {
   return this.w; 
  }
  
  @Override
  public int getHeight()
  {
   return this.h; 
  }
  
  /**
   * The event handler for this window. 
   * In the parameter the event which caused this method to be called is entailed.
   * This can be two events: the button to send the print job is pressed or to go to the next 
   * print job. When one of the buttons is pressed, a method from the printSubInstance which
   * created this PrintSubInstanceWindow is called which then handles the event.
   * 
   * @param theEvent
   * Contains the event which caused this method to be called
   */
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
