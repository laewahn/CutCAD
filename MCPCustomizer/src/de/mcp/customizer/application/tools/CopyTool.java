package de.mcp.customizer.application.tools;

import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.CopyShape;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Edge;
import de.mcp.customizer.model.GShape;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation2D;

public class CopyTool extends Tool {

	List<Shape> shapes;
	GShape master;
	Vec2D lastMousePosition;
	boolean selected;

	Shape copyShape, previewShape;

	public CopyTool(Rect view, Properties properties, List<Shape> shapes, Transformation2D transform) {
		super(view, properties, transform, "SelectTool");

		this.shapes = shapes;
		this.selected = false;
		this.lastMousePosition = new Vec2D(0, 0);
	}

	public PGraphics getIcon(PGraphics context) {
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
    	if (!selected)
    	{
    		for (Shape s : shapes)
    		{
                if (s.getShape().isSelected() && button == PConstants.LEFT)
                {
                	master = s.getShape();
                	previewShape = new CopyShape(master.getVertices(), lastMousePosition, master.getName());
                	selected = true;                        
                }
            }
    	}
    	else
    	{
    		if(button == PConstants.RIGHT)
    		{
    			this.copyShape = null;
    			this.selected = false;
    			this.previewShape = null;
    		}
    		else
    		{
    			this.copyShape = new CopyShape(master.getVertices(), lastMousePosition, master.getName());
    			shapes.add(this.copyShape);
    		}
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        // no actions required
    }   
    
    public void mouseMoved(Vec2D position)
    {
		if (this.inView(position)) {
			lastMousePosition = this.positionRelativeToView(position);

			for (Shape s : shapes) 
			{
				s.getShape().setSelected(s.getShape().mouseOver(lastMousePosition));
			}
		} 
		else 
		{
			for (Shape s : shapes) 
			{
				s.getShape().setSelected(false);
			}
		}
	}

    public void draw2D(PGraphics p)
    {
    	if(selected)
    	{
    		previewShape.getShape().setPosition2D(lastMousePosition);
    		previewShape.getShape().draw2D(p);
    	}
    }
    
	@Override
	public void wasUnselected() {
		this.copyShape = null;
		this.selected = false;
		this.previewShape = null;
		super.wasUnselected();
	}
}
