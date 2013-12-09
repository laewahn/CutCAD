import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

public class Rectangle
{
    private int posX, posY, sizeX, sizeY, thickness;
    private TriangleMesh mesh;

    public Rectangle(int posX, int posY, int sizeX, int sizeY, int thickness)
    {
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.thickness = thickness;
        this.mesh = new TriangleMesh();
        calcMesh();
    }

    public static void drawPreview(PGraphics p, int posX, int posY, int sizeX, int sizeY)
    {
        p.rect(posX,posY,sizeX,sizeY);
    }

    public void drawRectangle2D(PGraphics p)
    {
        p.rect(posX,posY,sizeX,sizeY);
    }

    private void calcMesh()
    {
        mesh.clear();
        Vec3D[] topRect = {
            new Vec3D(posX, posY, thickness),
            new Vec3D(posX+sizeX, posY, thickness),
            new Vec3D(posX+sizeX, posY+sizeY, thickness),
            new Vec3D(posX, posY+sizeY, thickness)           
        };
        Vec3D[] bottomRect = {
            new Vec3D(posX, posY, 0),
            new Vec3D(posX+sizeX, posY, 0),
            new Vec3D(posX+sizeX, posY+sizeY, 0),
            new Vec3D(posX, posY+sizeY, 0)           
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
        calcMesh();
    }

    public void setSizeY(int sizeY)
    {
        this.sizeY = sizeY;
        calcMesh();
    }

    public void setThickness(int thickness)
    {
        this.thickness = thickness;
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

    public TriangleMesh getMesh()
    {
        return this.mesh;
    }

    public void moveTo(int posX, int posY)
    {
        this.posX = posX - sizeX/2;
        this.posY = posY - sizeY/2;
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

}