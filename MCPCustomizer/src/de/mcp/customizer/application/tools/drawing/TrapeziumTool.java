package de.mcp.customizer.application.tools.drawing;

import processing.core.PGraphics;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.model.shapes.Trapezium;
import de.mcp.customizer.view.Transformation;

/**
 * The TrapeziumTool is used to draw a trapezium-shape (different top and bottom length, same side length and vertical symmetric).
 */
public class TrapeziumTool extends Tool {

	boolean isDrawing;

	Vector2D startCoord;
	Trapezium previewRectangle;

    /**
     * @param customizer the main class of the project
     * @param container the currently loaded ObjectContainer
     */
	public TrapeziumTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container);
		
		this.isDrawing = false;
	}
	
	@Override
	public String getIconName() {
		return "DrawTrapezium.svg";
	}
	
	public void mouseButtonPressed(Vector2D position, int button)
	{
		if (view.containsPoint(position)){
			
    		this.customizer.displayStatus("Use the mouse to drag the trapezoid to the size that you want");
			isDrawing = true;

			this.startCoord = view.positionRelativeToView(position);

			this.previewRectangle = new Trapezium(startCoord.to3DXY(), 0,0);
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
	    		this.customizer.displayStatus("Trapezoid created! If you want to add another rectangle, click and hold the left mousebutton anywhere on the 2D view");
	
				this.objectContainer.addShape(this.previewRectangle);
				this.previewRectangle = null;
	
				isDrawing = false;
        	}
		}
	}

	public void mouseMoved(Vector2D position)
	{
        Vector2D relativePosition = view.positionRelativeToView(position);
        this.customizer.displayMousePosition(relativePosition.scale(0.1f));
        
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
		this.customizer.displayStatus("To draw a trapezoid, click and hold the left mousebutton anywhere on the 2D view");
		super.toolWasSelected();
	}

	@Override
	public void toolWasUnselected() {
		this.customizer.displayStatus("");
		super.toolWasUnselected();
	}
}
