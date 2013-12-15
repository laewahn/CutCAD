import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;
import java.util.*;

class Edge
{
    private Vec3D p3D1, p3D2;
    private Vec2D p2D1, p2D2;
    private boolean isSelected;

    public Edge(Vec3D p3D1, Vec3D p3D2, Vec2D p2D1, Vec2D p2D2)
    {
        this.p3D1 = p3D1;
        this.p3D2 = p3D2;
        this.p2D1 = p2D1;
        this.p2D2 = p2D2;
        this.isSelected = false;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

    public void setSelected(boolean selected)
    {
        this.isSelected = selected;
    }

    public Vec3D getP3D1()
    {
        return p3D1;
    }

    public Vec3D getP3D2()
    {
        return p3D2;
    }

    public void setP3D1(Vec3D v)
    {
        this.p3D1 = v;
    }

    public void setP3D2(Vec3D v)
    {
        this.p3D2 = v;
    }

    public Vec2D getP2D1()
    {
        return p2D1;
    }

    public Vec2D getP2D2()
    {
        return p2D2;
    }

    public void setP2D1(Vec2D v)
    {
        this.p2D1 = v;
    }

    public void setP2D2(Vec2D v)
    {
        this.p2D2 = v;
    }

    public void drawEdge(PGraphics p)
    {
        p.line(p2D1.x(), p2D1.y(), p2D2.x(), p2D2.y());
    }

    public Vec2D getMid()
    {
        return new Vec2D((this.getP2D1().x() + this.getP2D2().x()) / 2, (this.getP2D1().y() + this.getP2D2().y()) / 2);
    }

    // Checks whether the mousepointer is within a certain area around the edge
    // only checking if the mousepointer is ON the edge would result in bad usability
    // since the user would have to precisely point to a line that is one pixel wide.
    public boolean mouseOver(int mouseX, int mouseY, int view2DPosX, int view2DPosY)
    {
        // create a vector that is perpendicular to the edge
        Vec2D perpendicularVector = p2D2.sub(p2D1).perpendicular().getNormalized();

        // with the perpendicular vector, calculate the defining points of a rectangle around the edge
        ArrayList<Vec2D> definingPoints = new ArrayList<Vec2D>();
        definingPoints.add(p2D1.sub(perpendicularVector.scale(10)));
        definingPoints.add(p2D1.add(perpendicularVector.scale(10)));
        definingPoints.add(p2D2.sub(perpendicularVector.scale(10)));
        definingPoints.add(p2D2.add(perpendicularVector.scale(10)));

        // create a rectangle around the edge
        Polygon2D borders = new Polygon2D(definingPoints);

        // check if the mousePointer is within the created rectangle
        Vec2D mousePointer = new Vec2D(mouseX-view2DPosX,mouseY-view2DPosY);
        return borders.containsPoint(mousePointer);
    }
}