package de.mcp.customizer.model.primitives;

import java.io.Serializable;

import toxi.geom.Vec3D;

public class Vector3D implements Serializable {

	private static final long serialVersionUID = -4325576947506131537L;
	
	private transient Vec3D vector;
	
	public Vector3D() {
		this(0, 0, 0);
	}
	
	public Vector3D(Vector3D vector) {
		this(vector.x(), vector.y(), vector.z());
	}
	
	public Vector3D(float x, float y, float z) {
		this(new Vec3D(x, y, z));
	}
	
	public Vector3D(Vec3D vector) {
		this.vector = vector;
	}
	
	public Vector3D copy() {
		return new Vector3D(this.vector.copy());
	}
	
	public float x() {
		return this.vector.x();
	}
	
	public float y() {
		return this.vector.y();
	}
	
	public float z() {
		return this.vector.z();
	}
	
	public Vec3D getVec3D() {
		return this.vector;
	}
	
	public Vector3D getNormalized() {
		return new Vector3D(this.vector.getNormalized());
	}
	
	public Vector3D add(Vector3D vector) {
		return new Vector3D(this.vector.add(vector.getVec3D()));
	}
	
	public Vector3D addSelf(Vector3D vector) {
		this.vector.addSelf(vector.getVec3D());
		return this;
	}
	
	public Vector3D sub(Vector3D vector) {
		Vec3D otherVector = vector.getVec3D();
		return new Vector3D(this.vector.sub(otherVector));
	}
	
	public Vector3D scale(float s) {
		return new Vector3D(this.vector.scale(s));
	}
	
	public Vector3D cross(Vector3D vector) {
		Vec3D otherVector = vector.getVec3D();
		return new Vector3D(this.vector.cross(otherVector));
	}
	
	public Vector2D to2DXY() {
		return new Vector2D(this.vector.to2DXY());
	}
	
	public float angleBetween(Vector3D vector, boolean bool) {
		return this.vector.angleBetween(vector.getVec3D(), bool);
	}
	
	public boolean equalsWithTolerance(Vector3D vector, float tolerance) {
		return this.vector.equalsWithTolerance(vector.getVec3D(), tolerance);
	}
	
	public Vector3D rotateAroundAxis(Vector3D axis, float angle) {
		Vec3D axisVec3D = axis.getVec3D();
		return new Vector3D(this.vector.rotateAroundAxis(axisVec3D, angle));
	}
	
	public boolean isZeroVector() {
		return this.vector.isZeroVector();
	}
	
	public Vector3D invert() {
		return new Vector3D(this.vector.invert());
	}
	
	public Vector3D normalize() {
		return new Vector3D(this.vector.normalize());
	}
	
	public Vector3D normalizeTo(float n) {
		return new Vector3D(this.vector.normalizeTo(n));
	}
}
