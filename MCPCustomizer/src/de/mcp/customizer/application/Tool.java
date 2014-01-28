package de.mcp.customizer.application;

import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.SVGIcon;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public abstract class Tool implements Drawable2D {

    protected Rect view;
    protected Transformation transform;
    protected String iconName;
    protected ShapeButton button;
    
    protected MCPCustomizer customizer;
    protected ObjectContainer objectContainer;
    
    private float scalingFactor = 0.5f;

    public Tool(MCPCustomizer customizer, ObjectContainer container, String iconName) {
    	this(customizer.view2DRect, customizer.properties, customizer.statusbar, customizer.transform2D, iconName);
    	this.customizer = customizer;
    	this.objectContainer = container;
		PGraphics p = this.customizer.createGraphics(50, 50);
		this.button = new ShapeButton(this.getIcon(), p, transform);
    }
    
    public Tool(Rect view, Properties properties, Statusbar statusbar, Transformation transform, String iconName)
    {
        this.view = view;
        this.transform = transform;
        this.iconName = iconName;
    }
    
    public float getScalingFactor() {
    	return scalingFactor;
    }
    
    public void setScalingFactor(float factor) {
    	scalingFactor = factor;
    }

    protected Vec2D positionRelativeToView(Vec2D inPosition) 
    {
        Vec2D newPos = inPosition.sub(this.view.getTopLeft());
        newPos.set(newPos.x()/transform.getScale(), newPos.y()/transform.getScale());
        newPos.addSelf(transform.getTranslation());
        //newPos = newPos.scale(1/scalingFactor);
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

    public SVGIcon getIcon() {
    	
    	float iconScaling = 1.57f;
		SVGIcon icon = new SVGIcon(this.getIconName(), iconScaling);		
		return icon;
    }
    
    public ShapeButton getButton()
    {
    	return this.button;
    }
    
    public void wasSelected()
    {
    	this.button.setSelected(true);
    }
    
    public void wasUnselected()
    {
    	this.button.setSelected(false);
    }
    
    public void draw2D(PGraphics p, Transformation t) {};

}
