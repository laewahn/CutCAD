import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import com.t_oster.liblasercut.dithering.*;
import com.t_oster.liblasercut.vectoroptimizers.*;
import com.t_oster.liblasercut.utils.*;
import com.t_oster.liblasercut.drivers.*;
import com.t_oster.liblasercut.laserscript.*;
import com.t_oster.liblasercut.platform.*;

import controlP5.*;

import java.util.*;

Toolbar toolbar;
Properties properties;
ControlP5 cp5;

ToxiclibsSupport gfx;
PGraphics view2D, view3D;
ArrayList<Shape> shapes;
ArrayList<Connection> connections;
ArrayList<Material> materials;

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

Transformation2D transform2D = new Transformation2D(1.0, new Vec2D(0,0));

Vec3D cameraPosition;
Tool selectedTool;
Tool tools[];

Shape previewRectangle = new Rectangle(new Vec3D(50,50,0), 100, 100);

void setup()
{
  size(displayWidth, displayHeight, P3D);
  ortho();

  view2D = createGraphics(viewSizeX, viewSizeY, P3D);
  view3D = createGraphics(viewSizeX, viewSizeY, P3D);

  shapes = new ArrayList<Shape>();
  connections = new ArrayList<Connection>();

  materials= new ArrayList<Material>();

  File[] files= new File(sketchPath("") + "/materials").listFiles();
  // Or we use the original visicut files
  // And that's stupid place for it

  for(int i=1; i<files.length; i++) 
  {
    XML material = loadXML(sketchPath("") + "/materials/" + files[i].getName());

    XML identity = material.getChild("identity");
    String      name = identity.getContent();
    
    int     redColor = identity.getInt("red");
    int    blueColor = identity.getInt("green");
    int   greenColor = identity.getInt("blue");
    int alphaChannel = identity.getInt("alpha");
    color materialColor = color(redColor, blueColor, greenColor, alphaChannel);

    XML[] differentThickness = material.getChildren("thickness");

    for (int j=0; j<differentThickness.length; j++)
    {
      int thickness = (int)(differentThickness[j].getInt("value")/10);
      int     power = differentThickness[j].getChild("cut").getChild("power").getIntContent();
      int     speed = differentThickness[j].getChild("cut").getChild("speed").getIntContent();
      int     focus = differentThickness[j].getChild("cut").getChild("focus").getIntContent();
      int frequency = differentThickness[j].getChild("cut").getChild("frequency").getIntContent();

      materials.add(new Material(name, thickness, materialColor, power, speed, focus, frequency));
    }
  }

//Just for testing: add some shapes
  // shapes.add(new Rectangle(new Vec3D(50, 50, 0), 300, 300));
  // shapes.add(new Rectangle(new Vec3D(450, 200, 0), 300, 300));
  shapes.add(new Rectangle(new Vec3D(50, 50, 0), 150, 100));
  shapes.add(new Rectangle(new Vec3D(50, 200, 0), 150, 100));
  shapes.add(new Rectangle(new Vec3D(400, 200, 0), 75, 100));
  shapes.add(new Rectangle(new Vec3D(400, 50, 0), 75, 100));
  shapes.add(new Rectangle(new Vec3D(250, 250, 0), 75, 150));


  cp5 = new ControlP5(this);

  createProperties();
  createToolbar();

  cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY).getRotatedAroundAxis(new Vec3D(0.0, 0.0, 1.0), radians(cameraX));
  selectedTool = new SelectTool(view2DRect, properties, shapes, transform2D);
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
    new SelectTool(view2DRect, properties, shapes, transform2D),
    new DrawTool(view2DRect, properties, shapes, transform2D),
    new ConnectTool(view2DRect, properties, shapes, connections, transform2D),
    new DeleteTool(view2DRect, properties, shapes, connections, transform2D)
  };

  for (int i = 0; i < tools.length; i++)
  {
    Tool theTool = tools[i];
    PGraphics toolIcon = theTool.getIcon(createGraphics(150, 50));
    toolbar.addCustomItem(theTool.getName(), i, new ShapeButton(toolIcon));
  }
  
  PGraphics printIcon = createGraphics(150, 50);
  printIcon.beginDraw();
  printIcon.textSize(30.0);
  printIcon.text("print",40,33);
  printIcon.endDraw();
  toolbar.addCustomItem("Print", 4, new ShapeButton(printIcon));
  
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
    if(id != 4)
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
    if(id == 4)
    {
      PrintDialog printDialog = new PrintDialog(shapes); 
    }
  }
}

void keyPressed()
{
  if (key == '+')
  {
    transform2D.scaleUp(0.01);
  }
  if (key == '-')
  {
    transform2D.scaleDown(0.01);
  }
}
