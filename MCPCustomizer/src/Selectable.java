import toxi.geom.Rect;
import toxi.geom.Vec2D;

interface Selectable {
	public Rect getBoundingBox();
	public boolean mouseOver(Vec2D mousePosition);
}