package de.mcp.customizer.view;

import java.util.List;

import processing.core.PGraphics;
import de.mcp.customizer.application.Grid;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.model.primitives.Vector2D;

public class DrawingView2D {
	
	private MCPCustomizer application;
	private PGraphics context;
	private DrawingViewFrame frame;
	
	private Transformation transform;
	private Grid grid;
	private Drawable2D axes = new Axes2D();
	
	public DrawingView2D(PGraphics context, DrawingViewFrame frame, Grid grid, Transformation transform, MCPCustomizer application) {
		this.context = context;
		this.frame = frame;
		this.grid = grid;
		
		this.transform = transform;
		this.application = application;
	}
	
	public void applyTransformation(Transformation transform) {
		context.scale(transform.getScale());
        context.translate(-transform.getTranslation().x(), -transform.getTranslation().y());
	}
	
	public void draw(List<Drawable2D> drawables) {
		context.beginDraw();
		context.background(150);
		
		applyTransformation(this.transform);

		drawables.add(axes);
		drawables.add((Drawable2D) grid);
		
		for(Drawable2D d : drawables) {
			d.draw2D(context, this.transform);
		}

		context.endDraw();

		application.image(context, frame.origin.x(), frame.origin.y());
	}
	
	public Vector2D getOrigin() {
		return this.frame.origin;
	}
	
	public boolean containsPoint(Vector2D point) {
		return this.frame.containsPoint(point);
	}
	
	public Transformation getTransformation() {
		return this.transform;
	}
	
	private class Axes2D implements Drawable2D {
		@Override
		public void draw2D(PGraphics context, Transformation transform) {
			context.strokeWeight(10);
			context.stroke(context.color(255, 100, 100));
			context.line(0, 0, 800, 0);
			context.stroke(context.color(100, 255, 100));
			context.line(0, 0, 0, 800);
			context.stroke(context.color(0, 0, 0));
			context.strokeWeight(1);
		}
	}

}
