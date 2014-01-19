package de.mcp.customizer.model;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.algorithm.RotateAdjectantShapes;
import de.mcp.customizer.view.Drawable2D;
import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class Connection implements Drawable2D
{
	private Edge masterEdge, slaveEdge;
	private float angle = 400;
	private boolean isSelected, isActive;
	private static List<Connection> connections; // wrong place???
	private float tolerance = 5f;

	public Connection(List<Connection> connections)
	{
		Connection.connections = connections;
		this.isSelected = false;
		this.isActive = false;
	}

	public Connection(Edge masterEdge, Edge slaveEdge, List<Connection> connections)
	{
		this.masterEdge = masterEdge;
		this.slaveEdge = slaveEdge;
		Connection.connections = connections;
		this.isSelected = false;
	}

	public static List<Connection> getConnections()
	{
		return connections;
	}

	public Edge getMasterEdge()
	{
		return this.masterEdge;
	}

	public Edge getSlaveEdge()
	{
		return this.slaveEdge;
	}

	public void setMasterEdge(Edge e)
	{
		this.masterEdge = e;
	}

	public void setSlaveEdge(Edge e)
	{
		this.slaveEdge = e;
	}

	@Override
	public void draw2D(PGraphics p) {
		this.drawConnection(p);
	}

	private void drawConnection(PGraphics p)
	{
		Vec2D mid1 = this.getMasterEdge().getMid().add(getMasterEdge().getShape().getPosition2D());
		Vec2D mid2 = this.getSlaveEdge().getMid().add(getSlaveEdge().getShape().getPosition2D());
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

	public void setSelected(boolean b)
	{
		this.isSelected = b;
	}

	public boolean isSelected()
	{
		return this.isSelected;
	}

	public boolean mouseOver(Vec2D position)
	{
		Vec2D mid1 = this.getMasterEdge().getMid().add(getMasterEdge().getShape().getPosition2D());
		Vec2D mid2 = this.getSlaveEdge().getMid().add(getSlaveEdge().getShape().getPosition2D());

		// create a vector that is perpendicular to the connections line
		Vec2D perpendicularVector = mid1.sub(mid2).perpendicular().getNormalized();

		// with the perpendicular vector, calculate the defining points of a rectangle around the connections line
		ArrayList<Vec2D> definingPoints = new ArrayList<Vec2D>();
		definingPoints.add(mid1.sub(perpendicularVector.getNormalizedTo(4)));
		definingPoints.add(mid2.sub(perpendicularVector.getNormalizedTo(4)));
		definingPoints.add(mid2.add(perpendicularVector.getNormalizedTo(4)));
		definingPoints.add(mid1.add(perpendicularVector.getNormalizedTo(4)));

		// create a rectangle around the edge
		Polygon2D borders = new Polygon2D(definingPoints);

		// check if the mousePointer is within the created rectangle
		return borders.containsPoint(position);
	}

	public void undoConnection()
	{
		// remove Tenons
		Tenon.createOutlineOfEdge(masterEdge);
		Tenon.createOutlineOfEdge(slaveEdge);

		// Edges are not locked anymore
		lockConnection(false);
		// Maybe in the future, this should also rotate the 3D-Shape back to its original position
		if(masterEdge.getShape().getNumberOfConnections() == 0) 
		{
			masterEdge.getShape().recalculate(masterEdge.getShape().getVertices());
		}
		if(slaveEdge.getShape().getNumberOfConnections() == 0)
		{
			slaveEdge.getShape().recalculate(slaveEdge.getShape().getVertices());
		}
		if(masterEdge.getShape().getNumberOfConnections() == 1) // Do weird stuff: lockup this connection and realign, maybe not necessary
		{
			recalculateConnectedEdge(masterEdge);
		}
		if(slaveEdge.getShape().getNumberOfConnections() == 1)
		{
			recalculateConnectedEdge(slaveEdge);
		}  
	}

	private void recalculateConnectedEdge(Edge edge)
	{
		GShape shape = edge.getShape();
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

	public boolean connect()
	{
		if (masterEdge.getShape() == slaveEdge.getShape())
		{
			// no connection allowed between two sides of the same shape
			// (until we use flexible materals :-)
			out.println("Do not connect two sides of the same shape!");
			return false;
		}
		else if (masterEdge.isLocked() || slaveEdge.isLocked())
		{
			// do not connect edges which already have a connection
			out.println("At least one edge is already connected!");
			return false;
		}
		else if (Math.abs(masterEdge.getLength()-slaveEdge.getLength())>tolerance)
		{
			// no connection between edges of different length (problem: not exacty same length...)
			out.println("Edges have different length!");
			return false;
		}
		else if (slaveEdge.getShape().getNumberOfConnections() == 0)
		{
			connectEdges(masterEdge, slaveEdge, (float) Math.PI);
			lockConnection(true);
			return true;

		}
		else if (masterEdge.getShape().getNumberOfConnections() == 0)
		{
			connectEdges(slaveEdge, masterEdge, (float) Math.PI);
			lockConnection(true);
			return true;
		}
		else if (isEqualEdge(masterEdge, slaveEdge))
		{
			Tenon.createOutlineOfEdge(masterEdge, slaveEdge);
			// tenons are symmetric, the different orientation didn't do something wrong (at least i hope so)
			lockConnection(true);
			return true;
		}
		else if(((masterEdge.getShape().getNumberOfConnections() == 1) || (slaveEdge.getShape().getNumberOfConnections() == 1))
				&& (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), tolerance) || 
						masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), tolerance) || 
						masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), tolerance) || 
						masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), tolerance)))
		{
			return RotateAdjectantShapes.rotateBothShapes(this, masterEdge, slaveEdge);

		}
		return false;
	}

	private boolean isEqualEdge(Edge masterEdge, Edge slaveEdge)
	{
		if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D2(), tolerance) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D1(), tolerance)) return true;
		if (masterEdge.getP3D1().equalsWithTolerance(slaveEdge.getP3D1(), tolerance) && masterEdge.getP3D2().equalsWithTolerance(slaveEdge.getP3D2(), tolerance)) return true;
		return false;
	}

	public void lockConnection(boolean locked)
	{
		int addNumber = locked ? 1 : -1;
		slaveEdge.getShape().addNumberOfConnections(addNumber);
		masterEdge.getShape().addNumberOfConnections(addNumber);
		slaveEdge.setLocked(locked);
		masterEdge.setLocked(locked);
	}

	public void setAngle(float angle)
	{
		if (!(this.slaveEdge.getShape().getNumberOfConnections() > 1))
		{
			this.angle = angle;
			connectEdges(this.masterEdge, this.slaveEdge, (float) Math.toRadians(angle));
		}
	}

	public void connectEdges(Edge masterEdge, Edge slaveEdge, float angle) {
		GShape master = masterEdge.getShape();
		GShape slave = slaveEdge.getShape();

		alignEdges(slave, masterEdge, slaveEdge);

		Vec3D toOrigin = slaveEdge.getP3D1().scale(-1);

		slave.translate3D(toOrigin);

		alignShapes(master, slave, masterEdge, slaveEdge);  

		// rotate the slave by the specified angle (currently hardcoded 90 degrees)
		Vec3D rotationAxis = slaveEdge.getP3D2().getNormalized();
		slave.rotateAroundAxis(rotationAxis, angle);

		Vec3D toMaster = masterEdge.getP3D1().sub(slaveEdge.getP3D1());
		slave.translate3D(toMaster);

		Tenon.createOutlineOfEdge(masterEdge, slaveEdge);
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

	public float getAngle() {
		if (angle == 400) angle = (float) Math.toDegrees(calculateAngleBetweenNormals(masterEdge.getShape(), slaveEdge.getShape()));
		return angle;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

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

