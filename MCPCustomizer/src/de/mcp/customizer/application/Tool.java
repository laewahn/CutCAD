package de.mcp.customizer.application;

import java.util.Timer;
import java.util.TimerTask;

import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.DrawingView2D;
import de.mcp.customizer.view.SVGIcon;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;

public abstract class Tool implements Drawable2D {

    protected DrawingView2D view;
    protected ShapeButton button;
    
    protected MCPCustomizer customizer;
    protected ObjectContainer objectContainer;
    
    public Tool(MCPCustomizer customizer, ObjectContainer container) {
 
    	this.view = customizer.drawingView2D;
    	this.customizer = customizer;
    	this.objectContainer = container;
    	
		PGraphics p = this.customizer.createGraphics(50, 50);
		this.button = new ShapeButton(this.getIcon(), p, this.view.getTransformation());
    }
    
    public abstract String getIconName();
    
    public void mouseButtonPressed(Vector2D position, int button) {};
    public void mouseButtonReleased(Vector2D position, int button) {};
    public void mouseMoved(Vector2D position) {};

	public void toolWasSelected() {
		this.objectContainer.setSelectedTool(this);
		this.button.setSelected(true);
		
		if(!this.canStaySelected()) {
			final Timer unselectTimer = new Timer();
			unselectTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					toolWasUnselected();					
				};
			}, 100);	
		}
	};

	public void toolWasUnselected() {
		this.button.setSelected(false);
	};

	public void draw2D(PGraphics p, Transformation transform) {};	
	
	public boolean canStaySelected() {
		return true;
	}

    protected Vector2D positionRelativeToView(Vector2D inPosition) 
    {
        Vector2D newPos = inPosition.sub(this.view.getOrigin());
        newPos.set(newPos.x()/this.view.getTransformation().getScale(), newPos.y()/this.view.getTransformation().getScale());
        newPos.addSelf(this.view.getTransformation().getTranslation());

        newPos = newPos.scale(1/this.view.getTransformation().getScale());
       
        return newPos;
    }

    protected boolean inView(Vector2D position) 
    {
        return this.view.containsPoint(position);
    }

    public ShapeButton getButton() {
		return this.button;
	}
    
	private SVGIcon getIcon() {

		float iconScaling = 1.57f;

		SVGIcon icon = new SVGIcon(this.getIconName(), iconScaling);
		return icon;
	}	
}
