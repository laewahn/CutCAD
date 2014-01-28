package de.mcp.customizer.model;

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import de.mcp.customizer.algorithm.CreateTenons;
import de.mcp.customizer.algorithm.RotateAdjectantShapes;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;

public class Connection implements Drawable2D
{
	private Edge masterEdge, slaveEdge;
	private float angle = 400;
	private boolean isSelected, isActive;
	private static List<Connection> connections; // wrong place???
	private float tolerance = 5f;
	private float scalingFactor, boundingBoxSize;

	/** 
	 * Creates a basic connection-object without specifying the two edges to be connected.
	 * 
	 * @param connections The list of existing connections
	 */
	public Connection(List<Connection> connections)
	{
		Connection.connections = connections;
		this.isSelected = false;
		this.isActive = false;
	}

	/**
	 * Creates a connection-object, specifying the two edges to be connected.
	 * 
	 * @param masterEdge The edge contained by the shape considered to be master (will not be moved when connecting the two shapes)
	 * @param slaveEdge The edge contained by the shape considered to be slave (will be moved/rotated when connecting the two shapes)
	 * @param connections The list of existing connections
	 */
	public Connection(Edge masterEdge, Edge slaveEdge, List<Connection> connections)
	{
		this.masterEdge = masterEdge;
		this.slaveEdge = slaveEdge;
		Connection.connections = connections;
		this.isSelected = false;
	}

	/**
	 * @return The list of all existing connections
	 */
	public static List<Connection> getConnections()
	{
		return connections;
	}

	/**
	 * @return The masterEdge of the connection
	 */
	public Edge getMasterEdge()
	{
		return this.masterEdge;
	}

	/**
	 * @return The slaveEdge of the connection
	 */
	public Edge getSlaveEdge()
	{
		return this.slaveEdge;
	}

	/**
	 * @param e The masterEdge of the connection
	 */
	public void setMasterEdge(Edge e)
	{
		this.masterEdge = e;
	}

	/**
	 * @param e The slaveEdge of the connection
	 */
	public void setSlaveEdge(Edge e)
	{
		this.slaveEdge = e;
	}

	public void draw2D(PGraphics p, Transformation t) {
		scalingFactor = t.getScale();
//		scalingFactor = masterEdge.getGShape().getScalingFactor();
		boundingBoxSize = 4 / scalingFactor;
		
		Vec2D mid1 = this.getMasterEdge().getMid().add(getMasterEdge().getGShape().getPosition2D()).scale(scalingFactor);
		Vec2D mid2 = this.getSlaveEdge().getMid().add(getSlaveEdge().getGShape().getPosition2D()).scale(scalingFactor);
		if (this.isSelected)
		{
			p.stroke(255, 0, 0);
		}
		else if (this.isActive)
		{
			p.stroke(125, 0, 0);
		}
		else
		{
			p.stroke(60, 60, 60);
		}
		p.line(mid1.x(), mid1.y(), mid2.x(), mid2.y());
		p.stroke(0);
	}

	/**
	 * Sets the selection-status of the connection to b
	 * 
	 * @param b The new selection-status of the connection
	 */
	public void setSelected(boolean b)
	{
		this.isSelected = b;
	}

	/**
	 * @return The current selection-status of the connection
	 */
	public boolean isSelected()
	{
		return this.isSelected;
	}

	/**
	 * Creates a rectangle around the representation of the connection and checks if the given mousePosition is within this rectangle
	 * 
	 * @param mousePosition The position of the mouse
	 * @return true if the given mousePosition is within the rectangle created around the connection
	 */
	public boolean mouseOver(Vec2D mousePosition)
	{
		Vec2D mid1 = this.getMasterEdge().getMid().add(getMasterEdge().getGShape().getPosition2D());
		Vec2D mid2 = this.getSlaveEdge().getMid().add(getSlaveEdge().getGShape().getPosition2D());

		// create a vector that is perpendicular to the connections line
		Vec2D perpendicularVector = mid1.sub(mid2).perpendicular().getNormalizedTo(boundingBoxSize);

		// with the perpendicular vector, calculate the defining points of a rectangle around the connections line
		ArrayList<Vec2D> definingPoints = new ArrayList<Vec2D>();
		definingPoints.add(mid1.sub(perpendicularVector).scale(scalingFactor));
		definingPoints.add(mid2.sub(perpendicularVector).scale(scalingFactor));
		definingPoints.add(mid2.add(perpendicularVector).scale(scalingFactor));
		definingPoints.add(mid1.add(perpendicularVector).scale(scalingFactor));

		// create a rectangle around the edge
		Polygon2D borders = new Polygon2D(definingPoints);

		// check if the mousePointer is within the created rectangle
		return borders.containsPoint(mousePosition.scale(scalingFactor));
	}

