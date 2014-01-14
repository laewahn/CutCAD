package de.mcp.customizer.application.tools;
import java.util.List;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Tool;
//import de.mcp.customizer.model.Rectangle;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.model.SymmetricPolygon;
import de.mcp.customizer.view.Transformation2D;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public class SymmetricPolygonTool extends Tool {

	boolean isDrawing;

	Vec2D startCoord;
	SymmetricPolygon previewRectangle;
	List<Shape> shapes;

	public SymmetricPolygonTool(Rect view, Properties properties, List<Shape> shapes, Transformation2D transform)
	{
		super(view, properties, transform, "SymmetricPolygonTool");
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
		context.line(50, 10, 75, 40);
		context.line(75, 40, 100, 10);
		
		context.endDraw();

		return context;
	}

	public void mouseButtonPressed(Vec2D position, int button)
	{
		if (this.inView(position)){
			isDrawing = true;

			this.startCoord = this.positionRelativeToView(position);

			this.previewRectangle = new SymmetricPolygon(startCoord.to3DXY(), 0,0);
		}
	}

	public void mouseButtonReleased(Vec2D position, int button)
	{

		if (isDrawing && this.inView(position)) {

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
}
