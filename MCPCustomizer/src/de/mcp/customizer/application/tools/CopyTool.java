package de.mcp.customizer.application.tools;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.CopyShape;
import de.mcp.customizer.model.GShape;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.Shape;

public class CopyTool extends Tool {

	GShape master;
	Vec2D lastMousePosition;
	boolean selected;

	Shape copyShape, previewShape;

	public CopyTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "Copy.svg");
		
		this.selected = false;
		this.lastMousePosition = new Vec2D(0, 0);
	}

    public void mouseButtonPressed(Vec2D position, int button)
    {
    	if (this.inView(position)) {
    		if (!selected)
    		{
    			for (Shape s : this.objectContainer.allShapes())
    			{
    				if (s.getShape().isSelected() && button == PConstants.LEFT)
    				{
    					this.customizer.displayStatus("Shape selected! Use the left mouse button to create copies or use the right mouse button to clear the selection");
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
    				this.customizer.displayStatus("Select the shape you want to copy");
    				this.copyShape = null;
    				this.selected = false;
    				this.previewShape = null;
    			}
    			else
    			{
    				this.customizer.displayStatus("Copy created! Use the left mouse button to create copies or use the right mouse button to clear the selection");
    				Shape copy = master.copyCompleteStructure();
    				copy.getShape().setPosition2D(lastMousePosition);
    				copy.getShape().recalculate(copy.copy().getShape().getVertices());
    				this.objectContainer.addShape(copy);
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
	        this.customizer.displayMousePosition(lastMousePosition.scale(0.1f));

			for (Shape s : this.objectContainer.allShapes()) 
			{
				s.getShape().setSelected(s.getShape().mouseOver(lastMousePosition));
			}
		} 
		else 
		{
			for (Shape s : this.objectContainer.allShapes()) 
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
		this.customizer.displayStatus("Select the shape you want to copy");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.customizer.displayStatus("");
		this.copyShape = null;
		this.selected = false;
		this.previewShape = null;
		super.wasUnselected();
	}
}
