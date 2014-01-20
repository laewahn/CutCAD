package de.mcp.customizer.application;

import processing.core.PApplet;
import toxi.geom.Vec2D;

public class Statusbar {
	
	private String status;
	private Vec2D mousePosition;
	
	public Statusbar()
	{
		this.mousePosition = new Vec2D(0,0);
		this.status = null;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setMousePosition(Vec2D mouse)
	{
		this.mousePosition = mouse;
	}
	
	public void drawStatusbar(PApplet p)
	{
	    if (status != null)
	    {
	    	p.textSize(24);
	    	p.text(status, 10, p.height-p.height/32);
	    	p.text("(" + (int) mousePosition.x() + "," + (int) mousePosition.y() + ")", p.width-100, p.height-p.height/32);
	    }
	}

}
