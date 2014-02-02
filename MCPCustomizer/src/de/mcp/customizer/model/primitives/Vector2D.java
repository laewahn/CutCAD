package de.mcp.customizer.model.primitives;

import java.io.Serializable;

import toxi.geom.Vec2D;

public class Vector2D implements Serializable{

	private static final long serialVersionUID = 3944945759160627223L;

	private transient Vec2D vector;
	
	public Vector2D(float x, float y) {
		this.vector = new Vec2D(x,y);
	}
	
	public Vec2D getVec2D() {
		return this.vector;
	}
	
	public float x() {
		return this.vector.x();
	}
	
	public float y() {
		return this.vector.y();
	}
}
