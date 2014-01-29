package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.Trapezium;
import de.mcp.customizer.view.Transformation;

/**
 * The TrapeziumTool is used to draw a trapezium-shape (different top and bottom length, same side length and vertical symmetric).
 */
public class TrapeziumTool extends Tool {

	boolean isDrawing;

	Vec2D startCoord;
	Trapezium previewRectangle;

    /**
     * @param customizer the main class of the project
     * @param container the currently loaded ObjectContainer
     */
	public TrapeziumTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "DrawTrapezium.svg");
		
		this.isDrawing = false;
	}
	
	public void mouseButtonPressed(Vec2D position, int button)
	{
		if (this.inView(position)){
    		this.customizer.displayStatus("Use the mouse to drag the trapezoid to the size that you want");
			isDrawing = true;

			this.startCoord = this.positionRelativeToView(position);

			this.previewRectangle = new Trapezium(startCoord.to3DXY(), 0,0);
		}
	}

	public void mouseButtonReleased(Vec2D position, int button)
	{

		if (isDrawing && this.inView(position)) {

    		this.customizer.displayStatus("Trapezoid created! If you want to add another rectangle, click and hold the left mousebutton anywhere on the 2D view");

			//Vec2D endCoord = this.positionRelativeToView(position);
			//Vec2D rectSize = endCoord.sub(this.startCoord);

			//this.previewRectangle.setSize(rectSize);

			this.objectContainer.addShape(this.previewRectangle);
			this.previewRectangle = null;

			isDrawing = false;
		}
	}

	public void mouseMoved(Vec2D position)
	{
        Vec2D relativePosition = this.positionRelativeToView(position);
        this.customizer.displayMousePosition(relativePosition.scale(0.1f));
        
		if (isDrawing){

			Vec2D endCoord = this.positionRelativeToView(position);
			Vec2D rectSize = endCoord.sub(this.startCoord);

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
	public void wasSelected() {
		this.customizer.displayStatus("To draw a trapezoid, click and hold the left mousebutton anywhere on the 2D view");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.customizer.displayStatus("");
		super.wasUnselected();
	}
}
