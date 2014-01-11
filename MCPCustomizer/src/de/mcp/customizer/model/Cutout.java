package de.mcp.customizer.model;
import java.util.ArrayList;

import de.mcp.customizer.view.Drawable2D;
import processing.core.PGraphics;
import toxi.geom.*;

public class Cutout implements Drawable2D
{
	private Vec2D position;
	private float angle;
	private GShape master, slave;
	private boolean isSelected;

	private static ArrayList<Cutout> allCutouts = new ArrayList<Cutout>();

	public Cutout(GShape master, GShape slave) 
	{
		this.master = master;
		this.slave = slave;
		this.angle = 0;
		this.position = new Vec2D(0,0);
		this.setSelected(false);
		allCutouts.add(this);
	}

	public static ArrayList<Cutout> getAllCutouts()
	{
		return allCutouts;
	}

	public void setSelected(boolean b) 
	{
		this.isSelected = b;
	}

	public boolean isSelected()
	{
		return isSelected;
	}

	public ArrayList<Vec2D> getVectors() 
	{
		ArrayList<Vec2D> modifiedVectors = new ArrayList<Vec2D>();

		Polygon2D findCenter = new Polygon2D();
		for (Vec2D v : slave.getTenons()) findCenter.add(v.copy());
		Vec2D center = findCenter.getCentroid();
		for (Vec2D v : slave.getTenons())
		{
			modifiedVectors.add(v.copy().sub(center).rotate((float) Math.toRadians(angle)).add(position).add(center));
		}
		return modifiedVectors;
	}

	public int getPositionXCutout() 
	{
		return (int) position.x();
	}
	
	public int getPositionYCutout() 
	{
		return (int) position.y();
	}

	public void setPositionXCutout(int position) 
	{
		this.position.set(position, this.position.y());
	}
	
	public void setPositionYCutout(int position) 
	{
		this.position.set(this.position.x(), position);
	}

	public float getAngle() 
	{
		return angle;
	}

	public void setAngle(float angle) 
	{
		this.angle = angle;
	}

	@Override
	public void draw2D(PGraphics p) {
		this.drawCutout(p);
	}
	
	private void drawCutout(PGraphics p)
	{
		Vec2D mid1 = findCenter(slave).add(master.getPosition2D()).add(this.position);
		Vec2D mid2 = findCenter(slave).add(slave.getPosition2D());
		if (this.isSelected)
		{
			p.stroke(255, 0, 0);
		}
		else
		{
			p.stroke(0, 60, 0);
		}
		p.line(mid1.x(), mid1.y(), mid2.x(), mid2.y());
		p.stroke(0);
	}

	public Vec2D findCenter(GShape shape)
	{
		Polygon2D findCenter = new Polygon2D();
		for (Edge e : shape.getEdges()) findCenter.add(e.getV1().copy());
		Vec2D center = findCenter.getCentroid();
		return center;
	}

	public boolean mouseOver(Vec2D position)
	{
		Vec2D mid1 = findCenter(slave).add(master.getPosition2D()).add(this.position);
		Vec2D mid2 = findCenter(slave).add(slave.getPosition2D());

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

	public void removeCutout() {
		master.removeCutout(this);
		
	}
}
