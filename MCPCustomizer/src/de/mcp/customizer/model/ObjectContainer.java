package de.mcp.customizer.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;

import de.mcp.customizer.model.primitives.Cutout;
import de.mcp.customizer.model.primitives.Edge;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.view.Drawable2D;

public class ObjectContainer {

	private List<Drawable2D> objects;
	private STLMesh stlMesh;
	
	private boolean unsavedChanges;
	
	public ObjectContainer() {
		this.objects = new ArrayList<>();
		this.stlMesh = new STLMesh();
	}
	
	public STLMesh getSTLMesh() {
		return this.stlMesh;
	}
	
	public List<Drawable2D> allDrawables() {
		List<Drawable2D> drawables = new ArrayList<Drawable2D>(this.objects);
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
		this.unsavedChanges = true;
	}
	
	public void removeShape(Shape shape) {
		this.objects.remove(shape);
		this.unsavedChanges = true;
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
		this.unsavedChanges = true;
	}
	
	public void removeConnection(Connection connection) {
		this.objects.remove(connection);
		this.unsavedChanges = true;
	}
	
	
	
	
	public List<Cutout> allCutouts() {
		List<Cutout> cutouts = new ArrayList<Cutout>();
		
		for(Shape s : this.allShapes()) {
			cutouts.addAll(s.getGShape().getCutouts());
		}
		
		return cutouts;
	}
	
	public void save(File file) {
		FileOutputStream fos;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);			
			
			oos.writeObject(objects);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				oos.close();
				this.unsavedChanges = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void load(File theFile) {
		FileInputStream fis;
		ObjectInputStream ois = null;
		
		try {
			fis = new FileInputStream(theFile);
			ois = new ObjectInputStream(fis);
			
			objects = (List<Drawable2D>) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				this.unsavedChanges = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clear() {
		this.objects = new ArrayList<Drawable2D>();
		this.stlMesh = new STLMesh();
		this.unsavedChanges = false;
	}
	
	public boolean hasUnsavedChanges() {
		return this.unsavedChanges;
	}
}
