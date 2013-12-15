import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

import java.util.*;

public class Rectangle
{
    private int posX, posY, posZ, sizeX, sizeY, thickness;
    private Edge north, west, south, east;
    private boolean isSeleceted;
    private TriangleMesh mesh;
    private ArrayList<Edge> edges;

    public Rectangle(int posX, int posY, int posZ, int sizeX, int sizeY, int thickness)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.north = new Edge(new Vec3D(posX, posY, posZ), new Vec3D(posX + sizeX, posY, posZ), new Vec2D(posX,posY), new Vec2D(posX+sizeX,posY));
        this.south = new Edge(new Vec3D(posX, posY + sizeY, posZ), new Vec3D(posX + sizeX, posY + sizeY, posZ), new Vec2D(posX, posY + sizeY), new Vec2D(posX + sizeX, posY + sizeY));
        this.west = new Edge(new Vec3D(posX, posY, posZ), new Vec3D(posX, posY + sizeY, posZ),new Vec2D(posX, posY), new Vec2D(posX, posY + sizeY));
        this.east = new Edge(new Vec3D(posX + sizeX, posY, posZ), new Vec3D(posX + sizeX, posY + sizeY, posZ),new Vec2D(posX + sizeX, posY), new Vec2D(posX + sizeX, posY + sizeY));
        this.edges = new ArrayList<Edge>();
        this.edges.add(north);
        this.edges.add(west);
        this.edges.add(south);
        this.edges.add(east);

        this.thickness = thickness;

        this.isSeleceted = false;

        this.mesh = new TriangleMesh();
        calcMesh();
    }

    public static void drawPreview(PGraphics p, int posX, int posY, int sizeX, int sizeY)
    {
        p.rect(posX,posY,sizeX,sizeY);
    }

    // TODO: Fill the rectangle drawn by the edges somehow without using the rect-function
    // as soon as we introduce riffled edges/tenons, it won't work this way.
    public void drawRectangle2D(PGraphics p)
    {   
        this.setFillColor(p);
        Rectangle.drawPreview(p, posX, posY, sizeX, sizeY);
        for (Edge e : edges)
        {
            e.drawEdge(p);
        }

    }

    public void drawRectangle3D(PGraphics p, ToxiclibsSupport gfx) {
        this.setFillColor(p);
        gfx.mesh(this.getMesh());
    }

    private void setFillColor(PGraphics p) {
        if (this.isSelected()) {
            p.fill(255,0,0);
        } else {
            p.fill(255);
        }
    }

    private void calcMesh()
    {
        mesh.clear();
        Vec3D perpendicularVector = north.getP3D1().sub(north.getP3D2()).cross(west.getP3D1().sub(west.getP3D2())).getNormalized().scale(thickness);
        Vec3D[] topRect = {
            north.getP3D1().add(perpendicularVector), 
            south.getP3D1().add(perpendicularVector), 
            south.getP3D2().add(perpendicularVector), 
            north.getP3D2().add(perpendicularVector)
        };
        Vec3D[] bottomRect = {
            north.getP3D1(), 
            south.getP3D1(), 
            south.getP3D2(), 
            north.getP3D2()
        };
        // create bottom triangles
        mesh.addFace(bottomRect[0], bottomRect[1], bottomRect[2]);
        mesh.addFace(bottomRect[0], bottomRect[2], bottomRect[3]);
        // create top triangles
        mesh.addFace(topRect[0], topRect[1], topRect[2]);
        mesh.addFace(topRect[0], topRect[2], topRect[3]);
        // create sides
        for (int i = 0; i < 4; i++)
        {
            mesh.addFace(bottomRect[i], topRect[i], bottomRect[(i+1)%4]);
            mesh.addFace(topRect[i], bottomRect[(i+1)%4], topRect[(i+1)%4]);
        }
    }

    public void setSizeX(int sizeX)
    {
        this.sizeX = sizeX;
        recalculateEdges();
        calcMesh();
    }

    public void setSizeY(int sizeY)
    {
        this.sizeY = sizeY;
        recalculateEdges();
        calcMesh();
    }

    public void setThickness(int thickness)
    {
        this.thickness = thickness;
        recalculateEdges();
        calcMesh();
    }

    public int getSizeX()
    {
        return sizeX;
    }

    public int getSizeY()
    {
        return sizeY;
    }

    public int getThickness()
    {
        return thickness;
    }

    public Edge getNorth()
    {
        return north;
    }

    public Edge getWest()
    {
        return west;
    }

    public Edge getSouth()
    {
        return south;
    }

    public Edge getEast()
    {
        return east;
    }

    public TriangleMesh getMesh()
    {
        return this.mesh;
    }

    public boolean isSelected() {
        return this.isSeleceted;
    }

    public void setSelected(boolean selected) {
        this.isSeleceted = selected;
    }

    public ArrayList<Edge> getEdges()
    {
        return this.edges;
    }

    public void moveTo(int posX, int posY)
    {
        this.posX = posX - sizeX/2;
        this.posY = posY - sizeY/2;
        recalculateEdges();
        calcMesh();
    }

    public boolean mouseOver(int mouseX, int mouseY, int view2DPosX, int view2DPosY)
    {
        if (mouseX > posX + view2DPosX && mouseX <= posX + sizeX + view2DPosX && mouseY > posY + view2DPosY && mouseY <= posY + sizeY + view2DPosY)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void recalculateEdges()
    {
        this.north.setP3D1(new Vec3D(posX, posY, posZ));
        this.north.setP3D2(new Vec3D(posX + sizeX, posY, posZ));
        this.north.setP2D1(new Vec2D(posX,posY));
        this.north.setP2D2(new Vec2D(posX+sizeX,posY));
        this.south.setP3D1(new Vec3D(posX, posY + sizeY, posZ));
        this.south.setP3D2(new Vec3D(posX + sizeX, posY + sizeY, posZ));
        this.south.setP2D1(new Vec2D(posX, posY + sizeY));
        this.south.setP2D2(new Vec2D(posX + sizeX, posY + sizeY));
        this.west.setP3D1(new Vec3D(posX, posY, posZ));
        this.west.setP3D2(new Vec3D(posX, posY + sizeY, posZ));
        this.west.setP2D1(new Vec2D(posX, posY));
        this.west.setP2D2(new Vec2D(posX, posY + sizeY));
        this.east.setP3D1(new Vec3D(posX + sizeX, posY, posZ));
        this.east.setP3D2(new Vec3D(posX + sizeX, posY + sizeY, posZ));
        this.east.setP2D1(new Vec2D(posX + sizeX, posY));
        this.east.setP2D2(new Vec2D(posX + sizeX, posY + sizeY));
    }

}