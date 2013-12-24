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
    

    public ConnectTool(Rect view, Properties properties, List<Shapes> shapes)
    {
        super(view, properties);
        this.shapes = shapes;
        this.selectedFirst = false;
        this.connections = new ArrayList<Connection>();
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
                        this.previewConnection.setEdge1(e);
                        selectedFirst = true;                        
                    }
                    else
                    {
                        this.previewConnection.setEdge2(e);
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
        if (selectedFirst) {
            this.lastMousePosition = position;
        }

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
            Vec2D mid = previewConnection.getEdge1().getMid().add(previewConnection.getEdge1().getShape().getPosition2D());
            p.stroke(255,0,0);
            Vec2D lineStart = mid.add(this.view.getTopLeft());
            p.line(lineStart.x(), lineStart.y(), this.lastMousePosition.x(), this.lastMousePosition.y());
            // line(mid.x()+view2DPosX, mid.y()+view2DPosY, mouseX, mouseY);
            p.stroke(0);
        }

        p.endDraw();
    }

}
