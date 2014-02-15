package de.mcp.customizer.view;

import de.mcp.customizer.model.primitives.Vector2D;
import processing.core.PGraphics;

public class Transformation
{
    protected float scale;
    protected Vector2D translation;

    public Transformation(float scale, Vector2D translation)
    {
        this.scale = scale;
        this.translation = translation;
    }

    public void transform(PGraphics view)
    {
        view.translate(-this.translation.x(), -this.translation.y());
    }

    public float getScale()
    {
        return this.scale;
    }

    public Vector2D getTranslation()
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

    public void translate(Vector2D v)
    {
        this.translation.subSelf(v);
    }
}