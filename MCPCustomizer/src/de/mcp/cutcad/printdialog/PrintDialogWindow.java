package de.mcp.cutcad.printdialog;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.ListBox;
import controlP5.Textfield;
import controlP5.Textlabel;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.printdialog.lasercutter.LaserCutter;
import de.mcp.cutcad.view.Transformation;

/**
 * 
 * 
 * @author Pierre
 *
 */
class PrintDialogWindow extends PApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The control element of control p5. Used to control GUI elements.
	 */
	private ControlP5 cp5;
 
	/**
	 * The width in Px of the print dialog window
	 */
	private int w;
	
	/**
	 * The height in Px of the print dialog window
	 */
	private int h;
	
	/**
	 * Represents the width of the laser bed. This depends on the lasercutter selected 
	 */
	private int bedWidth;
	
	/**
	 * Represents the height of the laser bed. This depends on the lasercutter selected 
	 */
	private int bedHeight;
	
	/**
	 * Stores which of the print instances (material of a certain thickness) is currently 
	 * active. 
	 */
	private boolean dragging;
    double[] dpi;
    
    private Textfield cutterAddress;
	
	private PGraphics objectLayout;
	private ListBox unplacedShapesBox;
	private ListBox cutterBox;
	private ListBox dpiBox;
	private Textlabel statusLabel;
	private Textlabel cutterSelected;
	private Textlabel dpiSelected;
	private Textlabel dpiSelectLabel;
	private Button lasercutButton;
	private Button exportSVGButton;
	private Button addExtraJobButton;
	private Button confirmPrint;
	private Button declinePrint;
  
	private Group instanceGroup; 
	private ArrayList<Button> instanceButtons;
  
	private PrintDialogInstance printDialogInstance;
	private Vector2D originalMousePosition;
	private Rect view;
  
	public PrintDialogWindow(int theWidth, int theHeight, PrintDialogInstance printDialogInstance)
	{
		this.printDialogInstance = printDialogInstance;
		this.w = theWidth;
		this.h = theHeight;
		this.bedWidth = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnBedWidth();
	    this.bedHeight = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnBedHeight();
	    this.dragging = false;
	    this.view = new Rect(0,0,bedWidth,bedHeight);
	    this.originalMousePosition = new Vector2D(0,0);
	}
  
  @Override
  public void setup()
  {
    size(h, w);
    frameRate(25);
    cp5 = new ControlP5(this);
    objectLayout = createGraphics(bedWidth, bedHeight);
    unplacedShapesBox = cp5.addListBox("unplacedShapesList")
                         .setPosition(10, bedHeight+20)
                         .setSize(120, 120)
                         .setItemHeight(15)
                         .setBarHeight(15)
                         .setColorBackground(color(128,128,128))
                         .setColorForeground(color(0,0,0))
                         .setColorLabel(color(255,255,255))
                         .setCaptionLabel("Choose an object");
    unplacedShapesBox.getCaptionLabel().getStyle().marginTop = 3;
    unplacedShapesBox.getValueLabel().getStyle().marginTop = 3;
   updateListBox();
   lasercutButton = cp5.addButton("Start cutting")
		   			   .setPosition(10,h-70)
		   			   .setSize(100,30)
		   			   .setColorBackground(color(128,128,128))
                       .setColorForeground(color(0,0,0))
                       .setColorCaptionLabel(color(255,255,255))
		   			   .setId(0);
   exportSVGButton = cp5.addButton("Export as SVG")
		   				.setPosition(120,h-70)
		   				.setSize(80,30)
		   				.setColorBackground(color(128,128,128))
                        .setColorForeground(color(0,0,0))
                        .setColorCaptionLabel(color(255,255,255))
		   				.setId(1);
   addExtraJobButton = cp5.addButton("Add extra job")
		   				  .setPosition(10,h-140)
		   				  .setSize(100,30)
		   				  .setColorBackground(color(128,128,128))
                          .setColorForeground(color(0,0,0))
                          .setColorCaptionLabel(color(255,255,255))
		   				  .setId(2);
   statusLabel = cp5.addTextlabel("")
		   			.setPosition(10,h-100)
		   			.setSize(120,30)
		   			.setColorValueLabel(0xffff0000)
                    .setFont(createFont("Georgia",12));
   instanceButtons = new ArrayList<Button>();
   instanceGroup = cp5.addGroup("Jobs")
                      .setPosition(140,bedHeight+20)
                      .setColorBackground(color(128,128,128))
                      .setColorForeground(color(128,128,128))
                      .setColorLabel(color(255,255,255))
                      .hideArrow()
                      .disableCollapse()
                      .setWidth(790);			  
   createButtons();
   confirmPrint = cp5.addButton("yes")
   					 .setPosition(450,h-110)
   					 .setSize(30,30)
   					 .setColorBackground(color(128,128,128))
                     .setColorForeground(color(0,0,0))
                     .setColorCaptionLabel(color(255,255,255))
   					 .setVisible(false);
   declinePrint = cp5.addButton("no")
   					 .setPosition(490,h-110)
   					 .setSize(30,30)
   					 .setColorBackground(color(128,128,128))
                     .setColorForeground(color(0,0,0))
                     .setColorCaptionLabel(color(255,255,255))
   					 .setVisible(false);
   cutterBox = cp5.addListBox("cutterBox")
           		  .setPosition(w-440, 40)
           		  .setSize(120, 120)
           		  .setItemHeight(15)
           		  .setBarHeight(15)
           		  .setColorBackground(color(128,128,128))
                  .setColorForeground(color(0,0,0))
                  .setColorLabel(color(255,255,255))
           		  .setCaptionLabel("Choose an cutter");
   cutterBox.getCaptionLabel().getStyle().marginTop = 3;
   cutterBox.getValueLabel().getStyle().marginTop = 3;
   updateCutters();
   cutterAddress = cp5.addTextfield("cutterAddress")
		   			  .setPosition(w-295,40)
		   			  .setColorBackground(color(128,128,128))
		   			  .setColorForeground(color(128,128,128))
		   			  .setSize(120,30);
   cp5.addTextlabel("cutterAddressLabel")
   	  .setPosition(w-300,20)
   	  .setSize(150,30)
   	  .setColorValueLabel(0xffffff)
      .setFont(createFont("Georgia",12))
      .setText("Address of laser cutter");
   cp5.addTextlabel("cutterSelectedLabel")
	  .setPosition(w-440,200)
	  .setSize(150,30)
	  .setColorValueLabel(0xffffff)
	  .setFont(createFont("Georgia",12))
	  .setText("Lasercutter selected");
   cutterSelected = cp5.addTextlabel("cutterSelected")
		   			   .setPosition(w-440,220)
		   			   .setSize(150,30)
		   			   .setColorValueLabel(0xffffff)
		   			   .setFont(createFont("Georgia",12))
		   			   .setText(this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnDevice());
   dpiBox = cp5.addListBox("dpiBox")
		   	   .setPosition(w-140,40)
		   	   .setSize(120, 120)
		   	   .setItemHeight(15)
     		   .setBarHeight(15)
     		   .setVisible(false)
     		   .setColorBackground(color(128,128,128))
               .setColorForeground(color(0,0,0))
               .setColorLabel(color(255,255,255))
     		   .setCaptionLabel("Choose a dpi setting");
   dpiBox.getCaptionLabel().getStyle().marginTop = 3;
   dpiBox.getValueLabel().getStyle().marginTop = 3;
   dpiSelected = cp5.addTextlabel("dpiSelected")
			   		.setPosition(w-140,220)
			   		.setSize(150,30)
			   		.setColorValueLabel(0xffffff)
			   		.setFont(createFont("Georgia",12))
			   		.setVisible(false)
			   		.setText(Integer.toString(this.printDialogInstance.getLaserCutterSettings().getDPI()) + " DPI");
   dpiSelectLabel = cp5.addTextlabel("dpiSelectLabel")
		   			   .setPosition(w-140,200)
		   			   .setSize(150,30)
		   			   .setColorValueLabel(0xffffff)
		   			   .setFont(createFont("Georgia",12))
		   			   .setVisible(false)
		   			   .setText("DPI setting selected");
  }
  
  /**
   * This method updates the list of laser cutters in the dropdown box for 
   * selecting the laser cutter to be used. 
   */
  private void updateCutters()
  {
	  cutterBox.clear();
	  boolean done = false;
	  int counter = 0;
	  while(!done)
	  {
		  LaserCutter cutter = new LaserCutter();
		  cutter.setDevice(counter);
		  if(!cutter.returnDevice().equals("no selected")) {
			  cutterBox.addItem(cutter.returnDevice(), counter);
		  } else {
			  done = true;
		  }
		  counter++;
	  }
  }
  
  private void updateDPIBox()
  {
	  dpiBox.clear();
	  this.dpi = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnDPI();
	  for(int i = 0; i < dpi.length; i++) {
		  dpiBox.addItem(Double.toString(dpi[i]), i);
	  }
  }
  
  private void updateListBox()
  {
    unplacedShapesBox.clear();
    if(this.printDialogInstance.printInstancesNotEmpty())
    {
    	ArrayList<Shape> unplacedShapes = this.printDialogInstance.getUnplacedShapes();
    	for(int i = 0; i < unplacedShapes.size(); i++)
    	{
    		unplacedShapesBox.addItem(unplacedShapes.get(i).getGShape().getName(), i);
    	}
    	unplacedShapesBox.updateListBoxItems();
    }
  }
  
  public void controlEvent(ControlEvent theEvent) 
  {
    if(theEvent.isGroup() && theEvent.getName().equals("unplacedShapesList"))
    {
    	int objectIndex = (int)theEvent.getGroup().getValue();
    	shapeListHandler(objectIndex);
    } else if(theEvent.isGroup() && theEvent.getName().equals("cutterBox")) {
    	
        int objectIndex = (int)theEvent.getGroup().getValue();
        cutterBoxHandler(objectIndex);
    } else if(theEvent.isGroup() && theEvent.getName().equals("dpiBox")) {
    	
    	int objectIndex = (int)theEvent.getGroup().getValue();
    	dpiListHandler(objectIndex);
    } else if(theEvent.isController() && theEvent.getName().equals("Start cutting")) {
    	printHandler(false);
    }
    else if(theEvent.isController() && theEvent.getName().equals("Export as SVG"))
    {
    	printHandler(true);
    }
    else if(theEvent.isController() && theEvent.getName().equals("Add extra job"))
    {
      	addJobHandler();
    }
    else if(theEvent.isController() && theEvent.getName().equals("yes"))
    {
    	confirmHandler();	
    }
    else if(theEvent.isController() && theEvent.getName().equals("no"))
    {
		declineHandler();
    }
    else if(theEvent.isController())
    {
      int objectIndex = (int)theEvent.getController().getId();
      selectInstanceHandler(objectIndex);
    }
  }
  
  private void shapeListHandler(int objectIndex) {
      this.printDialogInstance.placeShape(objectIndex);
      updateListBox();
  }
  
  private void cutterBoxHandler(int objectIndex) {
	  
	  this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().setDevice(objectIndex);
      this.bedWidth = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnBedWidth();
      this.bedHeight = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnBedHeight();
      this.objectLayout = createGraphics(bedWidth, bedHeight);
      this.view = new Rect(0,0,bedWidth,bedHeight);
      this.cutterSelected.setText(this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnDevice());
      this.printDialogInstance.getLaserCutterSettings().setDPI(0);
      this.dpiBox.setVisible(true);
      this.dpiSelected.setVisible(true);
      this.dpiSelectLabel.setVisible(true);
      this.dpiSelected.setText(Integer.toString(this.printDialogInstance.getLaserCutterSettings().getDPI()) + " DPI");
      updateDPIBox();
  }
  
  private void dpiListHandler(int objectIndex) {
	  this.printDialogInstance.getLaserCutterSettings().setDPI((int)this.dpi[objectIndex]);
  	this.dpiSelected.setText(Integer.toString(this.printDialogInstance.getLaserCutterSettings().getDPI()) + " DPI");
  }
  
  public void printHandler(boolean SVG)
  {
	  this.printDialogInstance.getLaserCutterSettings().setAddress(cutterAddress.getText());
	  String result = printDialogInstance.print(SVG);
	  if(!result.equals("passed")) {
		  statusLabel.setText(result);
	  }
  }
  
  private void addJobHandler() {
	  if(this.printDialogInstance.printInstancesNotEmpty())
	  	{
		  this.printDialogInstance.addSubInstance();
	  		createButtons();
	  	}
	  
  }
  
  private void confirmHandler() {
	  confirmPrint.setVisible(false);
		declinePrint.setVisible(false);
		statusLabel.setText("");
		String result = printDialogInstance.continuePrint();
		if(!result.equals("passed")) {
			statusLabel.setText(result);
			activateAll(true);
		}
  }
  
  private void declineHandler() {
	  confirmPrint.setVisible(false);
		declinePrint.setVisible(false);
		activateAll(true);
		statusLabel.setText("");
  }
  
  private void selectInstanceHandler(int objectIndex) {
	  instanceButtons.get(this.printDialogInstance.activeButtonNumber()).setColorBackground(color(128,128,128));
	  this.printDialogInstance.selectInstanceByButtonID(objectIndex);
	  instanceButtons.get(this.printDialogInstance.activeButtonNumber()).setColorBackground(color(0,0,0));
	  updateListBox();
  }
  
  private void setActiveButton()
  {
	  instanceButtons.get(this.printDialogInstance.activeButtonNumber()).setColorBackground(color(0,0,0));
  }
  
  @Override
  public void draw() 
  {
      background(255);
      drawObjectLayout();
  }
  
  
  void createButtons()
  {
	if(this.printDialogInstance.printInstancesNotEmpty()) {
		ArrayList<PrintInstance> printInstances = this.printDialogInstance.getPrintInstances();
	    int buttonNumber = 0;
	    for(int i = 0; i < instanceButtons.size(); i++)
	    {
	      cp5.remove(instanceButtons.get(i));
	      instanceButtons.get(i).remove();
	    }
	    instanceButtons = new ArrayList<Button>();
	    for(int i = 0; i < printInstances.size(); i++)
	    {
	      for(int j = 0; j < printInstances.get(i).getNumberOfSubInstances(); j++)
	      {
	       int row = (buttonNumber/5);
	       int collumn = (buttonNumber%5);
	       int name = j+1;
	       Button newButton = cp5.addButton(printInstances.get(i).getMaterial().getMaterialName() + " - " + name)
	                             .setPosition((collumn*160),(row*50))
	                             .setSize(150,30)
	                             .setId(buttonNumber)
	                             .setColorBackground(color(128,128,128))
	                             .setColorForeground(color(0,0,0))
	                             .setColorCaptionLabel(color(255,255,255))
	                             .setGroup(instanceGroup); 
	       instanceButtons.add(newButton); 
	       buttonNumber++;
	      }
	    }
	    setActiveButton();
	}
  }
  
  void drawObjectLayout()
  {
      objectLayout.beginDraw();
      objectLayout.background(100);
      ArrayList<Shape> drawShapes = new ArrayList<Shape>();
      Transformation t = new Transformation(1, new Vector2D(0,0));
      if(this.printDialogInstance.printInstancesNotEmpty())
      {
    	  drawShapes = this.printDialogInstance.getPlacedShapes();
      }
      for(int i = 0; i < drawShapes.size(); i++)
      {
       drawShapes.get(i).getGShape().draw2D(objectLayout, t);
      }
      objectLayout.endDraw();
      image(objectLayout, 0, 0);
  }
  
  public void mouseMoved()
  {
	  if(this.printDialogInstance.printInstancesNotEmpty())
	  {
        Vector2D position =  new Vector2D(mouseX, mouseY);
        Vector2D relativePosition = this.positionRelativeToView(position);

        for (Shape s :  this.printDialogInstance.getPlacedShapes()) {
            s.getGShape().setSelected(s.getGShape().mouseOver(relativePosition));
        }
	  }
  }
    
    public void mouseDragged()
    {
    	if(this.printDialogInstance.printInstancesNotEmpty())
  	  	{
    		for(Shape s : this.printDialogInstance.getPlacedShapes()) {
	            if (s.getGShape().isSelected() && this.dragging)
	            {
	                Vector2D position = new Vector2D(mouseX, mouseY);
	                Vector2D currentMousePosition = this.positionRelativeToView(position);
	                s.getGShape().translate2D(currentMousePosition.sub(originalMousePosition));
	                originalMousePosition.set(currentMousePosition);
	            }
    		}
  	  	}
    }
    
    public void mousePressed()
    {
    	if(this.printDialogInstance.printInstancesNotEmpty())
  	  	{
	      Vector2D mousePosition = new Vector2D(mouseX, mouseY);
	      ArrayList<Shape> placedShapes = this.printDialogInstance.getPlacedShapes();
	      ArrayList<Shape> shapes = new ArrayList<Shape>();
	      for(int i = 0; i < placedShapes.size(); i++)
	      {
	       shapes.add(placedShapes.get(i)); 
	      }
	      for (Shape s : shapes)
	      {
	            if (s.getGShape().isSelected() && mouseButton == PConstants.LEFT)
	            {
	                this.dragging = true;
	                Vector2D currentMousePosition = this.positionRelativeToView(mousePosition);
	                this.originalMousePosition.set(currentMousePosition);
	            } else if (s.getGShape().isSelected() && mouseButton == PConstants.RIGHT){
	            	this.printDialogInstance.unplaceShape(s);
	               updateListBox();
	            }
	      }
  	  	}
    }
    
    public void mouseReleased()
    {
      if (mouseButton == PConstants.LEFT) 
      {
         this.dragging = false;
      }
    }
    
    private Vector2D positionRelativeToView(Vector2D inPosition) 
    {
        return inPosition.sub(this.view.getTopLeft());
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
  
  private void activateAll(boolean state)
  {
	  this.lasercutButton.setVisible(state);
	  this.exportSVGButton.setVisible(state);
	  this.addExtraJobButton.setVisible(state);
  }
  
  void printComplete() {
	  activateAll(true);
  }

public void confirmationPrint() {
	activateAll(false);
	confirmPrint.setVisible(true);
	declinePrint.setVisible(true);
	
}
}
