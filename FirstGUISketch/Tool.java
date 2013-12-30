import processing.core.*;
import toxi.geom.*;
import java.util.*;

abstract class Tool implements Drawable2D {

    protected Properties properties;
    protected Rect view;

    public Tool(Rect view, Properties properties)
    {
        this.view = view;
        this.properties = properties;
    }

    protected Vec2D positionRelativeToView(Vec2D inPosition) 
    {
        return inPosition.sub(this.view.getTopLeft());
    }

    protected boolean inView(Vec2D position) 
    {
        return this.view.containsPoint(position);
    }

    abstract public void mouseButtonPressed(Vec2D position, int button);
    abstract public void mouseButtonReleased(Vec2D position, int button);
    abstract public void mouseMoved(Vec2D position);

    abstract public PGraphics getIcon(PGraphics context);
    
    public void draw2D(PGraphics p) {};

}
