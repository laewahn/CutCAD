package de.mcp.customizer.application;

import geomerative.RG;

import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.MouseEvent;
import processing.opengl.*;

import remixlab.proscene.Scene;

import toxi.geom.mesh.*;
import toxi.processing.ToxiclibsSupport;

import controlP5.ControlEvent;
import controlP5.ControlP5;

import de.mcp.customizer.application.tools.drawing.*;
import de.mcp.customizer.application.tools.fileManagement.*;
import de.mcp.customizer.application.tools.objectImport.*;
import de.mcp.customizer.application.tools.objectManipulation.*;

import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;

import de.mcp.customizer.view.DrawingView2D;
import de.mcp.customizer.view.DrawingViewFrame;
import de.mcp.customizer.view.Transformation;

public class MCPCustomizer extends PApplet {

	private static final long serialVersionUID = 6945013714741954254L;
	Toolbar toolbar;

	public Properties properties;

	Statusbar statusbar;
	ControlP5 cp5;
	
	Scene scene;

	ToxiclibsSupport gfx;
	PGraphics view2D, view3D;

	ObjectContainer container = new ObjectContainer();

	TriangleMesh mesh;

	int startX = 0;
	int startY = 0;

	int viewSizeX;
	int viewSizeY;

	int view2DPosX;
	int view2DPosY;

	int view3DPosX;
	int view3DPosY;

	public Transformation transform2D = new Transformation((float) 1.0,
			new Vector2D(-50, -50));
	Transformation transform3D = new Transformation((float) 1.0,
			new Vector2D(0, 0));

	Grid grid3D, grid2D;

	Tool tools[];

	public DrawingView2D drawingView2D;
	
	public STLMesh meshSTL;

	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	public void setup() {
		size(displayWidth, displayHeight, P3D);
		ortho();

		viewSizeX = (displayWidth - 50 - 30) / 2;
		viewSizeY = (displayHeight - 50 - 30);
		view2DPosX = 50;
		view2DPosY = 50;
		view3DPosX = view2DPosX + viewSizeX + 15;
		view3DPosY = 50;

		view2D = createGraphics(viewSizeX, viewSizeY, P3D);
		view3D = createGraphics(viewSizeX, viewSizeY, P3D);

		grid2D = new Grid(transform2D, view2D);
		grid3D = new Grid(transform3D, view3D);

		DrawingViewFrame theFrame = new DrawingViewFrame();
		theFrame.origin = new Vector2D(view2DPosX, view2DPosY);
		theFrame.size = new Vector2D(viewSizeX, viewSizeY);
		drawingView2D = new DrawingView2D(view2D, theFrame, grid2D, transform2D, this);
		
		gfx = new ToxiclibsSupport(this, view3D);

		RG.init(this);
		RG.ignoreStyles(false);

		RG.setPolygonizer(RG.ADAPTATIVE);

		meshSTL = new STLMesh();

		new AllMaterials().addMaterialsFromFile(sketchPath("") + "/materials");
		AllMaterials.setBaseMaterial(AllMaterials.getMaterials().get(40));

		cp5 = new ControlP5(this);

		createProperties();
		statusbar = new Statusbar();
		createToolbar();

		scene = new Scene((PApplet)this, (PGraphics3D)view3D);
		scene.disableKeyboardHandling();
		scene.disableMouseHandling();
		scene.setGridIsDrawn(false);
		scene.setAxisIsDrawn(false);
		scene.setRadius(10000.0f);
		scene.camera().setPosition(new PVector(500, 550, 1500));
	}

/* (non-Javadoc)
 * @see processing.core.PApplet#draw()
 */
	public void draw() {
		background(255);
		fill(0);

		drawingView2D.draw(container.allDrawables());
		
		draw3DView();
		
		properties.drawProperties(this);
		statusbar.drawStatusbar(this);
	}

	private void draw3DView()
	  {
	    view3D.beginDraw();
	    
	    scene.beginDraw();

	    view3D.background(150);
  
	    draw3DAxes(view3D);
	    grid3D.draw2D(view3D, transform3D);;
	    
	    for (Shape s : container.allShapes())
	    {
	      s.getGShape().draw3D(view3D, transform3D);
	    }

	    if(meshSTL.isStlImported())
	    {
	    	if(meshSTL.isPosChanged())
	    	{
	    		meshSTL.center();
	    	}
	    	if(meshSTL.isRotChanged())
	    	{
	    		meshSTL.rotate();
	    	}
	    	gfx.mesh(meshSTL.getSTLMesh());
	    }
	    
	    scene.endDraw();
	    view3D.endDraw(); 
	    
	    image(view3D, view3DPosX, view3DPosY);
	  }

