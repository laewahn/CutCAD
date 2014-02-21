package de.mcp.customizer.view;

import java.util.List;

import processing.core.PGraphics;
import de.mcp.customizer.application.Grid;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;

public class DrawingView2D {
	
	private MCPCustomizer application;
	private PGraphics context;
	private DrawingViewFrame frame;
	
	private Transformation transform;
	private Grid grid;
	private Drawable2D axes = new Axes2D();
	
	public DrawingView2D(PGraphics context, DrawingViewFrame frame, Transformation transform, MCPCustomizer application) {
		this.context = context;
		this.frame = frame;
		this.transform = transform;
		this.application = application;
		
		this.grid = new Grid(transform, context);
	}
	
	public void applyTransformation(Transformation transform) {
		context.scale(transform.getScale());
        context.translate(-transform.getTranslation().x(), -transform.getTranslation().y());
	}
	
	public void draw(ObjectContainer container) {
		context.beginDraw();
		context.background(150);
		
		applyTransformation(this.transform);
		
		List<Drawable2D> allDrawables = container.allDrawables();
		allDrawables.add(axes);
		allDrawables.add((Drawable2D) grid);
		
		for(Drawable2D d : allDrawables) {
			if(d instanceof Drawable2D) { 
				d.draw2D(context, this.transform);				
			}
		}

		context.endDraw();

		application.image(context, frame.origin.x(), frame.origin.y());
	}
	
	public Vector2D getOrigin() {
		return this.frame.origin;
	}
	
	public Vector2D positionRelativeToView(Vector2D inPosition) 
    {
        Vector2D newPos = inPosition.sub(this.getOrigin());
        newPos.set(newPos.x()/this.getTransformation().getScale(), newPos.y()/this.getTransformation().getScale());
        newPos.addSelf(this.getTransformation().getTranslation());

        newPos = newPos.scale(1/this.getTransformation().getScale());
       
        return newPos;
    }

	public boolean containsPoint(Vector2D point) {
		return this.frame.containsPoint(point);
	}
	
	public Transformation getTransformation() {
		return this.transform;
	}
	
	public boolean mouseOver(Vector2D mousePosition) {
		return mousePosition.x() > frame.origin.x() && mousePosition.x() <= frame.origin.x() + frame.size.x()
				&& mousePosition.x() > frame.origin.x() && mousePosition.y() <= frame.origin.y() + frame.size.y();
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
