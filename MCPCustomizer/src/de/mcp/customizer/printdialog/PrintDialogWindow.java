package de.mcp.customizer.printdialog;
import java.io.File;
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
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.printdialog.lasercutter.LaserCutter;
import de.mcp.customizer.printdialog.selectpath.SelectPathDialogInstance;
import de.mcp.customizer.view.Transformation;

/**
 * 
 * 
 * @author Pierre
 *
 */
class PrintDialogWindow extends PApplet
{
	//TODO Refactor this class: View, controller combined in this class
	//TODO make UI look consistent
	
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
	private int selectedInstance;
	private int printCounter = 0;
	private int printer;
	private int confirmLevel;
	private int selectedDPI = 0;
	private boolean dragging;
    private String overlapMessage;
    double[] dpi;
    
    private LaserCutter selectedCutter;
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
  
	private ArrayList<PrintInstance> printInstances;
	private Vector2D originalMousePosition;
	private Rect view;
  
	public PrintDialogWindow(int theWidth, int theHeight, ArrayList<PrintInstance> printInstances)
	{
		this.printInstances = printInstances;
		if(printInstances.size() > 0)
		{
			selectedInstance = 0; 
		}
		this.w = theWidth;
		this.h = theHeight;
		selectedCutter = new LaserCutter();
		this.bedWidth = selectedCutter.returnBedWidth();
	    this.bedHeight = selectedCutter.returnBedHeight();
	    this.dragging = false;
	    this.view = new Rect(0,0,bedWidth,bedHeight);
	    this.originalMousePosition = new Vector2D(0,0);
	    this.overlapMessage = "";
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
		   			   .setText(selectedCutter.returnDevice());
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
			   		.setText(Integer.toString(selectedDPI) + " DPI");
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
	  this.dpi = this.selectedCutter.returnDPI();
	  for(int i = 0; i < dpi.length; i++) {
		  dpiBox.addItem(Double.toString(dpi[i]), i);
	  }
  }
  
  private void updateListBox()
  {
    unplacedShapesBox.clear();
    if(printInstances.size() > 0)
    {
    	ArrayList<Shape> unplacedShapes = printInstances.get(selectedInstance).getUnplacedShapes();
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
      Shape toBePlacedShape = this.printInstances.get(selectedInstance).getUnplacedShapes().get(objectIndex);
      printInstances.get(selectedInstance).placeShape(toBePlacedShape);
      updateListBox();
    } else if(theEvent.isGroup() && theEvent.getName().equals("cutterBox")) {
        int objectIndex = (int)theEvent.getGroup().getValue();
        this.selectedCutter.setDevice(objectIndex);
        this.bedWidth = selectedCutter.returnBedWidth();
        this.bedHeight = selectedCutter.returnBedHeight();
        this.objectLayout = createGraphics(bedWidth, bedHeight);
        this.view = new Rect(0,0,bedWidth,bedHeight);
        this.cutterSelected.setText(selectedCutter.returnDevice());
        this.selectedDPI = 0;
        this.dpiBox.setVisible(true);
        this.dpiSelected.setVisible(true);
        this.dpiSelectLabel.setVisible(true);
        this.dpiSelected.setText(Integer.toString(selectedDPI) + " DPI");
        updateDPIBox();
    } else if(theEvent.isGroup() && theEvent.getName().equals("dpiBox")) {
    	int objectIndex = (int)theEvent.getGroup().getValue();
    	this.selectedDPI = (int)this.dpi[objectIndex];
    	this.dpiSelected.setText(Integer.toString(selectedDPI) + " DPI");
    } else if(theEvent.isController() && theEvent.getName().equals("Start cutting")) {
       print();
    }
    else if(theEvent.isController() && theEvent.getName().equals("Export as SVG"))
    {
       printSVG();
    }
    else if(theEvent.isController() && theEvent.getName().equals("Add extra job"))
    {
    	if(printInstances.size() > 0)
    	{
    		this.printInstances.get(selectedInstance).addSubInstance();
    		createButtons();
    	}
    }
    else if(theEvent.isController() && theEvent.getName().equals("yes"))
    {
    	confirmPrint.setVisible(false);
		declinePrint.setVisible(false);
		statusLabel.setText("");
    	if(confirmLevel == 0)
    	{
    		checkUnplacedShapes();
    	} else if(confirmLevel == 1)
    	{
    		startPrint();
    		activateAll(true);
    	}
    }
    else if(theEvent.isController() && theEvent.getName().equals("no"))
    {
    	confirmPrint.setVisible(false);
		declinePrint.setVisible(false);
		activateAll(true);
		statusLabel.setText("");
    }
    else if(theEvent.isController())
    {
      int objectIndex = (int)theEvent.getController().getId();
      boolean instanceFound = false;
      int instance = 0;
      int subInstance = 0;
      int buttonNumber = 0;
      for(int i = 0; i < selectedInstance; i++)
      {
        buttonNumber += printInstances.get(i).getNumberOfSubInstances();
      }
      buttonNumber += printInstances.get(selectedInstance).getSelectedSubInstance();
      instanceButtons.get(buttonNumber).setColorBackground(color(128,128,128));
      while(!instanceFound)
      {
        if((subInstance + printInstances.get(instance).getNumberOfSubInstances()) > objectIndex)
        {
         instanceFound = true;
        } else
        {
          subInstance += printInstances.get(instance).getNumberOfSubInstances();
          instance++;
        }
      }
      subInstance = objectIndex-subInstance;
      this.selectedInstance = instance;
      printInstances.get(this.selectedInstance).setActiveSubInstance(subInstance);
      updateListBox();
      setActiveButton();
    }
  }
  
