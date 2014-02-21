package de.mcp.cutcad.view;

import java.io.File;

import geomerative.RG;
import geomerative.RPoint;
import processing.core.PGraphics;

public class SVGIcon implements Drawable2D {
	
	private float scaling;
	private String name;
	
	private RPoint[][] svgPointPaths;
	
	public SVGIcon(String name, float scaling) {
		this.name = name;
		this.scaling = scaling;
	}
	
	
	
	@Override
	public void draw2D(PGraphics context, Transformation t) {
		
		context.beginDraw();
		context.fill(0);
		context.strokeWeight(1);
		
		if(this.svgPointPaths == null) {
			this.loadSVGPointPaths();
		}
		
		for(RPoint[] pointPath : this.svgPointPaths) {
			if(pointPath != null) {
				this.drawPointPath(pointPath, context);
			}
		}
		
		context.endDraw();
	}
	
	private void loadSVGPointPaths() {
		this.svgPointPaths = RG.loadShape("icons" + File.separator + this.name).getPointsInPaths();
	}
	
	private void drawPointPath(RPoint[] pointPath, PGraphics context) {
		context.beginShape();
		
		for(RPoint vertex : pointPath) {
			context.vertex(vertex.x * this.scaling,
					vertex.y * this.scaling);
		}
		
		context.endShape();
	}
}
