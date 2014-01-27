package de.mcp.customizer.model;
public abstract class Shape
{
  public abstract GShape getShape();
  public abstract int getValue(int index);
  public abstract Shape copy();
  public abstract int getNumberOfControls();
  public abstract int getControlType(int index);
  public abstract String getNameOfControl(int index);
  public abstract void recalculate();
}
