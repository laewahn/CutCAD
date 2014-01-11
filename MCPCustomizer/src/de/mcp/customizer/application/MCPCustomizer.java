package de.mcp.customizer.application;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;

import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.processing.ToxiclibsSupport;

import controlP5.ControlEvent;
import controlP5.ControlP5;

import de.mcp.customizer.application.tools.ConnectTool;
import de.mcp.customizer.application.tools.CutoutTool;
import de.mcp.customizer.application.tools.DeleteTool;
import de.mcp.customizer.application.tools.DrawTool;
import de.mcp.customizer.application.tools.PrintTool;
import de.mcp.customizer.application.tools.SelectTool;

import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Rectangle;
import de.mcp.customizer.model.Shape;

import de.mcp.customizer.printdialog.PrintDialog;

import de.mcp.customizer.view.Transformation2D;

public class MCPCustomizer extends PApplet {

	private static final long serialVersionUID = 6945013714741954254L;
	Toolbar toolbar;
	  Properties properties;
	  ControlP5 cp5;

	  ToxiclibsSupport gfx;
	  PGraphics view2D, view3D;
	  ArrayList<Shape> shapes;
	  ArrayList<Connection> connections;

	  int startX = 0;
	  int startY = 0;
	  int endX = 0;
	  int endY = 0;

	  int viewSizeX = 500;
	  int viewSizeY = 500;
	  int view2DPosX = 150;
	  int view2DPosY = 50;
	  Rect view2DRect = new Rect(view2DPosX, view2DPosY, viewSizeX, viewSizeY);

	  int view3DPosX = 700;
	  int view3DPosY = 50;

	  int cameraX = 45;
	  int cameraY = 1000;

	  Transformation2D transform2D = new Transformation2D((float) 1.0, new Vec2D(0,0));

	  Vec3D cameraPosition;
	  Tool selectedTool;
	  Tool tools[];