	/**
	 * Undos a connection between two edges. Removes the tenons from the edges, 
	 * unlocks the edges and sets the number of connections of the connected shapes to the correct number.
	 */
	public void undoConnection()
	{
		// remove Tenons
		CreateTenons.createOutlineOfEdge(masterEdge);
		CreateTenons.createOutlineOfEdge(slaveEdge);

		// Edges are not locked anymore
		lockConnection(false);
		// Maybe in the future, this should also rotate the 3D-Shape back to its original position
		if(masterEdge.getGShape().getNumberOfConnections() == 0) 
		{
			masterEdge.getGShape().recalculate(masterEdge.getGShape().getVertices());
		}
		if(slaveEdge.getGShape().getNumberOfConnections() == 0)
		{
			slaveEdge.getGShape().recalculate(slaveEdge.getGShape().getVertices());
		}
		if(masterEdge.getGShape().getNumberOfConnections() == 1) // Do weird stuff: lockup this connection and realign, maybe not necessary
		{
			recalculateConnectedEdge(masterEdge);
		}
		if(slaveEdge.getGShape().getNumberOfConnections() == 1)
		{
			recalculateConnectedEdge(slaveEdge);
		}  
	}

	private void recalculateConnectedEdge(Edge edge)
	{
		GShape shape = edge.getGShape();
		for (Edge e : shape.getEdges())
		{
			for (Connection c : Connection.getConnections())
			{
				if(c != this)
				{
					if (c.getMasterEdge() == e) 
					{
						connectEdges(c.getSlaveEdge(), c.getMasterEdge(), (float) Math.PI);
					}
					if (c.getSlaveEdge() == e)
					{
						connectEdges(c.getMasterEdge(), c.getSlaveEdge(), (float) Math.PI);
					}
				}
			}
		}
	}

