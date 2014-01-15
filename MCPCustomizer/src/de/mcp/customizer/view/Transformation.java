package de.mcp.customizer.view;
import processing.core.PGraphics;
import toxi.geom.Vec2D;

public class Transformation
{
    protected float scale;
    protected Vec2D translation;

    public Transformation(float scale, Vec2D translation)
    {
        this.scale = scale;
        this.translation = translation;
    }

    public void transform(PGraphics view)
    {
        view.scale(this.scale);
        view.translate(-this.translation.x(), -this.translation.y());
    }

    public float getScale()
    {
        return this.scale;
    }

    public Vec2D getTranslation()
    {
        return this.translation;
    }

    public void scaleUp(float amount)
    {
        this.scale += amount;
        if (this.scale < 0) this.scale = 0;
    }

    public void scaleDown(float amount)
    {
        this.scale -= amount;
        if (this.scale < 0) this.scale = 0;
    }

    public void translate(Vec2D v)
    {
        this.translation.subSelf(v);
    }
}