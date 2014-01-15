package de.mcp.customizer.printdialog;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.ListBox;
import controlP5.Textlabel;
import de.mcp.customizer.model.Shape;

public class PrintDialogFrame extends PApplet
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  private ControlP5 cp5;
 
  private int w, h;
  private int bgColor = 255;
  private int bedWidth, bedHeight;
  private int selectedInstance;
  private int printCounter = 0;
  private int printer;
  private int confirmLevel;
  private boolean dragging;
   
  private PGraphics objectLayout;
  private ListBox unplacedShapesBox;
  private Textlabel statusLabel;
  private Button confirmPrint;
  private Button declinePrint;
  
  private Group instanceGroup; 
  private ArrayList<Button> instanceButtons;
  
  private ArrayList<PrintInstance> printInstances;
  private Vec2D originalMousePosition;
  private Rect view;
  
  public PrintDialogFrame(int theWidth, int theHeight, ArrayList<PrintInstance> printInstances) 
  {
    this.printInstances = printInstances;
    if(printInstances.size() > 0)
    {
     selectedInstance = 0; 
    }
    this.w = theWidth;
    this.h = theHeight;
    this.bedWidth = 600; // These depend on the lasercutter used
    this.bedHeight = 300;
    this.dragging = false;
    this.view = new Rect(0,0,bedWidth,bedHeight);
    this.originalMousePosition = new Vec2D(0,0);
  }
  
  
  public void setup()
  {
    size(w, h);
    frameRate(25);
    cp5 = new ControlP5(this);
    objectLayout = createGraphics(bedWidth, bedHeight);
    unplacedShapesBox = cp5.addListBox("unplacedShapesList")
                         .setPosition(10, bedHeight+20)
                         .setSize(120, 120)
                         .setItemHeight(15)
                         .setBarHeight(15)
                         ;
   unplacedShapesBox.setCaptionLabel("Choose an object");
   unplacedShapesBox.getCaptionLabel().getStyle().marginTop = 3;
   unplacedShapesBox.getValueLabel().getStyle().marginTop = 3;
   updateListBox();
   cp5.addButton("Start cutting")
      .setPosition(10,h-70)
      .setSize(100,30)
      .setId(0);
   cp5.addButton("Export as SVG")
      .setPosition(120,h-70)
      .setSize(80,30)
      .setId(1);
   cp5.addButton("Add extra job")
      .setPosition(10,h-140)
      .setSize(100,30)
      .setId(2);
   statusLabel = cp5.addTextlabel("")
		   			.setPosition(10,h-100)
		   			.setSize(120,30)
		   			.setColorValueLabel(0xffff0000)
                    .setFont(createFont("Georgia",12));
   instanceButtons = new ArrayList<Button>();
   instanceGroup = cp5.addGroup("Jobs")
                      .setPosition(140,bedHeight+20)
                      .setColorBackground(0x00000000)
                      .setWidth(450);			  
   createButtons();
   confirmPrint = cp5.addButton("yes")
   					 .setPosition(420,h-110)
   					 .setSize(30,30)
   					 .setVisible(false);
   declinePrint = cp5.addButton("no")
   					 .setPosition(460,h-110)
   					 .setSize(30,30)
   					 .setVisible(false);
  }
  
  private void updateListBox()
  {
    unplacedShapesBox.clear();
    if(printInstances.size() > 0)
    {
    	ArrayList<Shape> unplacedShapes = printInstances.get(selectedInstance).getUnplacedShapes();
    	for(int i = 0; i < unplacedShapes.size(); i++)
    	{
    		int displayNumber = i+1;
    		unplacedShapesBox.addItem("object " + displayNumber, i);
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
    }
    else if(theEvent.isController() && theEvent.getName().equals("Start cutting"))
    {
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
    	}
    }
    else if(theEvent.isController() && theEvent.getName().equals("no"))
    {
    	confirmPrint.setVisible(false);
		declinePrint.setVisible(false);
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
      instanceButtons.get(buttonNumber).setColorBackground(0x00000000);
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
	  instanceButtons.get(buttonNumber).setColorBackground(0xffff0000);
  }
  
  public void draw() 
  {
      background(bgColor);
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
	       int row = (buttonNumber/4);
	       int collumn = (buttonNumber%4);
	       int name = j+1;
	       Button newButton = cp5.addButton(printInstances.get(i).getMaterial().getMaterialName() + " - " + name) //replace for material name and thickness
	                             .setPosition((collumn*110),(row*50))
	                             .setSize(100,30)
	                             .setId(buttonNumber)
	                             .setColorBackground(0x00000000)
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
      if(printInstances.size() > 0)
      {
    	  drawShapes = printInstances.get(selectedInstance).getPlacedShapes();
      }
      for(int i = 0; i < drawShapes.size(); i++)
      {
       drawShapes.get(i).getShape().draw2D(objectLayout);
      }
      objectLayout.endDraw();
      image(objectLayout, 0, 0);
  }
  
  public void mouseMoved()
  {
	  if(printInstances.size() > 0)
	  {
        Vec2D position =  new Vec2D(mouseX, mouseY);
        Vec2D relativePosition = this.positionRelativeToView(position);

        for (Shape s : printInstances.get(selectedInstance).getPlacedShapes()) {
            s.getShape().setSelected(s.getShape().mouseOver(relativePosition));
        }
	  }
  }
    
    public void mouseDragged()
    {
    	if(printInstances.size() > 0)
  	  	{
    		for (Shape s : printInstances.get(selectedInstance).getPlacedShapes()) {
	            if (s.getShape().isSelected() && this.dragging)
	            {
	                Vec2D position = new Vec2D(mouseX, mouseY);
	                Vec2D currentMousePosition = this.positionRelativeToView(position);
	                s.getShape().translate2D(currentMousePosition.sub(originalMousePosition));
	                originalMousePosition.set(currentMousePosition);
	            }
    		}
  	  	}
    }
    
    public void mousePressed()
    {
    	if(printInstances.size() > 0)
  	  	{
	      Vec2D mousePosition = new Vec2D(mouseX, mouseY);
	      ArrayList<Shape> placedShapes = printInstances.get(selectedInstance).getPlacedShapes();
	      ArrayList<Shape> shapes = new ArrayList<Shape>();
	      for(int i = 0; i < placedShapes.size(); i++)
	      {
	       shapes.add(placedShapes.get(i)); 
	      }
	      for (Shape s : shapes)
	      {
	            if (s.getShape().isSelected() && mouseButton == PConstants.LEFT)
	            {
	                this.dragging = true;
	                Vec2D currentMousePosition = this.positionRelativeToView(mousePosition);
	                this.originalMousePosition.set(currentMousePosition);
	            } else if (s.getShape().isSelected() && mouseButton == PConstants.RIGHT){
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
    
    private Vec2D positionRelativeToView(Vec2D inPosition) 
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
	  //TO DO: Dialog if user wants to continue printing if there are unplaced shapes
	  startPrint();
  }
  
  private void startPrint()
  {
	  if(printer == 0)
	  {
		  printCounter = 0;
		  printInstances.get(printCounter).print();
	  } else if(printer == 1)
	  {
		  for(int i = 0; i < printInstances.size(); i++)
		  {
			  printInstances.get(i).printSVG();
		  }
	  }
  }
  
  private boolean checkOverlap()
  {
	  //TO DO: check if there are any overlapping shapes and tell user where they are overlapping, printing is cancelled by overlap
	  return false;
  }
  
  private void checkPlacedShapes()
  {
	  boolean conditionsMet = false;
	  boolean placedShapeNoMaterial = false;
	  for(int i = 0; i < printInstances.size(); i++)
	  {
		if(printInstances.get(i).checkPlacedShapes())
		{
			if(printInstances.get(i).getMaterial().getMaterialName().equals("Nothing 0,5 mm"))
			{
				placedShapeNoMaterial = true;
			} else
			{
				conditionsMet = true;
			}
		}
	  }
	  if(!conditionsMet && placedShapeNoMaterial)
	  {
		  statusLabel.setText("No shape has been placed with a material assigned");
	  }
	  else if(!conditionsMet && !placedShapeNoMaterial)
	  {
		  statusLabel.setText("No shape has been placed");
	  }
	  else if(conditionsMet && placedShapeNoMaterial)
	  {
		  conditionsMet = checkOverlap();
		  statusLabel.setText("There are object placed without material assigned, continue?");
		  confirmLevel = 0;
		  confirmPrint.setVisible(true);
		  declinePrint.setVisible(true);
	  } else
	  {
		  checkUnplacedShapes();
	  }
  }
  
  private void checkPrintConstraints()
  {
	  statusLabel.setText("");
	  boolean conditionsMet = (printInstances.size() > 0);
	  if(!conditionsMet)
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
}
