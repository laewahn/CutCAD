package de.mcp.customizer.application.tools.objectManipulation;

import de.mcp.customizer.model.primitives.Vector2D;
import toxi.geom.Rect;

public interface Selectable {
	public Rect getBoundingBox();
	public boolean mouseOver(Vector2D mousePosition);
}