	  public void setup()
	  {
	    size(displayWidth, displayHeight, P3D);
	    ortho();

	    view2D = createGraphics(viewSizeX, viewSizeY, P3D);
	    view3D = createGraphics(viewSizeX, viewSizeY, P3D);

	    shapes = new ArrayList<Shape>();
	    connections = new ArrayList<Connection>();

	    new AllMaterials().addMaterialsFromFile(sketchPath("") + "/materials");

	  //Just for testing: add some shapes
	    // shapes.add(new Rectangle(new Vec3D(50, 50, 0), 300, 300));
	    // shapes.add(new Rectangle(new Vec3D(450, 200, 0), 300, 300));
	    shapes.add(new Rectangle(new Vec3D(50, 50, 0), 151, 101));
	    shapes.add(new Rectangle(new Vec3D(50, 200, 0), 151, 101));
	    shapes.add(new Rectangle(new Vec3D(400, 200, 0), 74, 101));
	    shapes.add(new Rectangle(new Vec3D(400, 50, 0), 74, 101));
	    shapes.add(new Rectangle(new Vec3D(250, 250, 0), 74, 151));

	    cp5 = new ControlP5(this);

	    createProperties();
	    createToolbar();

	    cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY).getRotatedAroundAxis(new Vec3D((float)0.0, (float)0.0, (float)1.0), radians(cameraX));
	    selectedTool = new SelectTool(view2DRect, properties, shapes, connections, transform2D);
	  }

	  public void draw()
	  {
	    background(180);
	    fill(0);

	    draw2DView();
	    draw3DView();
	    properties.drawProperties(this);
	  }

	  void draw2DView()
	  {
	    view2D.beginDraw();
	    transform2D.transform(view2D);

	    view2D.background(100);

	    for (Shape s : shapes)
	    {
	      s.getShape().draw2D(view2D);
	    }
	    
	    for (Connection c : connections)
	    {
	      c.drawConnection(view2D);
	    }
	    
	    for (Cutout c : Cutout.getAllCutouts())
	    {
	      c.drawCutout(view2D);
	    }

	    this.selectedTool.draw2D(view2D);

	    view2D.endDraw();

	    image(view2D, view2DPosX, view2DPosY);
	  }

	  void draw3DView()
	  {
	    view3D.beginDraw();

	    view3D.ortho();
	    view3D.beginCamera();
	    view3D.camera(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), (float)0.0, (float)0.0, (float)0.0, (float)0.0, (float)0.0, (float)-1.0);
	    view3D.translate(-viewSizeX/2, -viewSizeY/2);
	    view3D.endCamera();

	    view3D.background(100);
	    for (Shape s : shapes)
	    {
	      s.getShape().draw3D(view3D);
	    }

	    view3D.endDraw();

	    image(view3D, view3DPosX, view3DPosY);
	  }

	  void createToolbar()
	  {
	    toolbar = new Toolbar(cp5, "Toolbar");
	    toolbar.setPosition(0, 50).setSize(150, 500).setItemHeight(50).disableCollapse().hideBar();

	    tools = new Tool[]{
	      new SelectTool(view2DRect, properties, shapes, connections, transform2D),
	      new DrawTool(view2DRect, properties, shapes, transform2D),
	      new ConnectTool(view2DRect, properties, shapes, connections, transform2D),
	      new DeleteTool(view2DRect, properties, shapes, connections, transform2D),
	      new CutoutTool(view2DRect, properties, shapes, connections, transform2D),
	      new PrintTool(view2DRect, properties, transform2D)
	    };

	    for (int i = 0; i < tools.length; i++)
	    {
	      Tool theTool = tools[i];
	      PGraphics toolIcon = theTool.getIcon(createGraphics(150, 50));
	      toolbar.addCustomItem(theTool.getName(), i, new ShapeButton(toolIcon));
	    }
	  }

	  void createProperties()
	  {
	    properties = new Properties(cp5, 0, 0, width, 50);
	    properties.hide();
	  }

	  public void mousePressed()
	  {   
	      if (mouseOver3DView())
	      {
	          startX = mouseX - view3DPosX;
	          startY = mouseY - view3DPosY;
	      }
	      
	      Vec2D mousePosition = new Vec2D(mouseX, mouseY);
	      selectedTool.mouseButtonPressed(mousePosition, mouseButton);
	  }

	  public void mouseDragged()
	  {
	    if (mouseOver3DView()) {
	      cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY + 5 * (mouseY - view3DPosY - startY)).getRotatedAroundAxis(new Vec3D((float)0.0, (float)0.0, (float)1.0), radians(cameraX + mouseX - view3DPosX - startX));
	    }

	    selectedTool.mouseMoved(new Vec2D(mouseX, mouseY));
	  }

	  public void mouseReleased()
	  {
	    selectedTool.mouseButtonReleased(new Vec2D(mouseX, mouseY), mouseButton);

	    if (mouseOver3DView())
	    {
	      cameraX += mouseX - view3DPosX - startX;
	      cameraY += 5 * (mouseY - view3DPosY - startY);
	    }
	  }

	  public void mouseMoved() 
	  {
	      selectedTool.mouseMoved(new Vec2D(mouseX, mouseY));
	  }

	  boolean mouseOver3DView()
	  {
	    return mouseX > view3DPosX && mouseX <= view3DPosX + viewSizeX && mouseY > view3DPosY && mouseY <= view3DPosY + viewSizeY;
	  }

	  public void controlEvent(ControlEvent theEvent) 
	  {
	    // an event from the toolbar
	    if (theEvent.isGroup() && theEvent.isFrom("Toolbar"))
	    {
	      int id = (int)theEvent.group().value();
	      if(id != 5)
	      {
	        selectedTool = tools[id];
	      }
	      
	      if (!selectedTool.getName().equals("SelectTool")){
	        properties.hide();
	      }
	      if (id == 3)
	      {
	          selectedTool = new DeleteTool(view2DRect, properties, shapes, connections, transform2D);
	          properties.hide();        
	      }
	      if (id == 4)
	      {
	          selectedTool = new CutoutTool(view2DRect, properties, shapes, connections, transform2D);
	          properties.hide();        
	      }
	      if(id == 5)
	      {
	        PrintDialog printDialog = new PrintDialog(shapes); 
	      }
	    }
	    else if (theEvent.isGroup() && theEvent.getGroup().getName() == "setMaterial")
	    {
	      properties.changeMaterial(theEvent.getGroup().getValue());
	    }
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


}
