package de.mcp.cutcad.application;

public interface Pluggable
{
	public void setActive(boolean b);

	public int getNumberOfControls();

	public int getValue(int i);

	public int getControlType(int i);

	public String getNameOfControl(int i);
}
