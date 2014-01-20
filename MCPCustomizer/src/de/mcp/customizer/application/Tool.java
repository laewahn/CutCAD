package de.mcp.customizer.application;

import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public abstract class Tool implements Drawable2D {

    protected Properties properties;
    protected Statusbar statusbar;
    protected Rect view;
    protected Transformation transform;
    protected String name;

    public Tool(Rect view, Properties properties, Statusbar statusbar, Transformation transform, String name)
    {
        this.view = view;
        this.properties = properties;
        this.statusbar = statusbar;
        this.transform = transform;
        this.name = name;
    }

    protected Vec2D positionRelativeToView(Vec2D inPosition) 
    {
        Vec2D newPos = inPosition.sub(this.view.getTopLeft());
        newPos.set(newPos.x()/transform.getScale(), newPos.y()/transform.getScale());
        newPos.addSelf(transform.getTranslation());
        return newPos;
    }

    protected boolean inView(Vec2D position) 
    {
        return this.view.containsPoint(position);
    }

    public String getName()
    {
        return this.name;
    }
    
    public void displayStatus(String message)
    {
    	this.statusbar.setStatus(message);
    }
    
    public void updateMousePositon(Vec2D position)
    {
    	this.statusbar.setMousePosition(position);
    }
    
    abstract public void mouseButtonPressed(Vec2D position, int button);
    abstract public void mouseButtonReleased(Vec2D position, int button);
    abstract public void mouseMoved(Vec2D position);

    abstract public PGraphics getIcon(PGraphics context);
    
    public void wasSelected(){};
    public void wasUnselected(){};
    
    public void draw2D(PGraphics p) {};

}
