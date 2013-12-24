import processing.core.*;
import toxi.geom.*;
import java.util.*;

class ConnectTool extends Tool
{

    boolean selectedFirst;
    Vec2D lastMousePosition;

    Connection previewConnection;
    List<Connection> connections;

    List<Shapes> shapes;
    

    public ConnectTool(Rect view, Properties properties, List<Shapes> shapes, List<Connection> connections)
    {
        super(view, properties);
        this.shapes = shapes;
        this.selectedFirst = false;
        this.connections = connections;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        for (Shapes s : shapes)
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
                        this.previewConnection.connect();
                        this.connections.add(this.previewConnection);
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
    
        for (Shapes s : shapes)
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
        p.beginDraw();

        if (selectedFirst) {
            Vec2D mid = previewConnection.getMasterEdge().getMid().add(previewConnection.getMasterEdge().getShape().getPosition2D());
            p.stroke(255,0,0);
            Vec2D lineStart = mid;
            Vec2D lineEnd = this.positionRelativeToView(this.lastMousePosition);
            p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
            p.stroke(0);
        }

        p.endDraw();
    }

}
