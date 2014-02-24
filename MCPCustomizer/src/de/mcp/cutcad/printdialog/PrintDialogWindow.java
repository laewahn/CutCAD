package de.mcp.cutcad.printdialog;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
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
	
	
	private boolean dragging;
    private double[] dpi;
    
    private PGraphics objectLayout;
  
	private WidgetContainer printDialogWidgets;
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
    this.cp5 = new ControlP5(this);
    this.objectLayout = createGraphics(bedWidth, bedHeight);
    this.printDialogWidgets = new WidgetContainer(this.cp5, this.printDialogInstance);
    this.printDialogWidgets.setupButtons(this.h, this.w, this.bedHeight, this.bedWidth);
  }
  
  @Override
  public void draw() 
  {
      background(255);
      drawObjectLayout();
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
      this.printDialogWidgets.updateListBox();
  }
  
  private void cutterBoxHandler(int objectIndex) {
	  
	  this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().setDevice(objectIndex);
      this.bedWidth = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnBedWidth();
      this.bedHeight = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnBedHeight();
      this.objectLayout = createGraphics(bedWidth, bedHeight);
      this.view = new Rect(0,0,bedWidth,bedHeight);
      this.printDialogWidgets.setCutterSelected(this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnDevice());
      this.printDialogInstance.getLaserCutterSettings().setDPI(0);
      this.dpi = this.printDialogWidgets.showDPIBox();
  }
  
  private void dpiListHandler(int objectIndex) {
	  this.printDialogInstance.getLaserCutterSettings().setDPI((int)this.dpi[objectIndex]);
  	this.printDialogWidgets.setDPISelected(Integer.toString(this.printDialogInstance.getLaserCutterSettings().getDPI()) + " DPI");
  }
  
  public void printHandler(boolean SVG)
  {
	  this.printDialogInstance.getLaserCutterSettings().setAddress(this.printDialogWidgets.getCutterAddress());
	  String result = printDialogInstance.print(SVG);
	  if(!result.equals("passed")) {
		  this.printDialogWidgets.setStatusLabelText(result);
	  }
  }
  
  private void addJobHandler() {
	  if(this.printDialogInstance.printInstancesNotEmpty())
	  	{
		  this.printDialogInstance.addSubInstance();
	  		this.printDialogWidgets.createButtons();
	  	}
	  
  }
  
  private void confirmHandler() {
	  this.printDialogWidgets.activateConfirm(false);
		this.printDialogWidgets.setStatusLabelText("");
		String result = printDialogInstance.continuePrint();
		if(!result.equals("passed")) {
			this.printDialogWidgets.setStatusLabelText(result);
			this.printDialogWidgets.activateAll(true);
		}
  }
  
  private void declineHandler() {
	  this.printDialogWidgets.activateConfirm(false);
		this.printDialogWidgets.activateAll(true);
		this.printDialogWidgets.setStatusLabelText("");
  }
  
  private void selectInstanceHandler(int objectIndex) {
	  this.printDialogWidgets.setButtonUnactive();
	  this.printDialogInstance.selectInstanceByButtonID(objectIndex);
	  this.printDialogWidgets.setActiveButton();
	  printDialogWidgets.updateListBox();
  }
  
  void printComplete() {
	  this.printDialogWidgets.activateAll(true);
  }

public void confirmationPrint() {
	this.printDialogWidgets.activateAll(false);
	this.printDialogWidgets.activateConfirm(true);
	
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
	            	this.printDialogWidgets.updateListBox();
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
}
