import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;
import java.util.*;

class Edge
{
    private GShape parent;
    private Vec2D v1, v2;
    private boolean isSelected;

    public Edge(GShape parent, Vec2D v1, Vec2D v2)
    {
        this.parent = parent;
        this.v1 = v1;
        this.v2 = v2;
        this.isSelected = false;
    }
    
    public GShape getShape()
    {
        return parent;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

    public void setSelected(boolean selected)
    {
        this.isSelected = selected;
    }

    public Vec2D getV1()
    {
        return this.v1;
    }

    public Vec2D getV2()
    {
        return this.v2;
    }

    public void setV1(Vec2D v)
    {
        this.getV1().set(v);
    }

    public void setV2(Vec2D v)
    {
        this.getV2().set(v);
    }

//    public void drawEdge(PGraphics p)
//    {
//        if (this.isSelected())
//        {
//            p.stroke(255,0,0);
//            p.strokeWeight(3);
//        }
//        p.line(this.getV1().x()+parent.getPosition2D().x(), this.getV1().y()+parent.getPosition2D().y(), 
//        this.getV2().x()+parent.getPosition2D().x(), this.getV2().y()+parent.getPosition2D().y());
//        p.stroke(0);
//        p.strokeWeight(1);
//
//    }

    public Vec2D getMid()
    {
        return new Vec2D((this.getV1().x() + this.getV2().x()) / 2, (this.getV1().y() + this.getV2().y()) / 2);
    }

    // Checks whether the mousepointer is within a certain area around the edge
    // only checking if the mousepointer is ON the edge would result in bad usability
    // since the user would have to precisely point to a line that is one pixel wide.
    public boolean mouseOver(int mouseX, int mouseY, int view2DPosX, int view2DPosY)
    {
        // create a vector that is perpendicular to the edge
        Vec2D perpendicularVector = this.getV2().sub(this.getV1()).perpendicular().getNormalized();

        // with the perpendicular vector, calculate the defining points of a rectangle around the edge
        ArrayList<Vec2D> definingPoints = new ArrayList<Vec2D>();
        definingPoints.add(this.getV1().sub(perpendicularVector.scale(10)));
        definingPoints.add(this.getV1().add(perpendicularVector.scale(10)));
        definingPoints.add(this.getV2().sub(perpendicularVector.scale(10)));
        definingPoints.add(this.getV2().add(perpendicularVector.scale(10)));

        // create a rectangle around the edge
        Polygon2D borders = new Polygon2D(definingPoints);

        // check if the mousePointer is within the created rectangle
        Vec2D mousePointer = new Vec2D(mouseX-view2DPosX-parent.getPosition2D().x(),mouseY-view2DPosY-parent.getPosition2D().y());
        return borders.containsPoint(mousePointer);
    }
}
