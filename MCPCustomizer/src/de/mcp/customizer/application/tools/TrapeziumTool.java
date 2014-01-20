package de.mcp.customizer.application.tools;
import java.util.List;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.model.Trapezium;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public class TrapeziumTool extends Tool {

	boolean isDrawing;

	Vec2D startCoord;
	Trapezium previewRectangle;
	List<Shape> shapes;

	public TrapeziumTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes, Transformation transform)
	{
		super(view, properties, statusbar, transform, "TrapeziumTool");
		this.isDrawing = false;
		this.shapes = shapes;
	}

	public PGraphics getIcon(PGraphics context) 
	{
		context.beginDraw();
		context.noFill();
		context.stroke(0);
		context.strokeWeight(2);
		context.line(50, 10, 100, 10);
		context.line(50, 10, 60, 40);
		context.line(100, 10, 90, 40);
		context.line(60, 40, 90, 40);
		context.endDraw();

		return context;
	}

	public void mouseButtonPressed(Vec2D position, int button)
	{
		if (this.inView(position)){
    		this.displayStatus("Use the mouse to drag the trapezoid to the size that you want");
			isDrawing = true;

			this.startCoord = this.positionRelativeToView(position);

			this.previewRectangle = new Trapezium(startCoord.to3DXY(), 0,0);
		}
	}

	public void mouseButtonReleased(Vec2D position, int button)
	{

		if (isDrawing && this.inView(position)) {

    		this.displayStatus("Trapezoid created! If you want to add another rectangle, click and hold the left mousebutton anywhere on the 2D view");

			Vec2D endCoord = this.positionRelativeToView(position);
			Vec2D rectSize = endCoord.sub(this.startCoord);

			this.previewRectangle.setSize(rectSize);

			shapes.add(this.previewRectangle);
			this.previewRectangle = null;

			isDrawing = false;
		}
	}

	public void mouseMoved(Vec2D position)
	{
        Vec2D relativePosition = this.positionRelativeToView(position);
        this.updateMousePositon(relativePosition.scale(0.1f));
        
		if (isDrawing){

			Vec2D endCoord = this.positionRelativeToView(position);
			Vec2D rectSize = endCoord.sub(this.startCoord);

			this.previewRectangle.setSize(rectSize);
		}

	}

	public void draw2D(PGraphics p)
	{
		if (this.previewRectangle != null) {
			this.previewRectangle.getShape().draw2D(p);
		}
	}

	@Override
	public void wasSelected() {
		this.displayStatus("To draw a trapezoid, click and hold the left mousebutton anywhere on the 2D view");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		super.wasUnselected();
	}
}
