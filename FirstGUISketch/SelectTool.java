import processing.core.*;
import toxi.geom.*;
import java.util.*;

class SelectTool extends Tool {
    
    List<Shapes> shapes;
    boolean dragging;
    Vec2D originalMousePosition;
    
    public SelectTool(Rect view, Properties properties, List<Shapes> shapes) 
    {
        super(view, properties);
        
        this.shapes = shapes;
        this.dragging = false;
        this.originalMousePosition = new Vec2D(0,0);
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        for (Shapes s : shapes)
        {
            if (this.inView(position) && s.getShape().isSelected() && button == PConstants.LEFT)
            {
                properties.plugTo(s);
                properties.show();
            } else if (this.inView(position) && s.getShape().isSelected() && button == PConstants.RIGHT){
                this.dragging = true;
                // this.originalMousePosition.set(position.sub(new Vec2D(view2DPosX, view2DPosY)));
                Vec2D currentMousePosition = this.positionRelativeToView(position);
                this.originalMousePosition.set(currentMousePosition);
            }
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        if (button == PConstants.RIGHT) {
            this.dragging = false;
        }
    }
    
    public void mouseMoved(Vec2D position)
    {
        if (this.inView(position))
        {
            Vec2D relativePosition = this.positionRelativeToView(position);

            for (Shapes s : shapes) {
                s.getShape().setSelected(s.getShape().mouseOver(relativePosition));

                if (s.getShape().isSelected() && this.dragging)
                {
                    Vec2D currentMousePosition = this.positionRelativeToView(position);
                    s.getShape().translate2D(currentMousePosition.sub(originalMousePosition));
                    originalMousePosition.set(currentMousePosition);
                }
            }
        }
        else
        {
            for (Shapes s : shapes) {
                s.getShape().setSelected(false);
            }
        }
    }
}