  private void setActiveButton()
  {
	  int buttonNumber = 0;
	  for(int i = 0; i < selectedInstance; i++)
	  {
		 buttonNumber += printInstances.get(i).getNumberOfSubInstances();
	  }
	  buttonNumber += printInstances.get(selectedInstance).getSelectedSubInstance();
	  instanceButtons.get(buttonNumber).setColorBackground(color(0,0,0));
  }
  
  @Override
  public void draw() 
  {
      background(255);
      drawObjectLayout();
  }
  
  
  void createButtons()
  {
	if(printInstances.size() > 0)
	{
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
      if(printInstances.size() > 0)
      {
    	  drawShapes = printInstances.get(selectedInstance).getPlacedShapes();
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
	  if(printInstances.size() > 0)
	  {
        Vector2D position =  new Vector2D(mouseX, mouseY);
        Vector2D relativePosition = this.positionRelativeToView(position);

        for (Shape s : printInstances.get(selectedInstance).getPlacedShapes()) {
            s.getGShape().setSelected(s.getGShape().mouseOver(relativePosition));
        }
	  }
  }
    
    public void mouseDragged()
    {
    	if(printInstances.size() > 0)
  	  	{
    		for (Shape s : printInstances.get(selectedInstance).getPlacedShapes()) {
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
    	if(printInstances.size() > 0)
  	  	{
	      Vector2D mousePosition = new Vector2D(mouseX, mouseY);
	      ArrayList<Shape> placedShapes = printInstances.get(selectedInstance).getPlacedShapes();
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
	               printInstances.get(selectedInstance).unplaceShape(s);
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
    
  public void print()
  {
	  printer = 0;
	  checkPrintConstraints();
  }
  
  private void checkUnplacedShapes()
  {
	  boolean unplacedShapesFound = false;
	  String result = "";
	  for(int i = 0; i < this.printInstances.size(); i++)
	  {
		  if(this.printInstances.get(i).getUnplacedShapes().size() > 0)
		  {
			  if(result.equals(""))
			  {
				  result = "there are unplaced shapes for material(s): " + this.printInstances.get(i).getMaterial().getMaterialName();
			  } else
			  {
				  result = result + ", " + this.printInstances.get(i).getMaterial().getMaterialName();
			  }
			  unplacedShapesFound = true;
		  }
	  }
	  if(unplacedShapesFound)
	  {
		  statusLabel.setText(result);
		  confirmLevel = 1;
		  activateAll(false);
		  confirmPrint.setVisible(true);
		  declinePrint.setVisible(true);
	  } else
	  {
		  startPrint();
	  }
  }
  
  private void startPrint() {
	  if(printer == 0) {
		  if(selectedCutter.returnDevice().equals("no selected")) {
			  statusLabel.setText("No lasercutter has been selected");
		  } else if (cutterAddress.getText().equals("")) { // TODO more torough check
			  statusLabel.setText("The address of the lasercutter is not specified");
		  } else if(this.selectedDPI == 0) {
			  statusLabel.setText("No DPI setting has been selected");
		  } else {
			  setLaserCutter(cutterAddress.getText());
			  setDPI();
			  printCounter = 0;
			  printInstances.get(printCounter).print();
		  }
	  } else if(printer == 1) {
		  SelectPathDialogInstance selectPathInstance = new SelectPathDialogInstance();
		  selectPathInstance.showSelectPathDialog();
		  exportSVG(selectPathInstance.getSelectedPath());
	  }
  }
  
  protected void exportSVG(File thePath) {
	  for(int i = 0; i < printInstances.size(); i++) {
		  printInstances.get(i).printSVG(thePath);
	  }
  }
  
  private boolean checkOverlap(int printInstanceIndex)
  {
	  boolean overLapped = false;
	  String result = this.printInstances.get(printInstanceIndex).checkOverlap();
	  if(!result.equals("no overlap"))
	  {
		  if(!this.overlapMessage.equals(""))
		  {
			  this.overlapMessage = this.overlapMessage + ", " + result;
		  } else
		  {
			  this.overlapMessage = this.overlapMessage + result;
		  }
		  overLapped = true;
	  }
	  return overLapped;
  }
  
  private void checkPlacedShapes()
  {
	  boolean conditionsMet = false;
	  boolean placedShapeNoMaterial = false;
	  boolean overLapped = false;
	  for(int i = 0; i < printInstances.size(); i++)
	  {
		if(printInstances.get(i).checkPlacedShapes())
		{
			if(!checkOverlap(i))
			{
				if(printInstances.get(i).getMaterial().getMaterialName().equals("Nothing 0,5 mm"))
				{
					placedShapeNoMaterial = true;
				} else
				{
					conditionsMet = true;
				}
			} else
			{
				overLapped = true;
				conditionsMet = false;
			}
		}
	  }
	  if(!conditionsMet)
	  {
		  if(overLapped)
		  {
			  statusLabel.setText(overlapMessage);
		  } else if (placedShapeNoMaterial)
		  {
			  statusLabel.setText("No shape has been placed with a material assigned");
		  } else
		  {
			  statusLabel.setText("No shape has been placed");
		  }
	  } else
	  {
		  if(placedShapeNoMaterial)
		  {
			  statusLabel.setText("There are object placed without material assigned, continue?");
			  confirmLevel = 0;
			  activateAll(false);
			  confirmPrint.setVisible(true);
			  declinePrint.setVisible(true);
		  } else
		  {
			  checkUnplacedShapes();
		  }
	  }
  }
  
  private void checkPrintConstraints()
  {
	  this.statusLabel.setText("");
	  this.overlapMessage = "";
	  if(!(printInstances.size() > 0))
	  {
		  statusLabel.setText("There are no shapes that can be printed");
	  } else
	  {
		  checkPlacedShapes();
	  }
  }
  
  public void printNext()
  {
   printCounter++;
   if(printCounter < printInstances.size())
   {
     printInstances.get(printCounter).print();
   } else
   {
	   activateAll(true);
   }
  }
  
  public void printSVG()
  {
	printer = 1;
	checkPrintConstraints();
  }  
  
  public ControlP5 control() 
  {
    return cp5;
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
  
  private void activateAll(boolean state)
  {
	  this.lasercutButton.setVisible(state);
	  this.exportSVGButton.setVisible(state);
	  this.addExtraJobButton.setVisible(state);
  }
  
  private void setLaserCutter(String address)
  {
	  for(int i = 0; i < printInstances.size(); i++)
	  {
		  printInstances.get(i).setLaserCutter(selectedCutter,address);
	  }
  }
  
  private void setDPI()
  {
	  for(int i = 0; i < printInstances.size(); i++)
	  {
		  printInstances.get(i).setDPI(this.selectedDPI);
	  }
  }
}
