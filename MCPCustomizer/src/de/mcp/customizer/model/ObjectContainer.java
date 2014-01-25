package de.mcp.customizer.model;

import java.util.List;
import java.util.ArrayList;

public class ObjectContainer {

	private List<Shape> shapes;
	private List<Connection> connections;
	
	public ObjectContainer() {
		this.shapes = new ArrayList<Shape>();
		this.connections = new ArrayList<Connection>();
	}
	
	public List<Shape> allShapes() {
		return this.allShapes();
	}
	
	public void addShape(Shape shape) {
		this.shapes.add(shape);
	}
	
	public List<Edge> allEdges() {
		return null;
	}
	
	public List<Connection> allConnections() {
		return this.connections;
	}
	
	public void addConnection(Connection connection) {
		this.connections.add(connection);
	}
}
