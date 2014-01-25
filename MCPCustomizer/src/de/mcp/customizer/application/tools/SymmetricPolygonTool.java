package de.mcp.customizer.application.tools;
import geomerative.RG;
import geomerative.RPoint;

import java.io.File;
import java.util.List;

import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

import de.mcp.customizer.application.MCPCustomizer;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;

import de.mcp.customizer.model.Shape;
import de.mcp.customizer.model.SymmetricPolygon;
import de.mcp.customizer.view.Transformation;

public class SymmetricPolygonTool extends Tool {

	boolean isDrawing;

	Vec2D startCoord;
	SymmetricPolygon previewRectangle;
//	List<Shape> shapes;

	public SymmetricPolygonTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "SymmetricPolygonTool");
		this.isDrawing = false;
	}
	
//	public SymmetricPolygonTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes, Transformation transform)
//	{
//		super(view, properties, statusbar, transform, "SymmetricPolygonTool");
//		this.isDrawing = false;
//		this.shapes = shapes;
//	}

	public PGraphics getIcon(PGraphics context) 
	{
		float iconScaling = 1.57f;
		RPoint[][] pointPaths;
		
		context.beginDraw();
		context.fill(0);
		context.strokeWeight(1);

		pointPaths = RG.loadShape("icons" + File.separator + "DrawSymmetricPolygon.svg").getPointsInPaths();
 
		for(int i = 0; i<pointPaths.length; i++){
		    if (pointPaths[i] != null) {
		    	context.beginShape();
		      for(int j = 0; j<pointPaths[i].length; j++){
		    	  context.vertex(pointPaths[i][j].x*iconScaling, pointPaths[i][j].y*iconScaling);
		      }
		      context.endShape();
		    }
		  }
		context.endDraw();
		return context;
//		context.beginDraw();
//		context.noFill();
//		context.stroke(0);
//		context.strokeWeight(1);
//		
//		context.line(5, 20, 20, 20);
//		context.line(5, 20, 12.5f, 5);
//		context.line(20, 20, 12.5f, 5);
//		
//		context.rect(27.5f,5,15,15);
//		
//		context.line(8,45,16,45);
//		context.line(8,45,4,36);
//		context.line(16,45,20,36);
//		context.line(4,36,12,27);
//		context.line(20,36,12,27);
//		
//		context.line(30, 45, 40, 45);
//		context.line(30, 45, 25, 36);
//		context.line(25, 36, 30, 27);
//		context.line(30, 27, 40, 27);
//		context.line(40, 45, 45, 36);
//		context.line(45, 36, 40, 27);
//		
//		
////		context.line(50, 10, 100, 10);
////		context.line(50, 10, 75, 40);
////		context.line(75, 40, 100, 10);
//		
//		context.endDraw();
//
//		return context;
	}

	public void mouseButtonPressed(Vec2D position, int button)
	{
		if (this.inView(position)){
    		this.displayStatus("Use the mouse to drag the symmetric polygon to the size that you want");
			isDrawing = true;

			this.startCoord = this.positionRelativeToView(position);

			this.previewRectangle = new SymmetricPolygon(startCoord.to3DXY(), 0,0);
		}
	}

	public void mouseButtonReleased(Vec2D position, int button)
	{

		if (isDrawing && this.inView(position)) {
    		this.displayStatus("Symmetric polygon created! If you want to add another rectangle, click and hold the left mousebutton anywhere on the 2D view");

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
		this.displayStatus("To draw a symmetric polygon, click and hold the left mousebutton anywhere on the 2D view");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		super.wasUnselected();
	}
	
	
}
