import processing.core.*;
import toxi.geom.*;
import java.util.*;

abstract class Tool implements Drawable2D {

    protected Properties properties;
    protected Rect view;
    protected Transformation2D transform;

    public Tool(Rect view, Properties properties, Transformation2D transform)
    {
        this.view = view;
        this.properties = properties;
        this.transform = transform;
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

    abstract public void mouseButtonPressed(Vec2D position, int button);
    abstract public void mouseButtonReleased(Vec2D position, int button);
    abstract public void mouseMoved(Vec2D position);

    abstract public PGraphics getIcon(PGraphics context);
    
    public void draw2D(PGraphics p) {};

}
