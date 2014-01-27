package de.mcp.customizer.application.tools;

import processing.core.PGraphics;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.SymmetricPolygon;

public class SymmetricPolygonTool extends Tool {

	boolean isDrawing;

	Vec2D startCoord;
	SymmetricPolygon previewRectangle;

	public SymmetricPolygonTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "DrawSymmetricPolygon.svg");
		this.isDrawing = false;
	}

	public void mouseButtonPressed(Vec2D position, int button)
	{
		if (this.inView(position)){
    		this.customizer.displayStatus("Use the mouse to drag the symmetric polygon to the size that you want");
			isDrawing = true;

			this.startCoord = this.positionRelativeToView(position);

			this.previewRectangle = new SymmetricPolygon(startCoord.to3DXY(), 0,0);
		}
	}

	public void mouseButtonReleased(Vec2D position, int button)
	{

		if (isDrawing && this.inView(position)) {
    		this.customizer.displayStatus("Symmetric polygon created! If you want to add another rectangle, click and hold the left mousebutton anywhere on the 2D view");

			Vec2D endCoord = this.positionRelativeToView(position);
			Vec2D rectSize = endCoord.sub(this.startCoord);

			this.previewRectangle.setSize(rectSize);

//			shapes.add(this.previewRectangle);
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

	public void draw2D(PGraphics p)
	{
		if (this.previewRectangle != null) {
			this.previewRectangle.getGShape().draw2D(p);
		}
	}

	@Override
	public void wasSelected() {
		this.customizer.displayStatus("To draw a symmetric polygon, click and hold the left mousebutton anywhere on the 2D view");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.customizer.displayStatus("");
		super.wasUnselected();
	}
	
	
}