	/**
	 * Connects the two edges of the connection if possible and returns a success/error-message 
	 * based on whether or not the connection could be made.
	 * 
	 * @return A message that either reports success or contains a description of the problem
	 * encountered when trying to connect the edges
	 */
	public String connect()
	{
		if (masterEdge.getGShape() == slaveEdge.getGShape())
		{
			// no connection allowed between two sides of the same shape
			// (until we use flexible materals :-)
			return "Do not connect two sides of the same shape!";
		}
		else if (masterEdge.isLocked() || slaveEdge.isLocked())
		{
			// do not connect edges which already have a connection
			return "At least one edge is already connected!";
		}
		else if (Math.abs(masterEdge.getLength()-slaveEdge.getLength())>tolerance)
		{
			// no connection between edges of different length (problem: not exacty same length...)
			return "Edges have different length!";
		}
		else if (slaveEdge.getGShape().getNumberOfConnections() == 0)
		{
			connectEdges(masterEdge, slaveEdge, (float) Math.PI);
			lockConnection(true);
			return "Connection created!";

		}
		else if (masterEdge.getGShape().getNumberOfConnections() == 0)
		{
			connectEdges(slaveEdge, masterEdge, (float) Math.PI);
			lockConnection(true);
			return "Connection created!";
		}
		else if (isEqualEdge(masterEdge, slaveEdge))
		{
			CreateTenons.createOutlineOfEdge(masterEdge, slaveEdge);
			// tenons are symmetric, the different orientation didn't do something wrong (at least i hope so)
			lockConnection(true);
			return "Connection created!";
		}
		else if(((masterEdge.getGShape().getNumberOfConnections() == 1) || (slaveEdge.getGShape().getNumberOfConnections() == 1))
				&& (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), tolerance) || 
						masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), tolerance) || 
						masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), tolerance) || 
						masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), tolerance)))
		{
			if (RotateAdjectantShapes.rotateBothShapes(this, masterEdge, slaveEdge))
			{
				return "Connection created!";
			}
			else 
			{
				return "Couldn't rotate the shapes until both edges are at the same position";
			}

		}
		return "I'm sorry,... I'm afraid I can't do that.";
	}

	private boolean isEqualEdge(Edge masterEdge, Edge slaveEdge)
	{
		if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), tolerance) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), tolerance)) return true;
		if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), tolerance) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), tolerance)) return true;
		return false;
	}

	/**
	 * Sets the locked-variable of the two edges of the connection to the given boolean value.
	 * 
	 * @param locked The value the locked-variable of the two edges is set to
	 */
	public void lockConnection(boolean locked)
	{
		int addNumber = locked ? 1 : -1;
		slaveEdge.getGShape().addNumberOfConnections(addNumber);
		masterEdge.getGShape().addNumberOfConnections(addNumber);
		slaveEdge.setLocked(locked);
		masterEdge.setLocked(locked);
	}

	/**
	 * Sets the angle between the two shapes that are connected.
	 * 
	 * @param angle the angle between the two shapes connected by this connection in degrees
	 */
	public void setAngle(float angle)
	{
		if (!(this.slaveEdge.getGShape().getNumberOfConnections() > 1))
		{
			this.angle = angle;
			connectEdges(this.masterEdge, this.slaveEdge, (float) Math.toRadians(angle));
		}
	}

	/**
	 * 
	 * Aligns and connects two given edges and their shapes in 3D-space at a given angle.
	 * 
	 * @param masterEdge the edge of the shape that is considered master (will not be moved during this operation)
	 * @param slaveEdge the edge of the shape that is considered slave (will be moved/rotated to the master)
	 * @param angle the angle between master and slave
	 */
	public void connectEdges(Edge masterEdge, Edge slaveEdge, float angle) {
		GShape master = masterEdge.getGShape();
		GShape slave = slaveEdge.getGShape();

		alignEdges(slave, masterEdge, slaveEdge);

		Vec3D toOrigin = slaveEdge.getP3D1().scale(-1);

		slave.translate3D(toOrigin);

		alignShapes(master, slave, masterEdge, slaveEdge);  

		// rotate the slave by the specified angle (currently hardcoded 90 degrees)
		Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();
		slave.rotateAroundAxis(rotationAxis, angle);

		Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1());
		slave.translate3D(toMaster);

		CreateTenons.createOutlineOfEdge(masterEdge, slaveEdge);
	}

	private void alignEdges(GShape slave, Edge masterEdge, Edge slaveEdge)
	{
		Vec3D masterEdgeDirection = masterEdge.getP3D2().sub(masterEdge.getP3D1());
		Vec3D slaveEdgeDirection = slaveEdge.getP3D2().sub(slaveEdge.getP3D1());
		float angle = safeAngleBetween(masterEdgeDirection, slaveEdgeDirection);

		Vec3D normalVector = slaveEdgeDirection.cross(masterEdgeDirection).getNormalized();
		while (normalVector.equals(new Vec3D(0,0,0)))
		{
			normalVector = masterEdgeDirection.cross(new Vec3D((float)Math.random(), (float)Math.random(), (float)Math.random())).getNormalized();
		}
		slave.rotateAroundAxis(normalVector, angle);
	}

	private float safeAngleBetween(Vec3D masterEdgeDirection,
			Vec3D slaveEdgeDirection) {
		float angle = slaveEdgeDirection.angleBetween(masterEdgeDirection, true);
		if (Float.isNaN(angle))
		{
			if(slaveEdgeDirection.add(masterEdgeDirection).equalsWithTolerance(new Vec3D(0,0,0), 0.1f))
			{
				angle = (float)Math.PI;
			}
			else
			{
				angle = 0;
			}
		}
		return angle;
	}

	private void alignShapes(GShape master, GShape slave, Edge masterEdge, Edge slaveEdge)
	{
		Vec3D slaveEdgeDirection = slaveEdge.getP3D2().getNormalized();

		float angleBetweenNormals = calculateAngleBetweenNormals(master, slave);
		slave.rotateAroundAxis(slaveEdgeDirection, angleBetweenNormals);

		if (calculateAngleBetweenNormals(master, slave) > 0.001f)
		{
			slave.rotateAroundAxis(slaveEdgeDirection, (float) -2.0 * angleBetweenNormals);
		}
	}

	private float calculateAngleBetweenNormals(GShape master, GShape slave)
	{
		Vec3D masterNormal = master.getNormalVector();
		Vec3D slaveNormal = slave.getNormalVector();
		return safeAngleBetween(masterNormal, slaveNormal);
	}

	/**
	 * @return The current angle between the two shapes connected by this connection
	 */
	public float getAngle() {
		if (angle == 400) angle = (float) Math.toDegrees(calculateAngleBetweenNormals(masterEdge.getGShape(), slaveEdge.getGShape()));
		return angle;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return The number of control-elements needed to set the properties of this object
	 */
	public int getNumberOfControls()
	{
		return 1;
	}
	
	public int getValue(int index)
	{
		return (int)getAngle();
	}

	public int getControlType(int index)
	{
		return 0;
	}

	public String getNameOfControl(int index)
	{
		return "Angle";
	}

	public void setValue0(int size)
	{
		setAngle(size);
	}

}

