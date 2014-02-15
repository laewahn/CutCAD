package de.mcp.customizer.application;

import java.util.Timer;
import java.util.TimerTask;

import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.SVGIcon;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;

public abstract class Tool implements Drawable2D {

    protected CustomizerView view;
    protected String iconName;
    protected ShapeButton button;
    
    protected MCPCustomizer customizer;
    protected ObjectContainer objectContainer;
    
    private float scalingFactor = 0.5f;

    public Tool(MCPCustomizer customizer, ObjectContainer container, String iconName) {
    	this(customizer.customizerView2D, customizer.properties, customizer.statusbar, customizer.transform2D, iconName);
    	this.customizer = customizer;
    	this.objectContainer = container;
		PGraphics p = this.customizer.createGraphics(50, 50);
		this.button = new ShapeButton(this.getIcon(), p, customizer.customizerView2D.getTransformation());
    }
    
    public Tool(CustomizerView view, Properties properties, Statusbar statusbar, Transformation transform, String iconName)
    {
        this.view = view;
        this.iconName = iconName;
    }
    
    public float getScalingFactor() {
    	return scalingFactor;
    }
    
    public void setScalingFactor(float factor) {
    	scalingFactor = factor;
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

    public String getIconName()
    {
        return this.iconName;
    }
    
    abstract public void mouseButtonPressed(Vector2D position, int button);
    abstract public void mouseButtonReleased(Vector2D position, int button);
    abstract public void mouseMoved(Vector2D position);

	public SVGIcon getIcon() {

		float iconScaling = 1.57f;

		SVGIcon icon = new SVGIcon(this.getIconName(), iconScaling);
		return icon;
	}

	public ShapeButton getButton() {
		return this.button;
	}

	public void wasSelected() {
		this.objectContainer.setSelectedTool(this);
		this.button.setSelected(true);
		
		if(!this.canStaySelected()) {
			final Timer unselectTimer = new Timer();
			unselectTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					wasUnselected();					
				};
			}, 100);	
		}
	};

	public void wasUnselected() {
		this.button.setSelected(false);
	};

	public void draw2D(PGraphics p, Transformation transform) {
	};
	
	public boolean canStaySelected() {
		return true;
	}

}
