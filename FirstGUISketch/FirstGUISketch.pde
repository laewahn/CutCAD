import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import controlP5.*;

import java.util.*;

Toolbar toolbar;
Properties properties;
ControlP5 cp5;

ToxiclibsSupport gfx;
PGraphics view2D, view3D;
ArrayList<Shapes> shapes;
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

Vec3D cameraPosition;
Tool selectedTool;
Tool tools[] = {
  new SelectTool(view2DRect, properties, shapes),
  new DrawTool(view2DRect, properties, shapes),
  new ConnectTool(view2DRect, properties, shapes, connections)
};

Shapes previewRectangle = new Rectangle(50, 50, 0, 100, 100, 50);

void setup()
{
  size(displayWidth, displayHeight, P3D);
  ortho();

  view2D = createGraphics(viewSizeX, viewSizeY, P3D);
  view3D = createGraphics(viewSizeX, viewSizeY, P3D);

  shapes = new ArrayList<Shapes>();
  connections = new ArrayList<Connection>();

  shapes.add(new Rectangle(50, 50, 0, 300, 300, 30));
  shapes.add(new Rectangle(400, 150, 0, 300, 300, 15));

  cp5 = new ControlP5(this);

  createProperties();
  createToolbar();
  
  cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY).getRotatedAroundAxis(new Vec3D(0.0, 0.0, 1.0), radians(cameraX));
  selectedTool = new SelectTool(view2DRect, properties, shapes);
}

void draw()
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

  view2D.background(100);

  for (Shapes s : shapes)
  {
    s.getShape().draw2D(view2D);
  }
  
  for (Connection c : connections)
  {
    c.drawConnection(view2D);
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
  view3D.camera(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), 0.0, 0.0, 0.0, 0.0, 0.0, -1.0);
  view3D.translate(-viewSizeX/2, -viewSizeY/2);
  view3D.endCamera();

  view3D.background(100);
  for (Shapes s : shapes)
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

  PGraphics cursorIcon = tools[0].getIcon(createGraphics(150, 50));
  toolbar.addCustomItem("Cursor", 0, new ShapeButton(cursorIcon));

  PGraphics rectangleIcon = tools[1].getIcon(createGraphics(150, 50));
  toolbar.addCustomItem("Rectangle", 1, new ShapeButton(rectangleIcon));

  PGraphics connectIcon = tools[2].getIcon(createGraphics(150, 50));
  toolbar.addCustomItem("Connect", 2, new ShapeButton(connectIcon));
}

void createProperties()
{
  properties = new Properties(cp5, 0, 0, width, 50);
  properties.hide();
}

void mousePressed()
{   
    if (mouseOver3DView())
    {
        startX = mouseX - view3DPosX;
        startY = mouseY - view3DPosY;
    }
    
    Vec2D mousePosition = new Vec2D(mouseX, mouseY);
    selectedTool.mouseButtonPressed(mousePosition, mouseButton);
}

void mouseDragged()
{
  if (mouseOver3DView()) {
    cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY + 5 * (mouseY - view3DPosY - startY)).getRotatedAroundAxis(new Vec3D(0.0, 0.0, 1.0), radians(cameraX + mouseX - view3DPosX - startX));
  }

  selectedTool.mouseMoved(new Vec2D(mouseX, mouseY));
}

void mouseReleased()
{
  selectedTool.mouseButtonReleased(new Vec2D(mouseX, mouseY), mouseButton);

  if (mouseOver3DView())
  {
    cameraX += mouseX - view3DPosX - startX;
    cameraY += 5 * (mouseY - view3DPosY - startY);
  }
}

void mouseMoved() 
{
    selectedTool.mouseMoved(new Vec2D(mouseX, mouseY));
}

boolean mouseOver3DView()
{
  return mouseX > view3DPosX && mouseX <= view3DPosX + viewSizeX && mouseY > view3DPosY && mouseY <= view3DPosY + viewSizeY;
}

void controlEvent(ControlEvent theEvent) 
{
  // an event from the toolbar
  if (theEvent.isGroup() && theEvent.isFrom("Toolbar"))
  {
    int id = (int)theEvent.group().value();
    // TODO: Find another method to find out which element of the list sent the event. 
    // One should be able to get the object or at the very least the name instead of an ID

    // For now: 0 is Select, 1 is Rectangle
    if (id == 0)
    {
        selectedTool = new SelectTool(view2DRect, properties, shapes);
    }
    if (id == 1)
    {
        selectedTool = new DrawTool(view2DRect, properties, shapes);
        properties.hide();
    }
    if (id == 2)
    {
        selectedTool = new ConnectTool(view2DRect, properties, shapes, connections);
        properties.hide();
    }
  }
}
