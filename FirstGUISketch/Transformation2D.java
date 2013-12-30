import toxi.geom.*;
import processing.core.*;

class Transformation2D
{
    protected float scale;
    protected Vec2D translation;

    public Transformation2D(float scale, Vec2D translation)
    {
        this.scale = scale;
        this.translation = translation;
    }

    public void transform(PGraphics view)
    {
        view.scale(this.scale);
        view.translate(this.translation.x(), this.translation.y());
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
    }

    public void scaleDown(float amount)
    {
        this.scale -= amount;
    }

    public void translate(Vec2D translation)
    {
        this.translation.add(translation);
    }
}