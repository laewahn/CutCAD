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
Tool selectedTool;
Rectangle previewRectangle;

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
    selectedTool = new SelectTool(rectangles);
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

    if (previewRectangle != null)
    {
        previewRectangle.drawRectangle2D(view2D);
        // Rectangle.drawPreview(view2D, startX, startY, mouseX-startX-view2DPosX, mouseY-startY-view2DPosY);
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
    if (mouseOver3DView())
    {
        startX = mouseX - view3DPosX;
        startY = mouseY - view3DPosY;
    }
    
    selectedTool.mouseButtonPressed(mouseX, mouseY, mouseButton);
}

void mouseDragged()
{
    if (mouseOver3DView()) {
        cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY + 5 * (mouseY - view3DPosY - startY)).getRotatedAroundAxis(new Vec3D(0.0,0.0,1.0), radians(cameraX + mouseX - view3DPosX - startX));
    }

    selectedTool.mouseMoved(mouseX, mouseY);
}

void mouseReleased()
{
    selectedTool.mouseButtonReleased(mouseX, mouseY, mouseButton);

    if (mouseOver3DView())
    {
        cameraX += mouseX - view3DPosX - startX;
        cameraY += 5 * (mouseY - view3DPosY - startY);
    }
}

void mouseMoved() 
{
    selectedTool.mouseMoved(mouseX, mouseY);
}

boolean mouseOver3DView()
{
    return mouseX > view3DPosX && mouseX <= view3DPosX + viewSizeX && mouseY > view3DPosY && mouseY <= view3DPosY + viewSizeY;
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
            selectedTool = new SelectTool(rectangles);
            drawing = false;
            selecting = true;
        }
        if (id == 1)
        {
            selectedTool = new DrawTool(new Rect(view2DPosX, view2DPosY, viewSizeX, viewSizeY));
            drawing = true;
            selecting = false;
            properties.hide();
        }
    }
}


abstract class Tool {
    public abstract void mouseButtonPressed(int x, int y, int button);
    public abstract void mouseButtonReleased(int x, int y, int button);
    public abstract void mouseMoved(int x, int y);
}

class SelectTool extends Tool {
    
    List<Rectangle> rectangles;
    boolean dragging;

    public SelectTool(List<Rectangle> rectangles) {
        this.rectangles = rectangles;
        this.dragging = false;
    }

    public void mouseButtonPressed(int x, int y , int button){
        for (Rectangle r : rectangles)
        {
            if (r.isSelected() && button == LEFT)
            {
                properties.plugTo(r);
                properties.show();
            } else if (r.isSelected() && button == RIGHT){
                this.dragging = true;
            }
        }
    };

    public void mouseButtonReleased(int x, int y, int button){
        if (button == RIGHT) {
            this.dragging = false;
        }
    };
    
    public void mouseMoved(int x, int y){
        for (Rectangle r : rectangles) {
            r.setSelected(r.mouseOver(x, y, view2DPosX, view2DPosY));

            if (r.isSelected() && this.dragging)
            {
                r.moveTo(x-view2DPosX, y-view2DPosY);
            }
        }
    };
};

class DrawTool extends Tool {
    
    boolean isDrawing;

    int startCoordX;
    int startCoordY;

    Rect drawableArea;

    public DrawTool(Rect drawableArea) {
        this.drawableArea = drawableArea;
    }

    public void mouseButtonPressed(int x, int y, int button){
        if (this.inDrawableArea(x, y)){
            isDrawing = true;
            startCoordX = x - (int) drawableArea.getTopLeft().x();
            startCoordY = y - (int) drawableArea.getTopLeft().y();
            previewRectangle = new Rectangle(startCoordX, startCoordY, 0, 0, 50);
        }
    };

    public void mouseButtonReleased(int x, int y, int button){
        if (isDrawing && this.inDrawableArea(x, y)) {

            endX = x - (int) drawableArea.getTopLeft().x();
            endY = y - (int) drawableArea.getTopLeft().y();

            rectangles.add(previewRectangle);
            previewRectangle = null;

            isDrawing = false;
        }
    };

    public void mouseMoved(int x, int y){
        if (isDrawing){
            endX = x - (int) drawableArea.getTopLeft().x();
            endY = y - (int) drawableArea.getTopLeft().y();
            previewRectangle.setSizeX(endX - startCoordX);
            previewRectangle.setSizeY(endY - startCoordY);
        }
    };

    private boolean inDrawableArea(int x, int y) {
        return x > drawableArea.getTopLeft().x() && x <= drawableArea.getBottomRight().x() 
        && y > drawableArea.getTopLeft().y() && y <= drawableArea.getBottomRight().y() ;
    }

}