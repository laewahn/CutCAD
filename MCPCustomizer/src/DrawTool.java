import java.util.List;

import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

class DrawTool extends Tool {
    
    boolean isDrawing;

    Vec2D startCoord;
    Rectangle previewRectangle;
    List<Shape> rectangles;

    public DrawTool(Rect view, Properties properties, List<Shape> rectangles, Transformation2D transform)
    {
        super(view, properties, transform, "DrawTool");
        this.isDrawing = false;
        this.rectangles = rectangles;
    }

    public PGraphics getIcon(PGraphics context) 
    {
        context.beginDraw();
        context.noFill();
        context.stroke(0);
        context.strokeWeight(2);
        context.rect(50, 10, 50, 30);
        context.endDraw();
        
        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        if (this.inView(position)){
            isDrawing = true;
            
            this.startCoord = this.positionRelativeToView(position);
            
            this.previewRectangle = new Rectangle(startCoord.to3DXY(), 0,0);
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
