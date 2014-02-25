package de.mcp.cutcad.application.tools.objectManipulation;

import processing.core.PConstants;
import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.Connection;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.primitives.Cutout;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;

/**
 * The DeleteTool is used to Delete a Shape, Connection or Cutout
 */
public class DeleteTool extends Tool {
    
    boolean dragging;
    Vector2D originalMousePosition;
    
    /**
     * @param application the main class of the project
     * @param container the currently loaded ObjectContainer
     */
    public DeleteTool(CutCADApplet application, ObjectContainer container) {
    	super(application, container);
    	
        this.dragging = false;
        this.originalMousePosition = new Vector2D(0,0);
    }
   
    @Override
	public String getIconName() {
		return "Delete.svg";
	}
    
    public void mouseButtonPressed(Vector2D position, int button)
    {
        for(Shape s : this.objectContainer.allShapes()) {
        	if (view.containsPoint(position) && s.getGShape().isSelected() && button == PConstants.LEFT)
            {
                removeConnectionsContaining(s);
                removeCutoutsContaining(s);
                this.objectContainer.removeShape(s);
            }
        }
                
        for(Connection c : this.objectContainer.allConnections()) {
        	if (view.containsPoint(position) && c.isSelected() && button == PConstants.LEFT)
            {
                c.undoConnection();
                this.objectContainer.removeConnection(c);
            }
        }
        
        for(Cutout o : this.objectContainer.allCutouts()) {
        	if (view.containsPoint(position) && o.isSelected() && button == PConstants.LEFT)
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

    public void mouseMoved(Vector2D position)
    {
        if (view.containsPoint(position))
        {
            Vector2D relativePosition = view.positionRelativeToView(position);
	        this.application.displayMousePosition(relativePosition.scale(0.1f));


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
	public void toolWasSelected() {
		this.application.displayStatus("Click on a shape or connection to delete it");
		super.toolWasSelected();
	}

	@Override
	public void toolWasUnselected() {
		this.application.displayStatus("");
		super.toolWasUnselected();
	}
    
}
