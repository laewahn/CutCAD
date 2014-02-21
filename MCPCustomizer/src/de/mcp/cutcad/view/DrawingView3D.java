package de.mcp.cutcad.view;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import remixlab.proscene.Scene;
import toxi.processing.ToxiclibsSupport;
import de.mcp.cutcad.application.Grid;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.STLMesh;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;

public class DrawingView3D {
	
	private DrawingViewFrame frame;
	private PApplet application;
	
	private Scene scene;
	private Transformation transform;
	private ToxiclibsSupport gfx;
	private PGraphics3D context;
	
	private Drawable3D axes = new Axes3D();
	private Grid grid3D;

	public DrawingView3D(PGraphics3D context, DrawingViewFrame frame, Transformation transform, PApplet application) {
		this.context = context;
		this.frame = frame;
		this.transform = transform;
		this.application = application;
		
		this.grid3D = new Grid(this.transform, this.context);
		createScene();
		this.gfx = new ToxiclibsSupport(this.application, this.context);
	}
	
	private void createScene() {
		Scene scene = new Scene(this.application, this.context);
		scene.disableKeyboardHandling();
		scene.disableMouseHandling();
		scene.setGridIsDrawn(false);
		scene.setAxisIsDrawn(false);
		scene.setRadius(10000.0f);
		scene.camera().setPosition(new PVector(500, 550, 1500));
		
		this.scene = scene;
	}
	
	public void setInteractionEnabled(boolean interactionEnabled) {
		if(interactionEnabled) {
			scene.enableMouseHandling();
		} else {
			scene.disableMouseHandling();
		}
	}
	
	public boolean mouseOver(Vector2D mousePosition) {
		return mousePosition.x() > frame.origin.x() && mousePosition.x() <= frame.origin.x() + frame.size.x() 
				&& mousePosition.y() > frame.origin.y() && mousePosition.y() <= frame.origin.y() + frame.size.y();
	}
	
	public void draw(ObjectContainer container) {
		context.beginDraw();
	    
	    scene.beginDraw();

	    context.background(150);
  
	    axes.draw3D(context, transform);
	    grid3D.draw2D(context, transform);;
	    
	    for (Shape s : container.allShapes())
	    {
	      s.getGShape().draw3D(context, transform);
	    }

	    STLMesh meshSTL = container.getSTLMesh();
	    
	    if(meshSTL.isStlImported())
	    {
	    	if(meshSTL.isPosChanged())
	    	{
	    		meshSTL.center();
	    	}
	    	if(meshSTL.isRotChanged())
	    	{
	    		meshSTL.rotate();
	    	}
	    	gfx.mesh(meshSTL.getSTLMesh());
	    }
	    
	    scene.endDraw();
	    context.endDraw(); 
	    
	    application.image(context, frame.origin.x(), frame.origin.y());

	}
	
	private class Axes3D implements Drawable3D {
		@Override
		public void draw3D(PGraphics context, Transformation t) {
			context.strokeWeight(10);
			context.stroke(context.color(255, 100, 100));
			context.line(0, 0, 0, 1000, 0, 0);
			context.stroke(context.color(100, 255, 100));
			context.line(0, 0, 0, 0, 1000, 0);
			context.stroke(context.color(100, 100, 255));
			context.line(0, 0, 0, 0, 0, 1000);
			context.stroke(context.color(0, 0, 0));
			context.strokeWeight(1);
		}
		
	}
}

