package de.mcp.cutcad.printdialog;

import java.util.ArrayList;

import processing.core.PApplet;
import controlP5.Button;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.ListBox;
import controlP5.Textfield;
import controlP5.Textlabel;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.printdialog.lasercutter.LaserCutter;

class WidgetContainer extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -107837967380117539L;
	
	PrintDialogInstance printDialogInstance;
	ControlP5 printDialogWindowController;
	
	private Textfield cutterAddress;

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
	
	WidgetContainer(ControlP5 printDialogWindowController, PrintDialogInstance printDialogInstance) {
		this.printDialogWindowController = printDialogWindowController;
		this.printDialogInstance = printDialogInstance;
	}
	
	void setupButtons(int h, int w, int bedHeight, int bedWidth) {
		
	    unplacedShapesBox = printDialogWindowController.addListBox("unplacedShapesList")
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
	    lasercutButton = printDialogWindowController.addButton("Start cutting")
									   			    .setPosition(10,h-70)
									   			    .setSize(100,30)
									   			    .setColorBackground(color(128,128,128))
							                        .setColorForeground(color(0,0,0))
							                        .setColorCaptionLabel(color(255,255,255))
									   			    .setId(0);
	    exportSVGButton = printDialogWindowController.addButton("Export as SVG")
			   										 .setPosition(120,h-70)
			   										 .setSize(80,30)
			   										 .setColorBackground(color(128,128,128))
			   										 .setColorForeground(color(0,0,0))
			   										 .setColorCaptionLabel(color(255,255,255))
			   										 .setId(1);
	   addExtraJobButton = printDialogWindowController.addButton("Add extra job")
			   				  						  .setPosition(10,h-140)
			   				  						  .setSize(100,30)
			   				  						  .setColorBackground(color(128,128,128))
			   				  						  .setColorForeground(color(0,0,0))
			   				  						  .setColorCaptionLabel(color(255,255,255))
			   				  						  .setId(2);
	   statusLabel = printDialogWindowController.addTextlabel("")
			   									.setPosition(10,h-100)
			   									.setSize(120,30)
			   									.setColorValueLabel(0xffff0000)
			   									.setFont(createFont("Georgia",12));
	   instanceButtons = new ArrayList<Button>();
	   instanceGroup = printDialogWindowController.addGroup("Jobs")
	                      						  .setPosition(140,bedHeight+20)
	                      						  .setColorBackground(color(128,128,128))
	                      						  .setColorForeground(color(128,128,128))
	                      						  .setColorLabel(color(255,255,255))
	                      						  .hideArrow()
	                      						  .disableCollapse()
	                      						  .setWidth(790);			  
	   createButtons();
	   confirmPrint = printDialogWindowController.addButton("yes")
	   					 						 .setPosition(450,h-110)
	   					 						 .setSize(30,30)
	   					 						 .setColorBackground(color(128,128,128))
	   					 						 .setColorForeground(color(0,0,0))
	   					 						 .setColorCaptionLabel(color(255,255,255))
	   					 						 .setVisible(false);
	   declinePrint = printDialogWindowController.addButton("no")
			   									 .setPosition(490,h-110)
			   									 .setSize(30,30)
			   									 .setColorBackground(color(128,128,128))
			   									 .setColorForeground(color(0,0,0))
			   									 .setColorCaptionLabel(color(255,255,255))
			   									 .setVisible(false);
	   cutterBox = printDialogWindowController.addListBox("cutterBox")
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
	   cutterAddress = printDialogWindowController.addTextfield("cutterAddress")
			   			  						  .setPosition(w-295,40)
			   			  						  .setColorBackground(color(128,128,128))
			   			  						  .setColorForeground(color(128,128,128))
			   			  						  .setSize(120,30);
	   printDialogWindowController.addTextlabel("cutterAddressLabel")
	   	  						  .setPosition(w-300,20)
	   	  						  .setSize(150,30)
	   	  						  .setColorValueLabel(0xffffff)
	   	  						  .setFont(createFont("Georgia",12))
	   	  						  .setText("Address of laser cutter");
	   printDialogWindowController.addTextlabel("cutterSelectedLabel")
		  						  .setPosition(w-440,200)
		  						  .setSize(150,30)
		  						  .setColorValueLabel(0xffffff)
		  						  .setFont(createFont("Georgia",12))
		  						  .setText("Lasercutter selected");
	   cutterSelected = printDialogWindowController.addTextlabel("cutterSelected")
			   			   						   .setPosition(w-440,220)
			   			   						   .setSize(150,30)
			   			   						   .setColorValueLabel(0xffffff)
			   			   						   .setFont(createFont("Georgia",12))
			   			   .setText(this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnDevice());
	   dpiBox = printDialogWindowController.addListBox("dpiBox")
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
	   dpiSelected = printDialogWindowController.addTextlabel("dpiSelected")
				   								.setPosition(w-140,220)
				   								.setSize(150,30)
				   								.setColorValueLabel(0xffffff)
				   								.setFont(createFont("Georgia",12))
				   								.setVisible(false)
				   								.setText(Integer.toString(this.printDialogInstance.getLaserCutterSettings().getDPI()) + " DPI");
	   dpiSelectLabel = printDialogWindowController.addTextlabel("dpiSelectLabel")
			   			   						   .setPosition(w-140,200)
			   			   						   .setSize(150,30)
			   			   						   .setColorValueLabel(0xffffff)
			   			   						   .setFont(createFont("Georgia",12))
			   			   						   .setVisible(false)
			   			   						   .setText("DPI setting selected");
	}
	
	void updateListBox() {
	    this.unplacedShapesBox.clear();
	    if(this.printDialogInstance.printInstancesNotEmpty())
	    {
	    	ArrayList<Shape> unplacedShapes = this.printDialogInstance.getUnplacedShapes();
	    	for(int i = 0; i < unplacedShapes.size(); i++)
	    	{
	    		this.unplacedShapesBox.addItem(unplacedShapes.get(i).getGShape().getName(), i);
	    	}
	    	this.unplacedShapesBox.updateListBoxItems();
	    }
	}
	
	void createButtons()
	  {
		if(this.printDialogInstance.printInstancesNotEmpty()) {
			ArrayList<PrintInstance> printInstances = this.printDialogInstance.getPrintInstances();
		    int buttonNumber = 0;
		    for(int i = 0; i < instanceButtons.size(); i++)
		    {
		    	this.printDialogWindowController.remove(instanceButtons.get(i));
		      this.instanceButtons.get(i).remove();
		    }
		    this.instanceButtons = new ArrayList<Button>();
		    for(int i = 0; i < printInstances.size(); i++)
		    {
		      for(int j = 0; j < printInstances.get(i).getNumberOfSubInstances(); j++)
		      {
		       int row = (buttonNumber/5);
		       int collumn = (buttonNumber%5);
		       int name = j+1;
		       Button newButton = printDialogWindowController.addButton(printInstances.get(i).getMaterial().getMaterialName() + " - " + name)
		                             						 .setPosition((collumn*160),(row*50))
		                             						 .setSize(150,30)
		                             						 .setId(buttonNumber)
		                             						 .setColorBackground(color(128,128,128))
		                             						 .setColorForeground(color(0,0,0))
		                             						 .setColorCaptionLabel(color(255,255,255))
		                             						 .setGroup(instanceGroup); 
		       this.instanceButtons.add(newButton); 
		       buttonNumber++;
		      }
		    }
		    setActiveButton();
		}
	  }
	
	/**
	  * This method updates the list of laser cutters in the dropdown box for 
	  * selecting the laser cutter to be used. 
	  */
	void updateCutters() {
		this.cutterBox.clear();
		boolean done = false;
		int counter = 0;
		while(!done)
		{
			LaserCutter cutter = new LaserCutter();
			cutter.setDevice(counter);
			if(!cutter.returnDevice().equals("no selected")) {
				this.cutterBox.addItem(cutter.returnDevice(), counter);
			} else {
				done = true;
			}
			counter++;
		}
	}
	
	double[] updateDPIBox() {
		  this.dpiBox.clear();
		  double[] dpi = this.printDialogInstance.getLaserCutterSettings().getSelectedCutter().returnDPI();
		  for(int i = 0; i < dpi.length; i++) {
			  dpiBox.addItem(Double.toString(dpi[i]), i);
		  }
		  return dpi;
	  }
	
	void activateAll(boolean state)
	  {
		  this.lasercutButton.setVisible(state);
		  this.exportSVGButton.setVisible(state);
		  this.addExtraJobButton.setVisible(state);
	  }
	
	 void setActiveButton()
	  {
		  this.instanceButtons.get(this.printDialogInstance.activeButtonNumber()).setColorBackground(color(0,0,0));
	  }
	 
	 void setButtonUnactive() {
		 this.instanceButtons.get(this.printDialogInstance.activeButtonNumber()).setColorBackground(color(128,128,128));
	 }
	 
	 double[] showDPIBox() {
		 this.dpiBox.setVisible(true);
	      this.dpiSelected.setVisible(true);
	      this.dpiSelectLabel.setVisible(true);
	      this.dpiSelected.setText(Integer.toString(this.printDialogInstance.getLaserCutterSettings().getDPI()) + " DPI");
	      return updateDPIBox();
	 }
	 
	 void setStatusLabelText(String text) {
		 this.statusLabel.setText(text);
	 }
	 
	 void activateConfirm(boolean activation) {
		 this.confirmPrint.setVisible(activation);
		this.declinePrint.setVisible(activation);
	 }
	 
	 void setDPISelected(String text) {
		 this.dpiSelected.setText(text);
	 }
	 
	 void setCutterSelected(String text) {
		 this.cutterSelected.setText(text);
	 }
	 
	 String getCutterAddress() {
		 return this.cutterAddress.getText();
	 }
}


