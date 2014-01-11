package de.mcp.customizer.model;

public class PolygonShape extends Shape {
	
	private GShape shape;
	
	public PolygonShape(GShape shape) {
		this.shape = shape;
	}
	
	@Override
	public GShape getShape() {
		return this.shape;
	}

	@Override
	public void changeValue(int index, int value) {
	}

	@Override
	public int getValue(int index) {
		return 0;
	}

	@Override
	public Shape copy() {
		return new PolygonShape(this.shape);
	}

	@Override
	public String getName() {
		return this.toString();
	}

}
