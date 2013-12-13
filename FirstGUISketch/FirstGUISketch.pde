import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import controlP5.*;

Toolbar toolbar;
Properties properties;
ControlP5 cp5;

ToxiclibsSupport gfx;
PGraphics view2D, view3D;
ArrayList<Rectangle> rectangles;

int startX = 0;
int startY = 0;
int endX = 0;
int endY = 0;
boolean drawing = false;
boolean startedDrawing = false;
boolean selecting = true;

int viewSizeX = 500;
int viewSizeY = 500;
int view2DPosX = 150;
int view2DPosY = 50;
int view3DPosX = 700;
int view3DPosY = 50;

int cameraX = 45;
int cameraY = 1000;

Vec3D cameraPosition;

void setup()
{
    size(displayWidth,displayHeight,P3D);
    ortho();

    view2D = createGraphics(viewSizeX, viewSizeY);
    view3D = createGraphics(viewSizeX, viewSizeY, P3D);

    gfx = new ToxiclibsSupport(this, view3D);   

    rectangles = new ArrayList<Rectangle>();
    
    cp5 = new ControlP5(this);

    createProperties();
    createToolbar();
    
    cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY).getRotatedAroundAxis(new Vec3D(0.0,0.0,1.0), radians(cameraX));
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

    for (Rectangle r : rectangles)
    {
            r.drawRectangle2D(view2D);
    }

    if (startedDrawing)
    {
        Rectangle.drawPreview(view2D, startX, startY, mouseX-startX-view2DPosX, mouseY-startY-view2DPosY);
    }

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
    for (Rectangle r : rectangles)
    {
        r.drawRectangle3D(view3D, gfx);
    }

    view3D.endDraw();

    image(view3D, view3DPosX, view3DPosY); 
}

void createToolbar()
{
    toolbar = new Toolbar(cp5, "Toolbar");
    toolbar.setPosition(0, 50).setSize(150, 500).setItemHeight(50).disableCollapse().hideBar();

    PGraphics cursorIcon = createGraphics(150,50);
    cursorIcon.beginDraw();
    cursorIcon.noFill();
    cursorIcon.stroke(0);
    cursorIcon.strokeWeight(2);
    cursorIcon.translate(60,10);
    cursorIcon.rotate(radians(-45));
    cursorIcon.triangle(0, 0, -10, 30, 10, 30);
    cursorIcon.rect(-5, 30, 10, 10);
    cursorIcon.endDraw();
    toolbar.addCustomItem("Cursor", 0, new ShapeButton(cursorIcon));

    PGraphics rectangleIcon = createGraphics(150,50);
    rectangleIcon.beginDraw();
    rectangleIcon.noFill();
    rectangleIcon.stroke(0);
    rectangleIcon.strokeWeight(2);
    Rectangle.drawPreview(rectangleIcon, 50, 10, 50, 30);
    rectangleIcon.endDraw();
    toolbar.addCustomItem("Rectangle", 1, new ShapeButton(rectangleIcon));
}

void createProperties()
{
    properties = new Properties(cp5, 0, 0, width, 50);
    properties.hide();
}

void mousePressed()
{
    if (mouseOver2DView() && drawing)
    {
        startX = mouseX - view2DPosX;
        startY = mouseY - view2DPosY;
        startedDrawing = true;
    }
    
    if (mouseOver3DView())
    {
        startX = mouseX - view3DPosX;
        startY = mouseY - view3DPosY;
    }
    
    if (selecting)
    {
        for (Rectangle r : rectangles)
        {
            if (r.isSelected() && mouseButton == LEFT)
            {
                properties.plugTo(r);
                properties.show();
            }
        }
    }
}

void mouseDragged()
{
    if (selecting)
    {
        for (Rectangle r : rectangles)
        {
            if (r.mouseOver(mouseX, mouseY, view2DPosX, view2DPosY) && mouseButton == RIGHT)
            {
                r.moveTo(mouseX-view2DPosX, mouseY-view2DPosY);
            }
        }
    }

    if (mouseOver3DView()) {
        cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY + 5 * (mouseY - view3DPosY - startY)).getRotatedAroundAxis(new Vec3D(0.0,0.0,1.0), radians(cameraX + mouseX - view3DPosX - startX));
    }
}

void mouseReleased()
{
    if (mouseOver2DView() && drawing && startedDrawing)
    {
        endX = mouseX - view2DPosX;
        endY = mouseY - view2DPosY;
        rectangles.add(new Rectangle(startX, startY, endX-startX, endY-startY, 50));
        startedDrawing = false;
    }
    

    if (mouseOver3DView())
    {
        cameraX += mouseX - view3DPosX - startX;
        cameraY += 5 * (mouseY - view3DPosY - startY);
    }
}

void mouseMoved() 
{
    if (selecting) {
        for (Rectangle r : rectangles) {
            r.setSelected(r.mouseOver(mouseX, mouseY, view2DPosX, view2DPosY));
        }
    }
}

boolean mouseOver3DView()
{
    return mouseX > view3DPosX && mouseX <= view3DPosX + viewSizeX && mouseY > view3DPosY && mouseY <= view3DPosY + viewSizeY;
}

boolean mouseOver2DView()
{
    return mouseX > view2DPosX && mouseX <= view2DPosX + viewSizeX && mouseY > view2DPosY && mouseY <= view2DPosY + viewSizeY;
}

void controlEvent(ControlEvent theEvent) 
{
    // an event from the toolbar
    if(theEvent.isGroup() && theEvent.isFrom("Toolbar"))
    {
        int id = (int)theEvent.group().value();
        // TODO: Find another method to find out which element of the list sent the event. 
        // One should be able to get the object or at the very least the name instead of an ID

        // For now: 0 is Select, 1 is Rectangle
        if (id == 0)
        {
            drawing = false;
            selecting = true;
        }
        if (id == 1)
        {
            drawing = true;
            selecting = false;
            properties.hide();
        }
    }
}
