package de.mcp.cutcad.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;

import de.mcp.cutcad.model.primitives.Cutout;
import de.mcp.cutcad.model.primitives.Edge;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.view.Drawable2D;

/**
 * The object container is a model object to collect and manage all geometric instances of the project. It keeps track about changes in the project and 
 * allows for saving, loading and clearing the current project.
 * @author dennis
 *
 */
public class ObjectContainer {

	/**
	 * Contains all geometric objects created in the 2D drawing view.
	 */
	private List<Drawable2D> objects;
	
	/**
	 * Contains all imported STL meshes.
	 */
	private STLMesh stlMesh;
	
	private boolean unsavedChanges;
	
	public ObjectContainer() {
		this.objects = new ArrayList<>();
		this.stlMesh = new STLMesh();
	}
	
	/**
	 * Returns the STL mesh with all imported STLs.
	 * @return The STL mesh with all imported STLs.
	 */
	public STLMesh getSTLMesh() {
		return this.stlMesh;
	}
	
	/**
	 * Returns all 2D drawables in the current project.
	 * @return All 2D drawables in the container.
	 */
	public List<Drawable2D> allDrawables() {
		List<Drawable2D> drawables = new ArrayList<Drawable2D>(this.objects);
		return drawables;
	}
	
	/**
	 * Returns all shapes created in the current project.
	 * @return All shapes created in the current project.
	 */
	public List<Shape> allShapes() {
		List<Shape> shapes = new ArrayList<Shape>();
		
		for(Object o : this.objects) {
			if (o instanceof Shape) {
				shapes.add((Shape) o);
			}
		}
		
		return shapes;
	}
	
	/**
	 * Adds a shape to the project.
	 * @param shape A new shape.
	 */
	public void addShape(Shape shape) {
		this.objects.add((Drawable2D) shape);
		this.unsavedChanges = true;
	}
	
	/**
	 * Removes a shape from the project.
	 * @param shape The shape to remove.
	 */
	public void removeShape(Shape shape) {
		this.objects.remove(shape);
		this.unsavedChanges = true;
	}
	
	/**
	 * Returns the edges of all shapes in the project.
	 * @return The edges of all shapes in the project.
	 */
	public List<Edge> allEdges() {
		List<Edge> edges = new ArrayList<Edge>();
		
		for(Shape shape : this.allShapes()) {
			edges.addAll(shape.getGShape().getEdges());
		}
		
		return edges;
	}
	
	/**
	 * Returns all connections created in the project.
	 * @return All connections created in the project.
	 */
	public List<Connection> allConnections() {
		List<Connection> connections = new ArrayList<>();
		
		for(Object o : this.objects) {
			if(o instanceof Connection) {
				connections.add((Connection) o);
			}
		}
		
		return connections;
	}
	
	/**
	 * Adds a new connection to the project.
	 * @param connection A new connection.
	 */
	public void addConnection(Connection connection) {
		this.objects.add((Drawable2D) connection);
		this.unsavedChanges = true;
	}
	
	/**
	 * Removes a connection from the project.
	 * @param connection The connection to remove.
	 */
	public void removeConnection(Connection connection) {
		this.objects.remove(connection);
		this.unsavedChanges = true;
	}
	
	/**
	 * Returns all cutous created in the project.
	 * @return All cutous created in the project.
	 */
	public List<Cutout> allCutouts() {
		List<Cutout> cutouts = new ArrayList<Cutout>();
		
		for(Shape s : this.allShapes()) {
			cutouts.addAll(s.getGShape().getCutouts());
		}
		
		return cutouts;
	}
	
	/**
	 * Writes the current project into the given file.
	 * @param file The file to write the project into.
	 */
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
	
	/**
	 * Opens an existing project from a given file.
	 * @param theFile The file to open.
	 */
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
	
	/**
	 * Clears all shapes, STLs and connections of the current project.
	 */
	public void clear() {
		this.objects = new ArrayList<Drawable2D>();
		this.stlMesh = new STLMesh();
		this.unsavedChanges = false;
	}
	
	/**
	 * Returns whether the project has unsaved changes.
	 * @return true if the project has unsaved changes, false otherwise.
	 */
	public boolean hasUnsavedChanges() {
		return this.unsavedChanges;
	}
}
