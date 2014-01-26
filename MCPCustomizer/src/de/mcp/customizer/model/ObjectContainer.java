package de.mcp.customizer.model;

import java.util.List;
import java.util.ArrayList;

import de.mcp.customizer.view.Drawable2D;

public class ObjectContainer {

	private List<Drawable2D> objects;
	
	public ObjectContainer() {
		this.objects = new ArrayList<>();
	}
	
	
	public List<Drawable2D> allDrawables() {
		return new ArrayList<Drawable2D>(this.objects);
	}
	
	
	public List<Shape> allShapes() {
		List<Shape> shapes = new ArrayList<Shape>();
		
		for(Object o : this.objects) {
			if (o instanceof Shape) {
				shapes.add((Shape) o);
			}
		}
		
		return shapes;
	}
	
	public void addShape(Shape shape) {
		this.objects.add((Drawable2D) shape);
	}
	
	public void removeShape(Shape shape) {
		this.objects.remove(shape);
	}
	
	
	
	
	public List<Edge> allEdges() {
		List<Edge> edges = new ArrayList<Edge>();
		
		for(Shape shape : this.allShapes()) {
			edges.addAll(shape.getShape().getEdges());
		}
		
		return edges;
	}
	
	
	
	
	public List<Connection> allConnections() {
		List<Connection> connections = new ArrayList<>();
		
		for(Object o : this.objects) {
			if(o instanceof Connection) {
				connections.add((Connection) o);
			}
		}
		
		return connections;
	}
	
	public void addConnection(Connection connection) {
		this.objects.add((Drawable2D) connection);
	}
	
	public void removeConnection(Connection connection) {
		this.objects.remove(connection);
	}
	
	
	
	
	public List<Cutout> allCutouts() {
		List<Cutout> cutouts = new ArrayList<Cutout>();
		
		for(Shape s : this.allShapes()) {
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
