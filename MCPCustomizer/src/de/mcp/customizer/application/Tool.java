package de.mcp.customizer.application;

import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.SVGIcon;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;
import toxi.geom.Vec2D;

public abstract class Tool implements Drawable2D {

    protected CustomizerView view;
    protected String iconName;
    
    protected MCPCustomizer customizer;
    protected ObjectContainer objectContainer;
    
    private float scalingFactor = 0.5f;
    
    public float getScalingFactor() {
    	return scalingFactor;
    }
    
    public void setScalingFactor(float factor) {
    	scalingFactor = factor;
    }

    public Tool(MCPCustomizer customizer, ObjectContainer container, String iconName) {
    	this(customizer.customizerView2D, customizer.properties, customizer.statusbar, customizer.transform2D, iconName);
    	this.customizer = customizer;
    	this.objectContainer = container;
    }
    
    public Tool(CustomizerView view, Properties properties, Statusbar statusbar, Transformation transform, String iconName)
    {
        this.view = view;
        this.iconName = iconName;
    }

    protected Vec2D positionRelativeToView(Vec2D inPosition) 
    {
        Vec2D newPos = inPosition.sub(this.view.getOrigin());
        newPos.set(newPos.x()/this.view.getTransformation().getScale(), newPos.y()/this.view.getTransformation().getScale());
        newPos.addSelf(this.view.getTransformation().getTranslation());
        newPos = newPos.scale(1/scalingFactor);
        return newPos;
    }

    protected boolean inView(Vec2D position) 
    {
        return this.view.containsPoint(position);
    }

    public String getIconName()
    {
        return this.iconName;
    }
    
    abstract public void mouseButtonPressed(Vec2D position, int button);
    abstract public void mouseButtonReleased(Vec2D position, int button);
    abstract public void mouseMoved(Vec2D position);

    public PGraphics getIcon(PGraphics context) {
    	
    	float iconScaling = 1.57f;
		SVGIcon icon = new SVGIcon(this.getIconName(), iconScaling);
		icon.draw2D(context, new Transformation((float) 1.0, new Vec2D()));
		
		return context;
    }
    
    public void wasSelected(){
    	this.objectContainer.setSelectedTool(this);
    };
    
    public void wasUnselected(){
    };
    
    public void draw2D(PGraphics p, Transformation transform) {};

}
