package de.mcp.customizer.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;

import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.primitives.Cutout;
import de.mcp.customizer.model.primitives.Edge;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.view.Drawable2D;

public class ObjectContainer {

	private List<Drawable2D> objects;
	private Tool selectedTool;
	
	public ObjectContainer() {
		this.objects = new ArrayList<>();
	}
	
	
	public List<Drawable2D> allDrawables() {
		List<Drawable2D> drawables = new ArrayList<Drawable2D>(this.objects);
		
		if(getSelectedTool() != null) {
			drawables.add(getSelectedTool());
		}
		
		return drawables;
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
			edges.addAll(shape.getGShape().getEdges());
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
			cutouts.addAll(s.getGShape().getCutouts());
		}
		
		return cutouts;
	}
	
	public void setSelectedTool(Tool theTool) {
		this.selectedTool = theTool;
	}
	
	public Tool getSelectedTool() {
		return this.selectedTool;
	}
	
	public void safe(String filename) {
		FileOutputStream fos;
		ObjectOutputStream oos;
		try {
			fos = new FileOutputStream(new File(filename));
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(objects);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(String filename) {
		
	}
	
	public void clear() {
		this.objects = new ArrayList<Drawable2D>();
	}
}
