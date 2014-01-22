package de.mcp.customizer.application;

import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.*;
import toxi.processing.ToxiclibsSupport;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import de.mcp.customizer.application.tools.*;
import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;

public class MCPCustomizer extends PApplet {

	private static final long serialVersionUID = 6945013714741954254L;
	Toolbar toolbar;
	  Properties properties;
	  Statusbar statusbar;
	  ControlP5 cp5;

	  ToxiclibsSupport gfx;
	  PGraphics view2D, view3D;
	  ArrayList<Shape> shapes;
	  ArrayList<Connection> connections;
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

	  Transformation transform2D = new Transformation((float) 1.0, new Vec2D(0,0));
	  Transformation transform3D = new Transformation((float) 1.0, new Vec2D(0,0));

	  Vec3D cameraPosition;
	  Tool tools[];
	  
	  TriangleMesh meshSTL;

	  public void setup()
	  {
	    size(displayWidth, displayHeight, P3D);
	    ortho();
	    
	    viewSizeX = (displayWidth-50-30)/2;
	    viewSizeY = (displayHeight-50-30);
	    view2DPosX = 50;
	    view2DPosY = 50;
	    view3DPosX = view2DPosX + viewSizeX + 15;
	    view3DPosY = 50;
	    view2DRect = new Rect(view2DPosX, view2DPosY, viewSizeX, viewSizeY);

	    view2D = createGraphics(viewSizeX, viewSizeY, P3D);
	    view3D = createGraphics(viewSizeX, viewSizeY, P3D);
	    
	    gfx = new ToxiclibsSupport(this, view3D);
	    //gfx.setGraphics(view3D);

	    shapes = new ArrayList<Shape>();
	    connections = new ArrayList<Connection>();

	    new AllMaterials().addMaterialsFromFile(sketchPath("") + "/materials");
	    AllMaterials.setBaseMaterial(AllMaterials.getMaterials().get(48));

	    cp5 = new ControlP5(this);

	    createProperties();
	    statusbar = new Statusbar();
	    createToolbar();

	    cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY).getRotatedAroundAxis(new Vec3D((float)0.0, (float)0.0, (float)1.0), radians(cameraX));
	  }

	  public void draw()
	  {
	    background(255);
	    fill(0);

	    draw2DView();
	    draw3DView();
	    properties.drawProperties(this);
	    statusbar.drawStatusbar(this);
	  }

	  void draw2DView()
	  {
	    view2D.beginDraw();
	    transform2D.transform(view2D);

	    view2D.background(150);
	    
	    draw2DAxes(view2D);
	    drawGrid(view2D);

	    for (Shape s : shapes)
	    {
	      s.getShape().draw2D(view2D);
	    }
	    
	    for (Connection c : connections)
	    {
	      c.draw2D(view2D);
	    }
	    
	    for (Cutout c : Cutout.getAllCutouts())
	    {
	      c.draw2D(view2D);
	    }

	    this.toolbar.getSelectedTool().draw2D(view2D);

	    view2D.endDraw();

	    image(view2D, view2DPosX, view2DPosY);
	  }
	  
	private void drawGrid(PGraphics p) {
		for (int i = -100; i < 100; i++)
	    {
	    	p.strokeWeight(1);
	    	p.stroke(220);
	    	p.line(-100 * gridWidth, gridWidth * i, 100 * gridWidth, gridWidth * i);
	    	p.line(gridWidth * i, -100 * gridWidth, gridWidth * i, 100 * gridWidth);
	    }
	}

	  void draw3DView()
	  {
	    view3D.beginDraw();

	    view3D.ortho();
	    view3D.beginCamera();
	    view3D.camera(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), (float)0.0, (float)0.0, (float)0.0, (float)0.0, (float)0.0, (float)-1.0);
	    view3D.translate(-viewSizeX/2, -viewSizeY/2);
	    view3D.endCamera();

	    view3D.background(150);
	    
	    float scale = transform3D.getScale();	
	    view3D.scale(scale);
    
	    draw3DAxes(view3D);
	    drawGrid(view3D);
	    
	    for (Shape s : shapes)
	    {
	      s.getShape().draw3D(view3D);
	    }

	    if(meshSTL != null)
	    {
	    	//gfx.origin(new Vec3D(),200);
	    	meshSTL.center(new Vec3D(0,0,0));
	    	gfx.mesh(meshSTL);
	    }
	    
	    view3D.endDraw(); 
	    
	    image(view3D, view3DPosX, view3DPosY);
	  }

	private void draw3DAxes(PGraphics p) {
		p.strokeWeight(2);	   
		p.textSize(32);
		p.fill(color(255,0,0));
	    p.stroke(color(255,0,0));	    
	    p.line(0, 0, 0, 350, 0, 0);
	    p.text("X", 350, 12, 0);
		p.fill(color(0,255,0));
	    p.stroke(color(0,255,0));	   
	    p.line(0, 0, 0, 0, 350, 0);
	    p.text("Y", -10, 385, 0);
		p.fill(color(0,0,255));
	    p.stroke(color(0,0,255));	   
	    p.line(0, 0, 0, 0, 0, 350);
	    p.text("Z", 0, 0, 350);
	    p.stroke(color(0,0,0));	 
	    p.strokeWeight(1);
	}

	private void draw2DAxes(PGraphics p) {
		p.strokeWeight(2);	   
		p.textSize(32);
		p.fill(color(255,0,0));
	    p.stroke(color(255,0,0));	    
	    p.line(0, 0, 350, 0);
	    p.text("X", 350, 12);
		p.fill(color(0,255,0));
	    p.stroke(color(0,255,0));	   
	    p.line(0, 0, 0, 350);
	    p.text("Y", -10, 385);
	    p.stroke(color(0,0,0));	 
	    p.strokeWeight(1);
	}

	  void createToolbar()
	  {
	    toolbar = new Toolbar(cp5, this);

	    toolbar.setPosition(0, 50).setSize(50, 650).setItemHeight(50).disableCollapse().hideBar();

	    tools = new Tool[]{
	      new SelectTool(view2DRect, properties, statusbar, shapes, connections, transform2D),
	      new DrawTool(view2DRect, properties, statusbar, shapes, transform2D),
	      new SymmetricPolygonTool(view2DRect, properties, statusbar, shapes, transform2D),
	      new TrapeziumTool(view2DRect, properties, statusbar, shapes, transform2D),
	      new PolygonTool(view2DRect, properties, statusbar, transform2D, shapes),
	      new ConnectTool(view2DRect, properties, statusbar, shapes, connections, transform2D),
	      new DeleteTool(view2DRect, properties, statusbar, shapes, connections, transform2D),
	      new CutoutTool(view2DRect, properties, statusbar, shapes, connections, transform2D),
	      new CopyTool(view2DRect, properties, statusbar, shapes, transform2D),
	      new ImportSVGTool(view2DRect, properties, statusbar, shapes, transform2D),
	      new ImportSTLTool(view2DRect, properties, statusbar, this, transform2D),
	      new PrintTool(view2DRect, properties, statusbar, transform2D, shapes)
	    };
	    
	    toolbar.addTools(Arrays.asList(tools));
	    toolbar.setSelectedTool(tools[0]);
	  }

	  void createProperties()
	  {
	    properties = new Properties(cp5, 0, 0, width, 50);
	    properties.hide();
	  }
	  
	  public void controlEvent(ControlEvent theEvent)
	  {
		  if (theEvent.isGroup() && theEvent.getGroup().getName() == "setMaterial")
		  {
			  properties.changeMaterial(theEvent.getGroup().getValue());
		  }
	  } 

	  public void mousePressed()
	  {   
	      if (mouseOver3DView())
	      {
	          startX = mouseX - view3DPosX;
	          startY = mouseY - view3DPosY;
	      }
	      
	      Vec2D mousePosition = new Vec2D(mouseX, mouseY);
	      toolbar.getSelectedTool().mouseButtonPressed(mousePosition, mouseButton);
	  }

	  public void mouseDragged()
	  {
	    if (mouseOver3DView()) {
	    	if (mouseButton == PConstants.LEFT)
	    	{
	    		cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY + 5 * (mouseY - view3DPosY - startY)).getRotatedAroundAxis(new Vec3D((float)0.0, (float)0.0, (float)1.0), radians(cameraX + mouseX - view3DPosX - startX));
	    	}	    	
	    	else if (mouseButton == PConstants.RIGHT)
	    	{
	    		transform3D.scaleUp(0.001f * (mouseY - view3DPosY - startY));
	    		startY = mouseY - view3DPosY;
	    	}
	    }

	    toolbar.getSelectedTool().mouseMoved(new Vec2D(mouseX, mouseY));
	  }

	  public void mouseReleased()
	  {
	    toolbar.getSelectedTool().mouseButtonReleased(new Vec2D(mouseX, mouseY), mouseButton);

	    if (mouseOver3DView() && mouseButton == PConstants.LEFT)
	    {
	      cameraX += mouseX - view3DPosX - startX;
	      cameraY += 5 * (mouseY - view3DPosY - startY);
	    }
	  }

	  public void mouseMoved() 
	  {
	      toolbar.getSelectedTool().mouseMoved(new Vec2D(mouseX, mouseY));
	  }

	  boolean mouseOver3DView()
	  {
	    return mouseX > view3DPosX && mouseX <= view3DPosX + viewSizeX && mouseY > view3DPosY && mouseY <= view3DPosY + viewSizeY;
	  }

	  public void keyPressed()
	  {
	    if (key == '+')
	    {
	      transform2D.scaleUp((float) 0.01);
	    }
	    if (key == '-')
	    {
	      transform2D.scaleDown((float) 0.01);
	    }
	  }
	
	  public static void main(String args[]) {
		    PApplet.main(new String[] { /*"--present", */"de.mcp.customizer.application.MCPCustomizer" });
		  }
	  
	  public boolean sketchFullScreen() {
			  return true;
		  }

	  public void setMesh(TriangleMesh stlMesh)
	  {
		  this.meshSTL = stlMesh;
	  }

}
