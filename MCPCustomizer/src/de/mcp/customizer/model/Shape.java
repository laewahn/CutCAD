package de.mcp.customizer.model;
public abstract class Shape
{
  public abstract GShape getShape();
  //public abstract void changeValue(int index, int value);
  public abstract int getValue(int index);
  public abstract Shape copy();
//  public abstract String getName();
  public abstract int getNumberOfControls();
  public abstract int getMinValueOfControl(int index);
  public abstract int getMaxValueOfControl(int index);
  public abstract String getNameOfControl(int index);
  //ToDo getEdges (which change with values for display)??
  //ToDo getString (Name of Properties)??
}
