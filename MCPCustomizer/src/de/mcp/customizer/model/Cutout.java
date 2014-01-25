package de.mcp.customizer.model;

import java.util.ArrayList;
import de.mcp.customizer.view.Drawable2D;
import processing.core.PGraphics;
import toxi.geom.*;

public class Cutout implements Drawable2D {
	private Vec2D position;
	private float angle;
	private GShape master, slave;
	private boolean isSelected, isActive;
	private float scalingFactor = 0.5f;
	private float boundingBoxSize = 4 / scalingFactor;

//	private static ArrayList<Cutout> allCutouts = new ArrayList<Cutout>();

	public Cutout(GShape master, GShape slave) {
		this.master = master;
		this.slave = slave;
		slave.setMaterial(AllMaterials.getMaterials().get(0));
		this.angle = 0;
		this.position = new Vec2D(0, 0);
		this.isSelected = false;
		this.isActive = false;
//		allCutouts.add(this);
	}

	public void translate2D(Vec2D direction) {
		this.position.addSelf(direction);
	}

	public Cutout copyFor(GShape newMaster) {
		Cutout copy = new Cutout(newMaster, this.slave);
		copy.setAngle(this.getAngle());
		copy.setPositionXCutout(this.getPositionXCutout());
		copy.setPositionYCutout(this.getPositionYCutout());
		this.isSelected = false;
		this.isActive = false;
		return copy;
	}

	public Shape getMasterShape() {
		return this.master.getParent();
	}

	public Shape getSlaveShape() {
		return this.slave.getParent();
	}

//	public static ArrayList<Cutout> getAllCutouts() {
//		return allCutouts;
//	}

	public void setSelected(boolean b) {
		this.isSelected = b;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public ArrayList<Vec2D> getVectors() {
		ArrayList<Vec2D> modifiedVectors = new ArrayList<Vec2D>();

		Polygon2D findCenter = new Polygon2D();
		for (Vec2D v : slave.getTenons())
			findCenter.add(v.copy());
		Vec2D center = findCenter.getCentroid();
		for (Vec2D v : slave.getTenons()) {
			modifiedVectors.add(v.copy().sub(center)
					.rotate((float) Math.toRadians(angle)).add(position)
					.add(center));
		}
		return modifiedVectors;
	}

	public int getNumberOfControls() {
		return 3;
	}

	public int getValue(int index) {
		if (index == 0)
			return getPositionXCutout();
		else if (index == 1)
			return getPositionXCutout();
		else
			return (int) getAngle();
	}

	public int getControlType(int index) {
		if (index == 0)
			return 1;
		else if (index == 1)
			return 1;
		else
			return 0;
	}

	public String getNameOfControl(int index) {
		if (index == 0)
			return "X-PositionX";
		else if (index == 1)
			return "Y-Position";
		else
			return "Angle";
	}

	public void setValue0(int size) {
		setPositionXCutout(size);
	}

	public void setValue1(int size) {
		setPositionYCutout(size);
	}

	public void setValue2(int size) {
		setAngle(size);
	}

	public int getPositionXCutout() {
		return (int) position.x();
	}

	public int getPositionYCutout() {
		return (int) position.y();
	}

	public void setPositionXCutout(int position) {
		this.position.set(position, this.position.y());
	}

	public void setPositionYCutout(int position) {
		this.position.set(this.position.x(), position);
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void draw2D(PGraphics p) {
		this.drawCutout(p);
	}

	private void drawCutout(PGraphics p) {
		Vec2D mid1 = findCenter(slave).add(master.getPosition2D())
				.add(this.position).scale(scalingFactor);
		Vec2D mid2 = findCenter(slave).add(slave.getPosition2D()).scale(
				scalingFactor);
		if (this.isSelected) {
			p.stroke(255, 0, 0);
		} else if (this.isActive) {
			p.stroke(125, 0, 0);
		} else {
			p.stroke(0, 60, 0);
		}
		p.line(mid1.x(), mid1.y(), mid2.x(), mid2.y());
		p.stroke(0);
	}

	public Vec2D findCenter(GShape shape) {
		Polygon2D findCenter = new Polygon2D();
		for (Edge e : shape.getEdges())
			findCenter.add(e.getV1().copy());
		Vec2D center = findCenter.getCentroid();
		return center;
	}

	public boolean mouseOver(Vec2D mousePosition) {
		Vec2D mid1 = findCenter(slave).add(master.getPosition2D()).add(
				this.position);
		Vec2D mid2 = findCenter(slave).add(slave.getPosition2D());

		// create a vector that is perpendicular to the connections line
		Vec2D perpendicularVector = mid1.sub(mid2).perpendicular()
				.getNormalizedTo(boundingBoxSize);

		// with the perpendicular vector, calculate the defining points of a
		// rectangle around the connections line
		ArrayList<Vec2D> definingPoints = new ArrayList<Vec2D>();
		for (Vec2D v : getVectors()) {
			definingPoints.add(v.add(master.getPosition2D()));
		}

		ArrayList<Vec2D> definingPointsLine = new ArrayList<Vec2D>();
		definingPointsLine.add(mid1.sub(perpendicularVector).scale(
				scalingFactor));
		definingPointsLine.add(mid2.sub(perpendicularVector).scale(
				scalingFactor));
		definingPointsLine.add(mid2.add(perpendicularVector).scale(
				scalingFactor));
		definingPointsLine.add(mid1.add(perpendicularVector).scale(
				scalingFactor));

		// create a rectangle around the edge
		Polygon2D borders = new Polygon2D(definingPoints);
		if (borders.containsPoint(mousePosition)) {
			return true;
		} else {
			borders = new Polygon2D(definingPointsLine);
			// check if the mousePointer is within the created rectangle
			return borders.containsPoint(mousePosition.scale(scalingFactor));
		}
	}

	public void removeCutout() {
		master.removeCutout(this);

	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void scale2D(float scaleFactor) {
		this.slave.scale2D(scaleFactor);
	}
}
