package de.mcp.customizer.application.tools;
import geomerative.RG;
import geomerative.RPoint;

import java.io.File;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Edge;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;


public class CutoutTool extends Tool {

    List<Shape> shapes;
    List<Connection> connections;
    boolean dragging;
    boolean selectedFirst;
    Vec2D originalMousePosition;
    Vec2D relativePosition;
    Shape masterShape;
    private float scalingFactor = 0.5f;
    
    public CutoutTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes, List<Connection> connections, Transformation transform) 
    {
        super(view, properties, statusbar, transform, "CutoutTool");
        
        this.shapes = shapes;
        this.connections = connections;
        this.dragging = false;
        this.selectedFirst = false;
        this.originalMousePosition = new Vec2D(0,0);
    }

    public PGraphics getIcon(PGraphics context)
    {
		float iconScaling = 1.57f;
		RPoint[][] pointPaths;
		
		context.beginDraw();
		context.fill(0);
		context.strokeWeight(1);

		pointPaths = RG.loadShape("icons" + File.separator + "Cutout.svg").getPointsInPaths();
 
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
//        context.beginDraw();
//        context.noFill();
//        context.stroke(0);
//        context.strokeWeight(1);
//        context.rect(5, 5, 40, 40);
//        context.rect(20, 20, 10, 10);
//        context.endDraw();
//
//        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        for (Shape s : shapes)
        {
            if (this.inView(position) && button == PConstants.LEFT)
            {
            	if (!selectedFirst && s.getShape().isSelected() )
            	{
            		this.displayStatus("Now select the shape you want to add as a cutout");
            		s.getShape().setSelected(true);
            		Vec2D currentMousePosition = this.positionRelativeToView(position);
                    this.originalMousePosition.set(currentMousePosition);
            		masterShape = s;
            		selectedFirst = true;
            	}
            	else if (selectedFirst && s.getShape().isSelected() )
            	{
            		this.displayStatus("Cutout created! If you want to create another cutout, select the shape you want to add a cutout to");
            		masterShape.getShape().addCutout(s.getShape());
            		selectedFirst = false;
            	}
            }
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        if (button == PConstants.RIGHT) {
            this.dragging = false;
        }
    }
    
    public void mouseMoved(Vec2D position)
    {			
        if (this.inView(position))
        {
            relativePosition = this.positionRelativeToView(position);
	        this.updateMousePositon(relativePosition.scale(0.1f));

            for (Shape s : shapes) {
                s.getShape().setSelected(s.getShape().mouseOver(relativePosition));
            }
        }
    }
    
    public void draw2D(PGraphics p)
    {
        if (selectedFirst) {	
    		Polygon2D findCenter = new Polygon2D();
    		for (Edge e : masterShape.getShape().getEdges()) findCenter.add(e.getV1().copy());
    		Vec2D center = findCenter.getCentroid();
            Vec2D mid = center.add(masterShape.getShape().getPosition2D());
            p.stroke(255,0,0);
            Vec2D lineStart = mid.scale(scalingFactor);
            Vec2D lineEnd = this.relativePosition.scale(scalingFactor);
            p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
            p.stroke(0);
        }
    }

	@Override
	public void wasSelected() {
		this.displayStatus("First, select the shape you want to add a cutout to");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		selectedFirst = false;
		super.wasUnselected();
	}
    
    
}
