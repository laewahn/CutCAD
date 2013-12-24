import processing.core.*;
import toxi.geom.*;
import java.util.*;

class DrawTool extends Tool {
    
    boolean isDrawing;

    Vec2D startCoord;
    Rectangle previewRectangle;
    List<Shapes> rectangles;

    public DrawTool(Rect view, Properties properties, List<Shapes> rectangles)
    {
        super(view, properties);
        this.isDrawing = false;
        this.rectangles = rectangles;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        if (this.inView(position)){
            isDrawing = true;
            
            this.startCoord = this.positionRelativeToView(position);
            
            this.previewRectangle = new Rectangle(startCoord, new Vec2D(), 5);
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {

        if (isDrawing && this.inView(position)) {

            Vec2D endCoord = this.positionRelativeToView(position);
            Vec2D rectSize = endCoord.sub(this.startCoord);
            
            this.previewRectangle.setSize(rectSize);

            rectangles.add(this.previewRectangle);
            this.previewRectangle = null;

            isDrawing = false;
        }
    }

    public void mouseMoved(Vec2D position)
    {
        if (isDrawing){

            Vec2D endCoord = this.positionRelativeToView(position);
            Vec2D rectSize = endCoord.sub(this.startCoord);

            this.previewRectangle.setSize(rectSize);
        }

    }

    public void draw2D(PGraphics p)
    {
        if (this.previewRectangle != null) {
            this.previewRectangle.draw2D(p);
        }
    }
}
