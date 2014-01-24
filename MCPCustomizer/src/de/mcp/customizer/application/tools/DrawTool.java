package de.mcp.customizer.application.tools;
import geomerative.RG;
import geomerative.RPoint;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Rectangle;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public class DrawTool extends Tool {
    
    boolean isDrawing;

    Vec2D startCoord;
    Rectangle previewRectangle;
    List<Shape> shapes;

    public DrawTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes, Transformation transform)
    {
        super(view, properties, statusbar, transform, "DrawTool");
        this.isDrawing = false;
        this.shapes = shapes;
    }

    public PGraphics getIcon(PGraphics context) 
    {
		float iconScaling = 1.57f;
		RPoint[][] pointPaths;
		
		context.beginDraw();
		context.fill(0);
		context.strokeWeight(1);

		Path path = Paths.get(ImportSVGTool.class.getProtectionDomain().getCodeSource().getLocation().toString());
		pointPaths = RG.loadShape(path.getParent() + "/icons/DrawRectangle.svg").getPointsInPaths();
 
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
//        context.endDraw();
//        
//        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        if (this.inView(position)){
    		this.displayStatus("Use the mouse to drag the rectangle to the size that you want");
            isDrawing = true;
            
            this.startCoord = this.positionRelativeToView(position);
            
            this.previewRectangle = new Rectangle(startCoord.to3DXY(), 0,0);
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {

        if (isDrawing && this.inView(position)) {

    		this.displayStatus("Rectangle created! If you want to add another rectangle, click and hold the left mousebutton anywhere on the 2D view");
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
		this.displayStatus("To draw a rectangle, click and hold the left mousebutton anywhere on the 2D view");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		super.wasUnselected();
	}
    
    
}
