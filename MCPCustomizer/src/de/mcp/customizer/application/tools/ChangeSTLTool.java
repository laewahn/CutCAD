package de.mcp.customizer.application.tools;

import java.io.File;

import geomerative.RG;
import geomerative.RPoint;

import processing.core.PGraphics;
import toxi.geom.Vec2D;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;

public class ChangeSTLTool extends Tool {

	private STLMesh mesh;

	public ChangeSTLTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container, "ChangeSTLTool");
		this.mesh = customizer.meshSTL;
	}
	
//	public ChangeSTLTool(Rect view, Properties properties, Statusbar statusbar, STLMesh mesh, Transformation transform) {
//		super(view, properties, statusbar, transform, "ImportSTLTool");
//		this.mesh = mesh;
//	}

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

		pointPaths = RG.loadShape("icons" + File.separator + "MoveSTL.svg").getPointsInPaths();
 
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
//		context.fill(0);
//		context.noStroke();
//		context.strokeWeight(1);
//		context.textSize(12);
//		context.text("change", 5, 15);
//		context.text("STL", 12, 30);
//		context.text("model", 5, 45);
//		context.endDraw();
//		return context;
	}

	@Override
	public void draw2D(PGraphics p) {
	}
	
	@Override
	public void wasSelected() {
		if(mesh.isStlImported())
		{
			this.customizer.properties.show();
			this.customizer.properties.plugTo(mesh);
		}
	}
	
	@Override
	public void wasUnselected() {
		super.wasUnselected();
	}
}
