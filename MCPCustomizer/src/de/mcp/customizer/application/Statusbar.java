package de.mcp.customizer.application;

import processing.core.PApplet;

public class Statusbar {
	
	private String status;
	
	public Statusbar()
	{
		this.status = null;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void drawStatusbar(PApplet p)
	{
	    if (status != null)
	    {
	    	p.textSize(24);
	    	p.text(status, 10, p.height-p.height/32);
	    }
	}

}
