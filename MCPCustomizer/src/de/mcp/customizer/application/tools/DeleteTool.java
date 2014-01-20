package de.mcp.customizer.application.tools;
import java.util.Iterator;
import java.util.List;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public class DeleteTool extends Tool {
    
    List<Shape> shapes;
    List<Connection> connections;
    boolean dragging;
    Vec2D originalMousePosition;
    
    public DeleteTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes, List<Connection> connections, Transformation transform) 
    {
        super(view, properties, statusbar, transform, "DeleteTool");
        
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
                removeCutoutsContaining(s);
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
        
        Iterator<Cutout> cutoutIterator = Cutout.getAllCutouts().iterator();

        Cutout o;
        while (cutoutIterator.hasNext())
        {
            o = cutoutIterator.next();
            if (this.inView(position) && o.isSelected() && button == PConstants.LEFT)
            {
                o.removeCutout();
                cutoutIterator.remove();
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
    
    private void removeCutoutsContaining(Shape s)
    {
        Iterator<Cutout> cutoutIterator = Cutout.getAllCutouts().iterator();

        Cutout c;
        while (cutoutIterator.hasNext())
        {
            c = cutoutIterator.next();
            if (c.getMasterShape() == s || c.getSlaveShape() == s)
            {
            	c.removeCutout();
                cutoutIterator.remove();
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
            for (Cutout c : Cutout.getAllCutouts()) {
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
            for (Cutout c : Cutout.getAllCutouts()) {
                c.setSelected(false);
            }
        }
    }

	@Override
	public void wasSelected() {
		this.displayStatus("Click on a shape or connection to delete it");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		super.wasUnselected();
	}
    
    
}
