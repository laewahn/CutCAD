package de.mcp.customizer.application.tools;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public interface Selectable {
	public Rect getBoundingBox();
	public boolean mouseOver(Vec2D mousePosition);
}