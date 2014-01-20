package de.mcp.customizer.application.tools;

import java.util.List;


//import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
//import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.CopyShape;
//import de.mcp.customizer.model.Cutout;
//import de.mcp.customizer.model.Edge;
import de.mcp.customizer.model.GShape;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;

public class CopyTool extends Tool {

	List<Shape> shapes;
	GShape master;
	Vec2D lastMousePosition;
	boolean selected;

	Shape copyShape, previewShape;

	public CopyTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes, Transformation transform) {
		super(view, properties, statusbar, transform, "CopyTool");

		this.shapes = shapes;
		this.selected = false;
		this.lastMousePosition = new Vec2D(0, 0);
	}

	public PGraphics getIcon(PGraphics context) {
		context.beginDraw();
		context.noFill();
		context.stroke(0);
		context.strokeWeight(2);
		context.line(50, 7, 50, 36);
		context.line(50, 7, 100, 7);
		context.line(50, 36, 60, 36);
		context.line(100, 7, 100, 13);
		
		context.line(60, 14, 60, 43);
		context.line(60, 14, 110, 14);
		context.line(110, 14, 110, 43);
		context.line(60, 43, 110, 43);
		context.endDraw();

		return context;
	}

    public void mouseButtonPressed(Vec2D position, int button)
    {
    	if (this.inView(position)) {
    		if (!selected)
    		{
    			for (Shape s : shapes)
    			{
    				if (s.getShape().isSelected() && button == PConstants.LEFT)
    				{
    					this.displayStatus("Shape selected! Use the left mouse button to create copies or use the right mouse button to clear the selection");
    					master = s.getShape();
    					previewShape = new CopyShape(master.getVertices(), lastMousePosition, master.getName());
    					previewShape.getShape().setMaterial(master.getMaterial());
    					selected = true;                        
    				}
    			}
    		}
    		else
    		{
    			if(button == PConstants.RIGHT)
    			{
    				this.displayStatus("Select the shape you want to copy");
    				this.copyShape = null;
    				this.selected = false;
    				this.previewShape = null;
    			}
    			else
    			{
    				this.displayStatus("Copy created! Use the left mouse button to create copies or use the right mouse button to clear the selection");
    				//this.copyShape = new CopyShape(master.getVertices(), lastMousePosition, master.getName());
    				//copyShape.getShape().setMaterial(master.getMaterial());
    				Shape copy = master.copyCompleteStructure();
    				copy.getShape().setPosition2D(lastMousePosition);
    				shapes.add(copy);
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
	public void wasSelected() {
		this.displayStatus("Select the shape you want to copy");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		this.copyShape = null;
		this.selected = false;
		this.previewShape = null;
		super.wasUnselected();
	}
}
