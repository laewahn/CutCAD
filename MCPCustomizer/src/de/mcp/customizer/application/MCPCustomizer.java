package de.mcp.customizer.application;

import geomerative.RG;

import java.util.Arrays;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;
import toxi.geom.Rect;
//import toxi.geom.Vector2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.*;
import toxi.processing.ToxiclibsSupport;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import de.mcp.customizer.application.tools.*;
import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;

class CustomizerFrame {
	public Vector2D origin;
	public Vector2D size;
	
	public boolean containsPoint(Vector2D point) {
		Rect frameRect = new Rect(this.origin.getVec2D(), this.origin.add(size).getVec2D());
		return frameRect.containsPoint(point.getVec2D());
	}
}

class CustomizerView {
	
	private MCPCustomizer application;
	private PGraphics context;
	private CustomizerFrame frame;
	
	private Transformation transform;
	private Grid grid;
	private Drawable2D axes = new Axes2D();
	
	public CustomizerView(PGraphics context, CustomizerFrame frame, Grid grid, Transformation transform, MCPCustomizer application) {
		this.context = context;
		this.frame = frame;
		this.grid = grid;
		
		this.transform = transform;
		this.application = application;
	}
	
	public void applyTransformation(Transformation transform) {
		context.scale(transform.getScale());
        context.translate(-transform.getTranslation().x(), -transform.getTranslation().y());
	}
	
	public void draw(List<Drawable2D> drawables) {
		context.beginDraw();
		context.background(150);
		
		applyTransformation(this.transform);

		drawables.add(axes);
		drawables.add((Drawable2D) grid);
		
		for(Drawable2D d : drawables) {
			d.draw2D(context, this.transform);
		}

		context.endDraw();

		application.image(context, frame.origin.x(), frame.origin.y());
	}
	
	public Vector2D getOrigin() {
		return this.frame.origin;
	}
	
	public boolean containsPoint(Vector2D point) {
		return this.frame.containsPoint(point);
	}
	
	public Transformation getTransformation() {
		return this.transform;
	}
	
	private class Axes2D implements Drawable2D {
		@Override
		public void draw2D(PGraphics context, Transformation transform) {
			context.strokeWeight(2);
			context.textSize(32);
			context.fill(context.color(255, 0, 0));
			context.stroke(context.color(255, 0, 0));
			context.line(0, 0, 350, 0);
			context.text("X", 350, 12);
			context.fill(context.color(0, 255, 0));
			context.stroke(context.color(0, 255, 0));
			context.line(0, 0, 0, 350);
			context.text("Y", -10, 385);
			context.stroke(context.color(0, 0, 0));
			context.strokeWeight(1);
		}
	}

}



public class MCPCustomizer extends PApplet {

	
	private static final long serialVersionUID = 6945013714741954254L;
	Toolbar toolbar;

	public Properties properties;

	Statusbar statusbar;
	ControlP5 cp5;

	ToxiclibsSupport gfx;
	PGraphics view2D, view3D;

	ObjectContainer container = new ObjectContainer();

	TriangleMesh mesh;

	int startX = 0;
	int startY = 0;

	int gridWidth = 50; // 5 mm

	int viewSizeX;
	int viewSizeY;

	int view2DPosX;
	int view2DPosY;
	Rect view2DRect;

	int view3DPosX;
	int view3DPosY;

	int cameraX = 45;
	int cameraY = 1000;

	public Transformation transform2D = new Transformation((float) 1.0,
			new Vector2D(0, 0));
	Transformation transform3D = new Transformation((float) 1.0,
			new Vector2D(0, 0));

	Grid grid3D, grid2D;

	Vec3D cameraPosition;
	Tool tools[];

	public CustomizerView customizerView2D;
	
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
		
		view2DRect = new Rect(view2DPosX, view2DPosY, viewSizeX, viewSizeY);

		view2D = createGraphics(viewSizeX, viewSizeY, P3D);
		view3D = createGraphics(viewSizeX, viewSizeY, P3D);

		grid2D = new Grid(transform2D, view2D);
		grid3D = new Grid(transform3D, view3D);

		CustomizerFrame theFrame = new CustomizerFrame();
		theFrame.origin = new Vector2D(view2DPosX, view2DPosY);
		theFrame.size = new Vector2D(viewSizeX, viewSizeY);
		customizerView2D = new CustomizerView(view2D, theFrame, grid2D, transform2D, this);
		
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

		cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY)
				.getRotatedAroundAxis(new Vec3D((float) 0.0, (float) 0.0,
						(float) 1.0), radians(cameraX));
	}

/* (non-Javadoc)
 * @see processing.core.PApplet#draw()
 */
	public void draw() {
		background(255);
		fill(0);

		customizerView2D.draw(container.allDrawables());
		
		draw3DView();
		
		properties.drawProperties(this);
		statusbar.drawStatusbar(this);
	}

	private void draw3DView()
	  {
	    view3D.beginDraw();

	    view3D.ortho();
	    view3D.beginCamera();
	    view3D.camera(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), (float)0.0, (float)0.0, (float)0.0, (float)0.0, (float)0.0, (float)-1.0);
	    view3D.translate(-viewSizeX/2, -viewSizeY/2);
	    view3D.endCamera();

	    view3D.background(150);
	    
	    //float scale = transform3D.getScale();	
	    //view3D.scale(scale);
  
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
	    
	    view3D.endDraw(); 
	    
	    image(view3D, view3DPosX, view3DPosY);
	  }

	private void draw3DAxes(PGraphics p) {
		p.strokeWeight(2);
		p.textSize(32);
		p.fill(color(255, 0, 0));
		p.stroke(color(255, 0, 0));
		p.line(0, 0, 0, 350, 0, 0);
		p.text("X", 350, 12, 0);
		p.fill(color(0, 255, 0));
		p.stroke(color(0, 255, 0));
		p.line(0, 0, 0, 0, 350, 0);
		p.text("Y", -10, 385, 0);
		p.fill(color(0, 0, 255));
		p.stroke(color(0, 0, 255));
		p.line(0, 0, 0, 0, 0, 350);
		p.text("Z", 0, 0, 350);
		p.stroke(color(0, 0, 0));
		p.strokeWeight(1);
	}

	  private void createToolbar()
	  {
	    toolbar = new Toolbar(this.cp5);

	    toolbar.setPosition(0, 50).setSize(50, 700).setItemHeight(50).disableCollapse().hideBar();
	    
	    tools = new Tool[]{
	  	      new SelectTool(this, container),
	  	      new DrawTool(this, container),
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
	    toolbar.setSelectedTool(tools[0]);
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
	    if (mouseOver3DView()) {
	    	if (mouseButton == PConstants.LEFT)
	    	{
	    		cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY + 5 * (mouseY - view3DPosY - startY)).getRotatedAroundAxis(new Vec3D((float)0.0, (float)0.0, (float)1.0), radians(cameraX + mouseX - view3DPosX - startX));
	    	}	    	
	    	else if (mouseButton == PConstants.RIGHT)
	    	{
	    		// do nothing... later: translate 3D view. This is problematic due to the way the camera is handled.
	    	}
	    }

	    toolbar.getSelectedTool().mouseMoved(new Vector2D(mouseX, mouseY));
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseReleased()
	 */
	public void mouseReleased()
	  {
	    toolbar.getSelectedTool().mouseButtonReleased(new Vector2D(mouseX, mouseY), mouseButton);

	    if (mouseOver3DView() && mouseButton == PConstants.LEFT)
	    {
	      cameraX += mouseX - view3DPosX - startX;
	      cameraY += 5 * (mouseY - view3DPosY - startY);
	    }
	  }

	  /* (non-Javadoc)
	 * @see processing.core.PApplet#mouseMoved()
	 */
	public void mouseMoved() 
	  {
	      toolbar.getSelectedTool().mouseMoved(new Vector2D(mouseX, mouseY));
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
			  if (mouseOver3DView())
			  {
				  transform3D.scaleUp(0.01f);
			  }
	    }
	    if (key == '-')
	    {
	    	if (mouseOver2DView())
			  {
				  transform2D.scaleDown(0.01f);
			  }
			  if (mouseOver3DView())
			  {
				  transform3D.scaleDown(0.01f);
			  }
	    }
	    if (key == 's') {
	    	container.safe("foo");
	    }
	    
	    if (key == 'n') {
	    	container.clear();
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
		  if (mouseOver3DView())
		  {
			  transform3D.scaleUp((float) (0.01 * -event.getAmount()));
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
