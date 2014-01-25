package de.mcp.customizer.application.tools;

import geomerative.RG;
import geomerative.RPoint;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ImportSVG;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;

public class ImportSVGTool extends Tool {

	private List<Shape> shapes;

	public ImportSVGTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes,
			Transformation transform) {
		super(view, properties, statusbar, transform, "ImportSVGTool");
		this.shapes = shapes;
	}

	@Override
	public void mouseButtonPressed(Vec2D position, int button) {

	}

	@Override
	public void mouseButtonReleased(Vec2D position, int button) {
	}
	
	@Override
	public void mouseMoved(Vec2D position) {
	}

	@Override
	public PGraphics getIcon(PGraphics context) {
		float iconScaling = 1.57f;
		RPoint[][] pointPaths;
		
		context.beginDraw();
		context.fill(0);
		context.strokeWeight(1);

		Path path = Paths.get(ImportSVGTool.class.getProtectionDomain().getCodeSource().getLocation().toString().replace("file:/",""));
		pointPaths = RG.loadShape(path.getParent() + "/icons/LoadSVG.svg").getPointsInPaths();
 
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
	}
	
	@Override
	public void draw2D(PGraphics p) {
	}

	/*private void drawCloseRect(PGraphics p) {
	}*/
	
	@Override
	public void wasSelected() {
		new ImportSVG(shapes);	
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
