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

    
    public ShapeButton getButton() {
		return this.button;
	}
    
	private SVGIcon getIcon() {

		float iconScaling = 1.57f;

		SVGIcon icon = new SVGIcon(this.getIconName(), iconScaling);
		return icon;
	}	
}
