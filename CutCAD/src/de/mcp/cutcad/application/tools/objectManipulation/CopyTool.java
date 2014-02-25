package de.mcp.cutcad.application.tools.objectManipulation;

import processing.core.PConstants;
import processing.core.PGraphics;
import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.primitives.GShape;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.model.shapes.CopyShape;
import de.mcp.cutcad.view.Transformation;

/**
 * The CopyTool is used to create a copy of a Shape.
 */
public class CopyTool extends Tool {

	GShape master;
	Vector2D lastMousePosition;
	boolean selected;

	Shape copyShape, previewShape;

	/**
	 * @param application the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public CopyTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
		
		this.selected = false;
		this.lastMousePosition = new Vector2D(0, 0);
	}

	@Override
	public String getIconName() {
		return "Copy.svg";
	}
	
    public void mouseButtonPressed(Vector2D position, int button)
    {
    	if (view.containsPoint(position)) {
    		if (!selected)
    		{
    			for (Shape s : this.objectContainer.allShapes())
    			{
    				if (s.getGShape().isSelected() && button == PConstants.LEFT)
    				{
    					this.application.displayStatus("Shape selected! Use the left mouse button to create copies or use the right mouse button to clear the selection");
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
    				this.application.displayStatus("Select the shape you want to copy");
    				this.copyShape = null;
    				this.selected = false;
    				this.previewShape = null;
    			}
    			else
    			{
    				this.application.displayStatus("Copy created! Use the left mouse button to create copies or use the right mouse button to clear the selection");
    				Shape copy = master.copyCompleteStructure();
    				copy.getGShape().setPosition2D(lastMousePosition);
    				copy.recalculate();
    				this.objectContainer.addShape(copy);
    			}
    		}
    	}
    }

    public void mouseMoved(Vector2D position)
    {
		if (view.containsPoint(position)) {
			lastMousePosition = view.positionRelativeToView(position);
	        this.application.displayMousePosition(lastMousePosition.scale(0.1f));

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
	public void toolWasSelected() {
		this.application.displayStatus("Select the shape you want to copy");
		super.toolWasSelected();
	}

	@Override
	public void toolWasUnselected() {
		this.application.displayStatus("");
		this.copyShape = null;
		this.selected = false;
		this.previewShape = null;
		super.toolWasUnselected();
	}
}
