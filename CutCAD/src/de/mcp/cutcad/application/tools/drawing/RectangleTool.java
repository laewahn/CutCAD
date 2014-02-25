package de.mcp.cutcad.application.tools.drawing;

import processing.core.PGraphics;
import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.model.shapes.Rectangle;
import de.mcp.cutcad.view.Transformation;

/**
 * The DrawTool is used to draw a rectangle-shape.
 */
public class RectangleTool extends Tool {
    
    boolean isDrawing;

    Vector2D startCoord;
    Rectangle previewRectangle;

    /**
     * @param application the main class of the project
     * @param container the currently loaded ObjectContainer
     */
    public RectangleTool(CutCADApplet application, ObjectContainer container) {
    	super(application, container);
    	this.isDrawing = false;
    }

    @Override
	public String getIconName() {
		return "DrawRectangle.svg";
	}

    public void mouseButtonPressed(Vector2D position, int button)
    {
        if (view.containsPoint(position)){
    		this.application.displayStatus("Use the mouse to drag the rectangle to the size that you want");
            isDrawing = true;
            
            this.startCoord = view.positionRelativeToView(position);
            
            this.previewRectangle = new Rectangle(startCoord.to3DXY(), 0,0);
        }
    }

    public void mouseButtonReleased(Vector2D position, int button)
    {
        if (isDrawing && view.containsPoint(position)) {
        	if (view.positionRelativeToView(position).equals(this.startCoord))
        	{
                this.previewRectangle = null;
                isDrawing = false;
        	}
        	else
        	{
        		this.application.displayStatus("Rectangle created! If you want to add another rectangle, click and hold the left mousebutton anywhere on the 2D view");
                Vector2D endCoord = view.positionRelativeToView(position);
                Vector2D rectSize = endCoord.sub(this.startCoord);
                
                this.previewRectangle.setSize(rectSize);

                this.objectContainer.addShape(this.previewRectangle);
                this.previewRectangle = null;

                isDrawing = false;
        	}
        }
    }

    public void mouseMoved(Vector2D position)
    {
        Vector2D relativePosition = view.positionRelativeToView(position);
        this.application.displayMousePosition(relativePosition.scale(0.1f));
        if (isDrawing){

            Vector2D endCoord = view.positionRelativeToView(position);
            Vector2D rectSize = endCoord.sub(this.startCoord);

            this.previewRectangle.setSize(rectSize);
        }

    }

    public void draw2D(PGraphics p, Transformation t)
    {
        if (this.previewRectangle != null) {
            this.previewRectangle.getGShape().draw2D(p, t);
        }
    }

	@Override
	public void toolWasSelected() {
		this.application.displayStatus("To draw a rectangle, click and hold the left mousebutton anywhere on the 2D view");
		super.toolWasSelected();
	}

	@Override
	public void toolWasUnselected() {
		this.application.displayStatus("");
		super.toolWasUnselected();
	}
    
    
}
