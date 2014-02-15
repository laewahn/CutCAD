package de.mcp.customizer.application.tools.objectManipulation;

import processing.core.PConstants;
import processing.core.PGraphics;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.GShape;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.model.shapes.CopyShape;
import de.mcp.customizer.view.Transformation;

/**
 * The CopyTool is used to create a copy of a Shape.
 */
public class CopyTool extends Tool {

	GShape master;
	Vector2D lastMousePosition;
	boolean selected;

	Shape copyShape, previewShape;

	/**
	 * @param customizer the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public CopyTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "Copy.svg");
		
		this.selected = false;
		this.lastMousePosition = new Vector2D(0, 0);
	}

    public void mouseButtonPressed(Vector2D position, int button)
    {
    	if (this.inView(position)) {
    		if (!selected)
    		{
    			for (Shape s : this.objectContainer.allShapes())
    			{
    				if (s.getGShape().isSelected() && button == PConstants.LEFT)
    				{
    					this.customizer.displayStatus("Shape selected! Use the left mouse button to create copies or use the right mouse button to clear the selection");
    					master = s.getGShape();
    					previewShape = new CopyShape(master.getVertices(), lastMousePosition, master.getName());
    					previewShape.getGShape().setMaterial(master.getMaterial());
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
    				copy.getGShape().setPosition2D(lastMousePosition);
    				copy.recalculate();
    				this.objectContainer.addShape(copy);
    			}
    		}
    	}
    }

    public void mouseButtonReleased(Vector2D position, int button)
    {
        // no actions required
    } 
    
    public void mouseMoved(Vector2D position)
    {
		if (this.inView(position)) {
			lastMousePosition = this.positionRelativeToView(position);
	        this.customizer.displayMousePosition(lastMousePosition.scale(0.1f));

			for (Shape s : this.objectContainer.allShapes()) 
			{
				s.getGShape().setSelected(s.getGShape().mouseOver(lastMousePosition));
			}
		} 
		else 
		{
			for (Shape s : this.objectContainer.allShapes()) 
			{
				s.getGShape().setSelected(false);
			}
		}
	}

    public void draw2D(PGraphics p, Transformation t)
    {
    	if(selected)
    	{
    		previewShape.getGShape().setPosition2D(lastMousePosition);
    		previewShape.getGShape().draw2D(p, t);
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
