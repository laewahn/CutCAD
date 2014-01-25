package de.mcp.customizer.model;

import java.util.List;
import java.util.ArrayList;

public class ObjectContainer {

	private List<Shape> shapes;
	private List<Connection> connections;
	private List<Cutout> cutouts;
	
	
	public ObjectContainer() {
		this.shapes = new ArrayList<Shape>();
		this.connections = new ArrayList<Connection>();
		this.cutouts = new ArrayList<Cutout>();
	}
	
	
	
	
	
	public List<Shape> allShapes() {
		return new ArrayList<Shape>(this.shapes);
	}
	
	public void addShape(Shape shape) {
		this.shapes.add(shape);
	}
	
	public void removeShape(Shape shape) {
		this.shapes.remove(shape);
	}
	
	
	
	
	public List<Edge> allEdges() {
		List<Edge> edges = new ArrayList<Edge>();
		
		for(Shape shape : this.shapes) {
			edges.addAll(shape.getShape().getEdges());
		}
		
		return edges;
	}
	
	
	
	
	public List<Connection> allConnections() {
		return new ArrayList<Connection>(this.connections);
	}
	
	public void addConnection(Connection connection) {
		this.connections.add(connection);
	}
	
	public void removeConnection(Connection connection) {
		this.connections.remove(connection);
	}
	
	
	
	
	public List<Cutout> allCutouts() {
		List<Cutout> cutouts = new ArrayList<Cutout>();
		
		for(Shape s : this.shapes) {
			cutouts.addAll(s.getShape().cutouts);
		}
		
		return cutouts;
	}
	
//	public void addCutout(Cutout cutout) {
//		this.cutouts.add(cutout);
//	}
//	
//	public void removeCutout(Cutout cutout) {
//		this.cutouts.remove(cutout);
//	}
}
