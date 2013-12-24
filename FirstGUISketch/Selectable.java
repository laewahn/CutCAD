import toxi.geom.*;

interface Selectable {
	public Rect getBoundingBox();
	public boolean mouseOver(Vec2D mousePosition);
}