package de.mcp.customizer.application.tools.objectManipulation;

import processing.core.PConstants;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Cutout;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;

/**
 * The DeleteTool is used to Delete a Shape, Connection or Cutout
 */
public class DeleteTool extends Tool {
    
    boolean dragging;
    Vector2D originalMousePosition;
    
    /**
     * @param customizer the main class of the project
     * @param container the currently loaded ObjectContainer
     */
    public DeleteTool(MCPCustomizer customizer, ObjectContainer container) {
    	super(customizer, container, "Delete.svg");
    	
        this.dragging = false;
        this.originalMousePosition = new Vector2D(0,0);
    }
   
    public void mouseButtonPressed(Vector2D position, int button)
    {
        for(Shape s : this.objectContainer.allShapes()) {
        	if (this.inView(position) && s.getGShape().isSelected() && button == PConstants.LEFT)
            {
                removeConnectionsContaining(s);
                removeCutoutsContaining(s);
                this.objectContainer.removeShape(s);
            }
        }
                
        for(Connection c : this.objectContainer.allConnections()) {
        	if (this.inView(position) && c.isSelected() && button == PConstants.LEFT)
            {
                c.undoConnection();
                this.objectContainer.removeConnection(c);
            }
        }
        
        for(Cutout o : this.objectContainer.allCutouts()) {
        	if (this.inView(position) && o.isSelected() && button == PConstants.LEFT)
            {
                o.removeCutout();
            }
        }        
    }

    private void removeConnectionsContaining(Shape s)
    {
    	for(Connection c : this.objectContainer.allConnections()) {
    		boolean shapeIsParentOfMasterEdge = c.getMasterEdge().getGShape().getShape().equals(s);
            boolean shapeIsParentOfSlaveEdge = c.getSlaveEdge().getGShape().getShape().equals(s);
            if (shapeIsParentOfMasterEdge || shapeIsParentOfSlaveEdge)
            {
                c.undoConnection();
                this.objectContainer.removeConnection(c);
            }
    	}
    }
    
    private void removeCutoutsContaining(Shape s)
    {
    	for(Cutout c : this.objectContainer.allCutouts()) {
    		if (c.getMasterShape() == s || c.getSlaveShape() == s)
            {
            	c.removeCutout();
            }
    	}        
    }

    public void mouseButtonReleased(Vector2D position, int button)
    {
        // nothing to do here
    }
    
    public void mouseMoved(Vector2D position)
    {
        if (this.inView(position))
        {
            Vector2D relativePosition = this.positionRelativeToView(position);
	        this.customizer.displayMousePosition(relativePosition.scale(0.1f));


            for (Shape s : this.objectContainer.allShapes()) {
                s.getGShape().setSelected(s.getGShape().mouseOver(relativePosition));
            }
            for (Connection c : this.objectContainer.allConnections()) {
                c.setSelected(c.mouseOver(relativePosition));
            }
            for (Cutout c : this.objectContainer.allCutouts()) {
                c.setSelected(c.mouseOver(relativePosition));
            }
        }
    }

	@Override
	public void wasSelected() {
		this.customizer.displayStatus("Click on a shape or connection to delete it");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.customizer.displayStatus("");
		super.wasUnselected();
	}
    
}
