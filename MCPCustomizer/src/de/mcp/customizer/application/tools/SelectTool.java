package de.mcp.customizer.application.tools;
import java.util.List;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation2D;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public class SelectTool extends Tool {
    
    List<Shape> shapes;
    List<Connection> connections;
    boolean dragging;
    Vec2D originalMousePosition;
    
    public SelectTool(Rect view, Properties properties, List<Shape> shapes, List<Connection> connections, Transformation2D transform) 
    {
        super(view, properties, transform, "SelectTool");
        
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
        context.translate(60, 10);
        context.rotate(PApplet.radians(-45));
        context.triangle(0, 0, -10, 30, 10, 30);
        context.rect(-5, 30, 10, 10);
        context.endDraw();

        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        boolean noneSelected = true;
        for (Shape s : shapes)
        {
            if (this.inView(position) && s.getShape().isSelected() && button == PConstants.LEFT)
            {
                properties.show();
                properties.plugTo(s);
            } else if (this.inView(position) && s.getShape().isSelected() && button == PConstants.RIGHT){
                this.dragging = true;
                // this.originalMousePosition.set(position.sub(new Vec2D(view2DPosX, view2DPosY)));
                Vec2D currentMousePosition = this.positionRelativeToView(position);
                this.originalMousePosition.set(currentMousePosition);
                noneSelected = false;
            }
        }
        for (Connection c : connections)
        {
        	if (this.inView(position) && c.isSelected() && button == PConstants.LEFT)
        	{
        		properties.show();
        		properties.plugTo(c);
        	}
        }
        for (Cutout c : Cutout.getAllCutouts())
        {
        	if (this.inView(position) && c.isSelected() && button == PConstants.LEFT)
        	{
        		properties.show();
        		properties.plugTo(c);
        	}
        }
        if (this.inView(position) && button == PConstants.RIGHT && noneSelected)
        {
            this.dragging = true;
            Vec2D currentMousePosition = this.positionRelativeToView(position);
            this.originalMousePosition.set(currentMousePosition);
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        if (button == PConstants.RIGHT) {
            this.dragging = false;
        }
    }
    
    public void mouseMoved(Vec2D position)
    {
        if (this.inView(position))
        {
            Vec2D relativePosition = this.positionRelativeToView(position);

            boolean noneSelected = true;

            for (Shape s : shapes) {
                s.getShape().setSelected(s.getShape().mouseOver(relativePosition));

                if (s.getShape().isSelected() && this.dragging)
                {
                    Vec2D currentMousePosition = this.positionRelativeToView(position);
                    s.getShape().translate2D(currentMousePosition.sub(originalMousePosition));
                    originalMousePosition.set(currentMousePosition);
                    noneSelected = false;
                }
            }
            for (Connection c : connections)
            {
                c.setSelected(c.mouseOver(relativePosition));

                if (c.isSelected())
                {
                    noneSelected = false;
                }
            }
            for (Cutout c : Cutout.getAllCutouts())
            {
                c.setSelected(c.mouseOver(relativePosition));

                if (c.isSelected())
                {
                    noneSelected = false;
                }
            }
            if (noneSelected && this.dragging)
            {
                Vec2D currentMousePosition = this.positionRelativeToView(position);
                
                transform.translate(currentMousePosition.sub(originalMousePosition));
                originalMousePosition.set(this.positionRelativeToView(position));                
            }
        }
        else
        {
            for (Shape s : shapes) {
                s.getShape().setSelected(false);
            }
        }
    }
}
