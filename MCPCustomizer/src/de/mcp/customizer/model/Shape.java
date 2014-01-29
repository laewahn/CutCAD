package de.mcp.customizer.model;

import processing.core.PGraphics;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;

public abstract class Shape implements Drawable2D
{
  public abstract GShape getShape();
  public abstract int getValue(int index);
  public abstract Shape copy();
  public abstract int getNumberOfControls();
  public abstract int getControlType(int index);
  public abstract String getNameOfControl(int index);
  public abstract void recalculate();
  
  @Override
	public void draw2D(PGraphics p, Transformation transform) {
		this.getShape().draw2D(p, transform);
	}

}