	private void draw3DAxes(PGraphics p) {
		p.strokeWeight(10);
		p.stroke(color(255, 100, 100));
		p.line(0, 0, 0, 1000, 0, 0);
		p.stroke(color(100, 255, 100));
		p.line(0, 0, 0, 0, 1000, 0);
		p.stroke(color(100, 100, 255));
		p.line(0, 0, 0, 0, 0, 1000);
		p.stroke(color(0, 0, 0));
		p.strokeWeight(1);
	}

	  private void createToolbar()
	  {
	    toolbar = new Toolbar(this.cp5);

	    toolbar.setPosition(0, 50).setSize(50, 900).setItemHeight(50).disableCollapse().hideBar();
	    
	    tools = new Tool[]{
	    	  new NewProjectTool(this, container),
	    	  new LoadTool(this, container),
	    	  new SaveTool(this, container),
	  	      new SelectTool(this, container),
	  	      new RectangleTool(this, container),
	  	      new SymmetricPolygonTool(this, container),
	  	      new TrapeziumTool(this, container),
	  	      new PolygonTool(this, container),
	  	      new ConnectTool(this, container),
	  	      new DeleteTool(this, container),
	  	      new CutoutTool(this, container),
	  	      new CopyTool(this, container),
	  	      new ImportSVGTool(this, container),
	  	      new ImportSTLTool(this, container),
	  	      new ChangeSTLTool(this, container),
	  	      new PrintTool(this, container)
	  	    };
	    
	    toolbar.addTools(Arrays.asList(tools));
	    toolbar.setSelectedTool(tools[3]);
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

	/* (non-Javadoc)
	 * @see processing.core.PApplet#mousePressed()
	 */
	public void mousePressed()
	  {   
	      if (mouseOver3DView())
	      {
	          startX = mouseX - view3DPosX;
	          startY = mouseY - view3DPosY;
	      }
	      
	      Vector2D mousePosition = new Vector2D(mouseX, mouseY);
	      toolbar.getSelectedTool().mouseButtonPressed(mousePosition, mouseButton);
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseDragged()
	 */
	public void mouseDragged()
	  {
	    toolbar.getSelectedTool().mouseMoved(new Vector2D(mouseX, mouseY));
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseReleased()
	 */
	public void mouseReleased()
	  {
	    toolbar.getSelectedTool().mouseButtonReleased(new Vector2D(mouseX, mouseY), mouseButton);

	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseMoved()
	 */
	public void mouseMoved() 
	  {
	      toolbar.getSelectedTool().mouseMoved(new Vector2D(mouseX, mouseY));
	      if (mouseOver3DView())
	      {
	    	  scene.enableMouseHandling();
	      }
	      else
	      {
	    	  scene.disableMouseHandling();
	      }
	  }

	  private boolean mouseOver2DView()
	  {
	    return mouseX > view2DPosX && mouseX <= view2DPosX + viewSizeX && mouseY > view2DPosY && mouseY <= view2DPosY + viewSizeY;
	  }

	  private boolean mouseOver3DView()
	  {
	    return mouseX > view3DPosX && mouseX <= view3DPosX + viewSizeX && mouseY > view3DPosY && mouseY <= view3DPosY + viewSizeY;
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#keyPressed()
	 */
	public void keyPressed()
	  {
	    if (key == '+')
	    {
	    	if (mouseOver2DView())
			  {
				  transform2D.scaleUp(0.01f);
			  }
	    }
	    if (key == '-')
	    {
	    	if (mouseOver2DView())
			  {
				  transform2D.scaleDown(0.01f);
			  }
	    }
	  }
	  
	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseWheel(processing.event.MouseEvent)
	 */
	@SuppressWarnings("deprecation")
	public void mouseWheel(MouseEvent event)
	  {
		  if (mouseOver2DView())
		  {
			  transform2D.scaleUp((float) (0.01 * -event.getAmount()));
		  }
	  }
	
	public static void main(String args[]) {
		    PApplet.main(new String[] { /*"--present", */"de.mcp.customizer.application.MCPCustomizer" });
		  }
	  
	  /* (non-Javadoc)
	 * @see processing.core.PApplet#sketchFullScreen()
	 */
	public boolean sketchFullScreen() {
			  return true;
		  }
}
