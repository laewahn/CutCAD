package de.mcp.customizer.model.primitives;

import java.io.Serializable;

import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class Vector2D implements Serializable{

	private static final long serialVersionUID = 3944945759160627223L;

	private transient Vec2D vector;
	
	public Vector2D(Vector2D vector) {
		this(vector.x(), vector.y());
	}
	
	public Vector2D(float x, float y) {
		this(new Vec2D(x,y));
	}
	
	public Vector2D(Vec2D vec) {
		this.vector = vec;
	}
	
	public Vector2D copy() {
		return new Vector2D(this);
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
	
	public Vector3D to3DXY() {
		return new Vector3D(this.x(), this.y(), 0);
	}
	
	public float getComponent(int idx) {
		return this.vector.getComponent(idx);
	}
	
	public Vector2D normalizeTo(float len) {
		this.vector.normalizeTo(len);
		return this;
	}
	
	public Vector2D getNormalizedTo(float len) {
		return new Vector2D(this.vector.normalizeTo(len));
	}
	
	public Vector2D getNormalized() {
		return new Vector2D(this.vector.getNormalized());
	}
	
	public void set(float x, float y) {
		this.vector.set(x, y);
	}
	
	public void set(Vector2D vec) {
		this.set(vec.x(), vec.y());
	}
	
	public Vector2D addSelf(Vector2D vector2d) {
		Vec2D otherVec2D = vector2d.getVec2D();
		this.vector.addSelf(otherVec2D);
		return this;
	}
	
	public Vector2D add(float a, float b) {
		return new Vector2D(this.vector.add(a,b));
	}
	
	public Vector2D add(Vector2D vec) {
		Vec2D otherVec2D = vec.getVec2D();
		return new Vector2D(this.vector.add(otherVec2D));
	}
	
	public Vector2D scale(float a) {
		return new Vector2D(this.vector.scale(a));
	}
	
	public Vector2D sub(Vec2D vec) {
		return new Vector2D(this.vector.sub(vec));
	}
	
	public Vector2D sub(Vector2D vec) {
		return this.sub(vec.getVec2D());
	}
	
	public Vector2D subSelf(float a, float b) {
		return new Vector2D(this.vector.subSelf(a, b));
	}
	
	public Vector2D subSelf(Vector2D vec) {
		return new Vector2D(this.vector.subSelf(vec.getVec2D()));
	}
	
	public float distanceTo(Vector2D vec) {
		return this.vector.distanceTo(vec.getVec2D());
	}

	public Vector2D perpendicular() {
		return new Vector2D(this.vector.perpendicular());
	}
	
	public Vector2D getRotated(float angle) {
		return new Vector2D(this.vector.getRotated(angle));
	}
	
	public Vector2D rotate(float angle) {
		this.vector.rotate(angle);
		return this;
	}

	public boolean equalsWithTolerance(Vector2D v, float f)
	{
		return this.vector.equalsWithTolerance(v.getVec2D(), f);
	}
	
	public boolean equals(Object o) {
		
		if(o instanceof Vector2D) {
			Vector2D otherVector = (Vector2D) o;
			return this.equals(otherVector.getVec2D());
		}
		
		if (o instanceof Vec2D) {
			return this.vector.equals(o);
		}
		return false;	
	}
}
