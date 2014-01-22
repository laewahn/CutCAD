package de.mcp.customizer.model;

import toxi.geom.mesh.TriangleMesh;

public class STLMesh {
	private TriangleMesh STLMesh;
	private float X;
	
	public TriangleMesh getSTLMesh() {
		return STLMesh;
	}
	public void setSTLMesh(TriangleMesh sTLMesh) {
		STLMesh = sTLMesh;
	}
	public float getX() {
		return X;
	}
	public void setX(float x) {
		X = x;
	}

}
