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
int view3DPosX = 700;
int view3DPosY = 50;

int cameraX = 45;
int cameraY = 1000;

Vec3D cameraPosition;
Tool selectedTool;
Shapes previewRectangle = new Rectangle(50, 50, 0, 100, 100, 50);
Shapes newRectangle;
Connection previewConnection;

void setup()
{
    size(displayWidth,displayHeight,P3D);
    ortho();

    view2D = createGraphics(viewSizeX, viewSizeY, P3D);
    view3D = createGraphics(viewSizeX, viewSizeY, P3D);

    shapes = new ArrayList<Shapes>();
    connections = new ArrayList<Connection>();
    
    shapes.add(new Rectangle(50, 50, 0, 100, 100, 5));
    
    cp5 = new ControlP5(this);

    createProperties();
    createToolbar();
    
    cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY).getRotatedAroundAxis(new Vec3D(0.0,0.0,1.0), radians(cameraX));
    selectedTool = new SelectTool(shapes);
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

    if (newRectangle != null)
    {
        newRectangle.getShape().draw2D(view2D);
    }

    if (previewConnection != null)
    {
        Vec2D mid = previewConnection.getEdge1().getMid().add(previewConnection.getEdge1().getShape().getPosition2D());
        stroke(255,0,0);
        line(mid.x()+view2DPosX, mid.y()+view2DPosY, mouseX, mouseY);
        stroke(0);
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
    previewRectangle.getShape().drawPreview(rectangleIcon, 50, 10, 50, 30);
    rectangleIcon.endDraw();
    toolbar.addCustomItem("Rectangle", 1, new ShapeButton(rectangleIcon));

    PGraphics connectIcon = createGraphics(150,50);
    connectIcon.beginDraw();
    connectIcon.noFill();
    connectIcon.stroke(0);
    connectIcon.strokeWeight(2);
    connectIcon.line(25, 10, 25, 40);
    connectIcon.line(25, 25, 125, 25);
    connectIcon.line(125, 10, 125, 40);
    connectIcon.endDraw();
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
            selectedTool = new SelectTool(shapes);
        }
        if (id == 1)
        {
            selectedTool = new DrawTool(new Rect(view2DPosX, view2DPosY, viewSizeX, viewSizeY));
            properties.hide();
        }
        if (id == 2)
        {
            selectedTool = new ConnectTool(shapes);
            properties.hide();
        }
    }
}


interface Tool {
    public void mouseButtonPressed(int x, int y, int button);
    public void mouseButtonReleased(int x, int y, int button);
    public void mouseMoved(int x, int y);
}

class SelectTool implements Tool {
    
    List<Shapes> shapes;
    boolean dragging;

    public SelectTool(List<Shapes> shapes) {
        this.shapes = shapes;
        this.dragging = false;
    }

    public void mouseButtonPressed(int x, int y , int button){
        for (Shapes s : shapes)
        {
            if (s.getShape().isSelected() && button == LEFT)
            {
                properties.plugTo(s);
                properties.show();
            } else if (s.getShape().isSelected() && button == RIGHT){
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
        for (Shapes s : shapes) {
            s.getShape().setSelected(s.getShape().mouseOver(x, y, view2DPosX, view2DPosY));

            if (s.getShape().isSelected() && this.dragging)
            {
                s.getShape().setPosition2D(new Vec2D(x-view2DPosX, y-view2DPosY));
            }
        }
    };
};

class ConnectTool implements Tool
{
    boolean selectedFirst;

    List<Shapes> shapes;

    public ConnectTool(List<Shapes> shapes)
    {
        this.shapes = shapes;
        this.selectedFirst = false;
    }

    public void mouseButtonPressed(int x, int y, int button)
    {

        for (Shapes s : shapes)
        {
            for (Edge e : s.getShape().getEdges())
            {
                if (e.isSelected() && button == LEFT)
                {
                    if (!selectedFirst)
                    {
                        previewConnection = new Connection();
                        previewConnection.setEdge1(e);
                        selectedFirst = true;                        
                    }
                    else
                    {
                        previewConnection.setEdge2(e);
                        connections.add(previewConnection);
                        println("Added Connection between " + previewConnection.getEdge1() + " and " + previewConnection.getEdge2());
                        previewConnection = null;
                        selectedFirst = false;
                    }
                }
            }
        }
    }

    public void mouseButtonReleased(int x, int y, int button)
    {
        // no actions required
    }

    public void mouseMoved(int x, int y)
    {
        for (Shapes s : shapes)
        {
            for (Edge e : s.getShape().getEdges())
            {
                e.setSelected(e.mouseOver(x, y, view2DPosX, view2DPosY));
            }
        }
    }
}

class DrawTool implements Tool {
    
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
            newRectangle = new Rectangle(startCoordX, startCoordY, 0, 0, 0, 50);
        }
    };

    public void mouseButtonReleased(int x, int y, int button){
        if (isDrawing && this.inDrawableArea(x, y)) {

            endX = x - (int) drawableArea.getTopLeft().x();
            endY = y - (int) drawableArea.getTopLeft().y();
            newRectangle.changeValue(0, endX - startCoordX);
            newRectangle.changeValue(1, endY - startCoordY);

            shapes.add(newRectangle);
            newRectangle = null;

            isDrawing = false;
        }
    };

    public void mouseMoved(int x, int y){
        if (isDrawing){
            endX = x - (int) drawableArea.getTopLeft().x();
            endY = y - (int) drawableArea.getTopLeft().y();
            newRectangle.changeValue(0, endX - startCoordX);
            newRectangle.changeValue(1, endY - startCoordY);
        }
    };

    private boolean inDrawableArea(int x, int y) {
        return x > drawableArea.getTopLeft().x() && x <= drawableArea.getBottomRight().x() 
        && y > drawableArea.getTopLeft().y() && y <= drawableArea.getBottomRight().y() ;
    }

}
