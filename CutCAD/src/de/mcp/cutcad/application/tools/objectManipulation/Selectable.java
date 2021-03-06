package de.mcp.cutcad.application.tools.objectManipulation;

import de.mcp.cutcad.model.primitives.Vector2D;
import toxi.geom.Rect;

public interface Selectable {
	public Rect getBoundingBox();
	public boolean mouseOver(Vector2D mousePosition);
}