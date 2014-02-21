package de.mcp.cutcad.application;

import geomerative.RG;

import javax.swing.JOptionPane;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;
import processing.opengl.*;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import de.mcp.cutcad.model.AllMaterials;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.view.DrawingView2D;
import de.mcp.cutcad.view.DrawingView3D;
import de.mcp.cutcad.view.DrawingViewFrame;
import de.mcp.cutcad.view.Transformation;

public class CutCADApplet extends PApplet implements ToolbarDelegate {

	private static final long serialVersionUID = 6945013714741954254L;
	
	int viewSizeX;
	int viewSizeY;
	
	ControlP5 cp5;
	Toolbar toolbar;
	public Properties properties;
	Statusbar statusbar;
	
	ObjectContainer container = new ObjectContainer();
	private Tool selectedTool;

	public Transformation transform2D = new Transformation((float) 1.0,
			new Vector2D(-50, -50));
	Transformation transform3D = new Transformation((float) 1.0,
			new Vector2D(0, 0));

	Tool tools[];

	public DrawingView2D drawingView2D;
	public DrawingView3D drawingView3D;
	
	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	public void setup() {
		size(displayWidth, displayHeight, P3D);
		ortho();
		
		int toolbarWidth = Toolbar.DEFAULT_TOOLBAR_WIDTH;
		
		viewSizeX = (displayWidth - toolbarWidth - 30) / 2;
		viewSizeY = (displayHeight - 50 - 30);
		
		setup2DDrawingView(toolbarWidth, 50, viewSizeX, viewSizeY);
		
		int view3DPosX = toolbarWidth + viewSizeX + 15;
		int view3DPosY = 50;
		
		setup3DDrawingView(view3DPosX, view3DPosY, viewSizeX, viewSizeY);
		
		new AllMaterials().addMaterialsFromFile(sketchPath("") + "/materials");

		cp5 = new ControlP5(this);
		createProperties();
		statusbar = new Statusbar();
		toolbar = Toolbar.createDefaultToolbar(this);
	}
	
	private void setup2DDrawingView(int originX, int originY, int width, int heigth) {
		
		DrawingViewFrame theFrame = new DrawingViewFrame();
		theFrame.origin = new Vector2D(originX, originY);
		theFrame.size = new Vector2D(width, heigth);
		
		PGraphics view2D = createGraphics(width, heigth, P3D);
		drawingView2D = new DrawingView2D(view2D, theFrame, transform2D, this);
	}
	
	private void setup3DDrawingView(int originX, int originY, int width, int height) {

		RG.init(this);
		RG.ignoreStyles(false);
		RG.setPolygonizer(RG.ADAPTATIVE);

		DrawingViewFrame theFrame = new DrawingViewFrame();
		theFrame.origin = new Vector2D(originX, originY);
		theFrame.size = new Vector2D(width, height);
		
		PGraphics3D view3D = (PGraphics3D) createGraphics(width, height, P3D);
		drawingView3D = new DrawingView3D(view3D, theFrame, transform3D, this);
	}

/* (non-Javadoc)
 * @see processing.core.PApplet#draw()
 */
	public void draw() {
		background(255);
		fill(0);

		drawingView2D.draw(container);
		drawingView3D.draw(container);
		
		properties.drawProperties(this);
		statusbar.drawStatusbar(this);
	}

	  private void createProperties()
	  {
	    properties = new Properties(cp5, 0, 0, width, 50);
	    properties.hide();
	  }
	  
	 /**
	 * 
	 * Sets the status of the statusbar to status.
	 * 
	 * @param status the message that should be displayed on the statusbar
	 */
	  public void displayStatus(String status) {
		  this.statusbar.setStatus(status);
	  }
	  
	  /**
	  * Sets the mouse position to be displayed on the statusbar
	  * @param position the mouse position to be displayed on the statusbar
	  */
	  public void displayMousePosition(Vector2D position) {
		  this.statusbar.setMousePosition(position);
	  }
	  
	  
	  /**
	  * Responds to ControlEvents from controlP5
	  * 
	  * @param theEvent the ControlEvent that has been passed by controlP5
	  */
	  public void controlEvent(ControlEvent theEvent)
	  {
		  if (theEvent.isGroup() && theEvent.getGroup().getName() == "setMaterial")
		  {
			  properties.changeMaterial(theEvent.getGroup().getValue());
		  }
	  }
	  
	  public void toolWasSelected(Tool theTool) {
		  setSelectedTool(theTool);
	  }
	  
	  void setSelectedTool(Tool theTool) {
		  
		  theTool.toolWasSelected();
		  
		  if(theTool.canStaySelected()) {
			  
			  if(this.selectedTool != null) {
				  this.selectedTool.toolWasUnselected();
			  }
		
			  this.selectedTool = theTool;  
		  }
	  }
	  
	  public Tool getSelectedTool() {
		  return this.selectedTool;
	  }

	/* (non-Javadoc)
	 * @see processing.core.PApplet#mousePressed()
	 */
	public void mousePressed()
	  {   
	      Vector2D mousePosition = new Vector2D(mouseX, mouseY);
	      getSelectedTool().mouseButtonPressed(mousePosition, mouseButton);
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseDragged()
	 */
	public void mouseDragged()
	  {
	    getSelectedTool().mouseMoved(new Vector2D(mouseX, mouseY));
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseReleased()
	 */
	public void mouseReleased()
	  {
	    getSelectedTool().mouseButtonReleased(new Vector2D(mouseX, mouseY), mouseButton);

	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseMoved()
	 */
	public void mouseMoved() 
	  {
		Vector2D mousePosition = new Vector2D(mouseX, mouseY);
	      getSelectedTool().mouseMoved(mousePosition);
	      drawingView3D.setInteractionEnabled(drawingView3D.mouseOver(mousePosition));
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#keyPressed()
	 */
	public void keyPressed()
	  {
	    if (key == '+')
	    {
	    	if (drawingView2D.mouseOver(new Vector2D(mouseX, mouseY)))
			  {
				  transform2D.scaleUp(0.01f);
			  }
	    }
	    if (key == '-')
	    {
	    	if (drawingView2D.mouseOver(new Vector2D(mouseX, mouseY)))
			  {
				  transform2D.scaleDown(0.01f);
			  }
	    }
	    if (keyCode == ESC) {
	    	if (container.hasUnsavedChanges()) {
	    		int response = JOptionPane.showConfirmDialog(null, "You have unsaved changes. Quit anyways?");
	    		if (response == JOptionPane.CANCEL_OPTION || response == JOptionPane.NO_OPTION) {
	    			key = 0;  			
	    		}
	    	}
	    	
	    }
	  }
	
	
	  
	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseWheel(processing.event.MouseEvent)
	 */
	@SuppressWarnings("deprecation")
	public void mouseWheel(MouseEvent event)
	  {
		if (drawingView2D.mouseOver(new Vector2D(mouseX, mouseY)))
		  {
			  transform2D.scaleUp((float) (0.01 * -event.getAmount()));
		  }
	  }
	
	public static void main(String args[]) {
		    PApplet.main(new String[] { /*"--present", */"de.mcp.cutcad.application.CutCADApplet" });
		  }
	  
	  /* (non-Javadoc)
	 * @see processing.core.PApplet#sketchFullScreen()
	 */
	public boolean sketchFullScreen() {
			  return true;
		  }
}
