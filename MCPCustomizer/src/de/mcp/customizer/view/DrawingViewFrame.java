package de.mcp.customizer.view;

import toxi.geom.Rect;
import de.mcp.customizer.model.primitives.Vector2D;

public class DrawingViewFrame {
	
	public Vector2D origin;
	public Vector2D size;
	
	public boolean containsPoint(Vector2D point) {
		Rect frameRect = new Rect(this.origin.getVec2D(), this.origin.add(size).getVec2D());
		return frameRect.containsPoint(point.getVec2D());
	}
}
