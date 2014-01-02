import processing.core.*;
import toxi.geom.*;
import java.util.*;

class ConnectTool extends Tool
{

    boolean selectedFirst;
    Vec2D lastMousePosition;

    Connection previewConnection;
    List<Connection> connections;

    List<Shape> shapes;
    

    public ConnectTool(Rect view, Properties properties, List<Shape> shapes, List<Connection> connections, Transformation2D transform)
    {
        super(view, properties, transform, "ConnectTool");
        this.shapes = shapes;
        this.selectedFirst = false;
        this.connections = connections;
    }

    public PGraphics getIcon(PGraphics context) 
    {
        context.beginDraw();
        context.noFill();
        context.stroke(0);
        context.strokeWeight(2);
        context.line(25, 10, 25, 40);
        context.line(25, 25, 125, 25);
        context.line(125, 10, 125, 40);
        context.endDraw();

        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        for (Shape s : shapes)
        {
            for (Edge e : s.getShape().getEdges())
            {
                if (e.isSelected() && button == PConstants.LEFT)
                {
                    if (!selectedFirst)
                    {
                        this.previewConnection = new Connection();
                        this.previewConnection.setMasterEdge(e);
                        selectedFirst = true;                        
                    }
                    else
                    {
                        this.previewConnection.setSlaveEdge(e);
                        if(this.previewConnection.connect()) 
                        {
                            this.connections.add(this.previewConnection);
                        }
                        // println("Added Connection between " + this.previewConnection.getEdge1() + " and " + this.previewConnection.getEdge2());
                        this.previewConnection = null;
                        selectedFirst = false;
                    }
                }
            }
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        // no actions required
    }   
    
    public void mouseMoved(Vec2D position)
    {
        this.lastMousePosition = position;
    
        for (Shape s : shapes)
        {
            for (Edge e : s.getShape().getEdges())
            {
                Vec2D relativePosition = this.positionRelativeToView(position);
                e.setSelected(e.mouseOver(relativePosition));
            }
        }
    }

    public void draw2D(PGraphics p)
    {
        if (selectedFirst) {
            Vec2D mid = previewConnection.getMasterEdge().getMid().add(previewConnection.getMasterEdge().getShape().getPosition2D());
            p.stroke(255,0,0);
            Vec2D lineStart = mid;
            Vec2D lineEnd = this.positionRelativeToView(this.lastMousePosition);
            p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
            p.stroke(0);
        }
    }

}
