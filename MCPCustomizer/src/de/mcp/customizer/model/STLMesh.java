package de.mcp.customizer.model;

import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;

public class STLMesh {
	private TriangleMesh STLMesh;
	private float X;
	private float Y;
	private float Z;
	private float rotX;
	private float rotY;
	private float rotZ;
	private float rotDelX;
	private float rotDelY;
	private float rotDelZ;
	private boolean changedPos;
	private boolean changedRot;
	private boolean stlImported;
	
	public STLMesh()
	{	this.X = 0;
		this.Y = 0;
		this.Z = 0;
		this.changedPos = false;
		this.stlImported = false;
	}
	
	public TriangleMesh getSTLMesh() {
		return STLMesh;
	}
	
	public void setSTLMesh(TriangleMesh sTLMesh) {
		STLMesh = sTLMesh;
		this.changedPos = true;
		this.stlImported = true;
	}
	
	private void setX(float x) {
		X = x;
		this.changedPos = true;
	}

	private void setY(float y) {
		Y = y;
		this.changedPos = true;
	}
	
	private void setZ(float z) {
		Z = z;
		this.changedPos = true;
	}
	
	public void setValue0(int size)
	{
		setX((float)(size*10));
	}
	
	public void setValue1(int size)
	{
		setY((float)(size*10));
	}
	
	public void setValue2(int size)
	{
		setZ((float)(size*10));
	}
	
	private void setRotX(float rotX) {
		this.rotDelX = rotX - this.rotX;
		this.rotX = rotX;
		this.changedRot = true;
	}
	
	private void setRotY(float rotY) {
		this.rotDelY = rotY - this.rotY;
		this.rotY = rotY;
		this.changedRot = true;
	}
	
	private void setRotZ(float rotZ) {
		this.rotDelZ = rotZ - this.rotZ;
		this.rotZ = rotZ;
		this.changedRot = true;
	}
	
	public void setValue3(int size)
	{
		setRotX((float)(size));
	}
	
	public void setValue4(int size)
	{
		setRotY((float)(size));
	}
	
	public void setValue5(int size)
	{
		setRotZ((float)(size));
	}
	
	public boolean isPosChanged() {
		return changedPos;
	}
	
	public boolean isStlImported() {
		return stlImported;
	}

	public boolean isRotChanged() {
		return changedRot;
	}
	
	public int getNumberOfControls() {
		return 6;
	}
	
	public float getValue(int i)
	{
		switch (i) {
		
        case 0: return this.X/10;
        
        case 1: return this.Y/10;
        
        case 2: return this.Z/10;
        
        case 3: return this.rotX;
        
        case 4: return this.rotY;
        
        case 5: return this.rotZ;
        
        default: return 0; }
	}
	
	public int getControlType(int index)
	{
		if(index < 3)
		{
			return 2;
		} else
		{
			return 0;
		}
	}
	  
	public String getNameOfControl(int index)
	{
		switch (index) {
		case 0: return "Position X";
		
		case 1: return "Position Y";
		
		case 2: return "Position Z";
		
		case 3: return "Rotation X";
		
		case 4: return "Rotation Y";
		
		case 5: return "Rotation Z";
		
		default: return "error";
		}
	}
	
	public void center()
	{
		STLMesh.center(new Vec3D(X,Y,Z));
		this.changedPos = false;
	}
	
	public void rotate()
	{
		STLMesh.rotateX((float)Math.toRadians(rotDelX));
		STLMesh.rotateY((float)Math.toRadians(rotDelY));
		STLMesh.rotateZ((float)Math.toRadians(rotDelZ));
		rotDelX = 0;
		rotDelY = 0;
		rotDelZ = 0;
		this.changedRot = false;
	}
}
