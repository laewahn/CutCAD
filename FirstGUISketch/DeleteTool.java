import processing.core.*;
import toxi.geom.*;
import java.util.*;

class DeleteTool extends Tool {
    
    List<Shape> shapes;
    List<Connection> connections;
    boolean dragging;
    Vec2D originalMousePosition;
    
    public DeleteTool(Rect view, Properties properties, List<Shape> shapes, List<Connection> connections, Transformation2D transform) 
    {
        super(view, properties, transform);
        
        this.shapes = shapes;
        this.connections = connections;
        this.dragging = false;
        this.originalMousePosition = new Vec2D(0,0);
    }

    public PGraphics getIcon(PGraphics context)
    {
        context.beginDraw();
        context.noFill();
        context.stroke(0);
        context.strokeWeight(2);
        context.line(50, 10, 100, 40);
        context.line(50, 40, 100, 10);
        context.endDraw();

        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        Iterator<Shape> shapeIterator = shapes.iterator();

        Shape s;
        while(shapeIterator.hasNext())
        {
            s = shapeIterator.next();
            if (this.inView(position) && s.getShape().isSelected() && button == PConstants.LEFT)
            {
                removeConnectionsContaining(s);
                shapeIterator.remove();
            } 
        }

        Iterator<Connection> connectionIterator = connections.iterator();

        Connection c;
        while (connectionIterator.hasNext())
        {
            c = connectionIterator.next();
            if (this.inView(position) && c.isSelected() && button == PConstants.LEFT)
            {
                c.undoConnection();
                connectionIterator.remove();
            }
        }
    }

    private void removeConnectionsContaining(Shape s)
    {
        Iterator<Connection> connectionIterator = connections.iterator();

        Connection c;
        while (connectionIterator.hasNext())
        {
            c = connectionIterator.next();
            boolean shapeIsParentOfMasterEdge = c.getMasterEdge().getShape().getParent().equals(s);
            boolean shapeIsParentOfSlaveEdge = c.getSlaveEdge().getShape().getParent().equals(s);
            if (shapeIsParentOfMasterEdge || shapeIsParentOfSlaveEdge)
            {
                c.undoConnection();
                connectionIterator.remove();
            }
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        // nothing to do here
    }
    
    public void mouseMoved(Vec2D position)
    {
        if (this.inView(position))
        {
            Vec2D relativePosition = this.positionRelativeToView(position);


            for (Shape s : shapes) {
                s.getShape().setSelected(s.getShape().mouseOver(relativePosition));
            }
            for (Connection c : connections) {
                c.setSelected(c.mouseOver(relativePosition));
            }
        }
        else
        {
            for (Shape s : shapes) {
                s.getShape().setSelected(false);
            }
            for (Connection c : connections) {
                c.setSelected(false);
            }
        }
    }
}
