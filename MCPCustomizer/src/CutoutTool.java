import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import toxi.geom.Rect;
import toxi.geom.Vec2D;


public class CutoutTool extends Tool {

    List<Shape> shapes;
    List<Connection> connections;
    boolean dragging;
    boolean selectedFirst;
    Vec2D originalMousePosition;
    Vec2D relativePosition;
    Shape masterShape;
    
    public CutoutTool(Rect view, Properties properties, List<Shape> shapes, List<Connection> connections, Transformation2D transform) 
    {
        super(view, properties, transform, "CutoutTool");
        
        this.shapes = shapes;
        this.connections = connections;
        this.dragging = false;
        this.selectedFirst = false;
        this.originalMousePosition = new Vec2D(0,0);
    }

    public PGraphics getIcon(PGraphics context)
    {
        context.beginDraw();
        context.noFill();
        context.stroke(0);
        context.strokeWeight(2);
        context.rect(50, 10, 50, 30);
        context.rect(75, 17, 10, 20);
        context.endDraw();

        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        for (Shape s : shapes)
        {
            if (this.inView(position) && button == PConstants.LEFT)
            {
            	if (!selectedFirst && s.getShape().isSelected() )
            	{
            		s.getShape().setSelected(true);
            		Vec2D currentMousePosition = this.positionRelativeToView(position);
                    this.originalMousePosition.set(currentMousePosition);
            		masterShape = s;
            		selectedFirst = true;
            	}
            	else if (selectedFirst && s.getShape().isSelected() )
            	{
            		masterShape.getShape().addCutout(s.getShape());
            		selectedFirst = false;
            	}
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
            relativePosition = this.positionRelativeToView(position);

            for (Shape s : shapes) {
                s.getShape().setSelected(s.getShape().mouseOver(relativePosition));
            }
        }
    }
    
    public void draw2D(PGraphics p)
    {
        if (selectedFirst) {	
    		Polygon2D findCenter = new Polygon2D();
    		for (Edge e : masterShape.getShape().getEdges()) findCenter.add(e.getV1().copy());
    		Vec2D center = findCenter.getCentroid();
            Vec2D mid = center.add(masterShape.getShape().getPosition2D());
            p.stroke(255,0,0);
            Vec2D lineStart = mid;
            Vec2D lineEnd = this.relativePosition;
            p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
            p.stroke(0);
        }
    }
}
