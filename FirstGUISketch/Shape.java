public abstract class Shape
{
  public abstract GShape getShape();
  public abstract void changeValue(int index, int value);
  public abstract int getValue(int index);
  public abstract Shape copy();
  public abstract String getName();
  //ToDo getEdges (which change with values for display)??
  //ToDo getString (Name of Properties)??
}
