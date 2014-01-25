 package de.mcp.customizer.application.tools;

import geomerative.RG;
import geomerative.RPoint;

import java.io.File;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;

public class SelectTool extends Tool {

	List<Shape> shapes;
	List<Connection> connections;
	boolean dragging, draggingCutout;
	Vec2D originalMousePosition;

	public SelectTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes,
			List<Connection> connections, Transformation transform) {
		super(view, properties, statusbar, transform, "SelectTool");

		this.shapes = shapes;
		this.connections = connections;
		this.dragging = false;
		this.draggingCutout = false;
		this.originalMousePosition = new Vec2D(0, 0);
	}

	public PGraphics getIcon(PGraphics context) {
		float iconScaling = 1.57f;
		RPoint[][] pointPaths;
		
		context.beginDraw();
		context.fill(0);
		context.strokeWeight(1);

		pointPaths = RG.loadShape("icons" + File.separator + "Select.svg").getPointsInPaths();
 
		for(int i = 0; i<pointPaths.length; i++){
		    if (pointPaths[i] != null) {
		    	context.beginShape();
		      for(int j = 0; j<pointPaths[i].length; j++){
		    	  context.vertex(pointPaths[i][j].x*iconScaling, pointPaths[i][j].y*iconScaling);
		      }
		      context.endShape();
		    }
		  }
		context.endDraw();
		return context;
//		context.beginDraw();
//		context.noFill();
//		context.stroke(0);
//		context.strokeWeight(1);
//		context.translate(10, 10);
//		context.rotate(PApplet.radians(-45));
//		context.triangle(0, 0, -10, 30, 10, 30);
//		context.rect(-5, 30, 10, 10);
//		context.endDraw();
//
//		return context;
	}

	public void mouseButtonPressed(Vec2D position, int button) {
		boolean noneSelected = true;
		for (Shape s : shapes) {
			if (this.inView(position) && s.getShape().isSelected()
					&& button == PConstants.LEFT) {
				properties.show();
				properties.plugTo(s);
			} else if (this.inView(position) && s.getShape().isSelected()
					&& button == PConstants.RIGHT) {
				this.dragging = true;
				// this.originalMousePosition.set(position.sub(new
				// Vec2D(view2DPosX, view2DPosY)));
				Vec2D currentMousePosition = this
						.positionRelativeToView(position);
				this.originalMousePosition.set(currentMousePosition);
				noneSelected = false;
			}
		}
		for (Cutout c : Cutout.getAllCutouts()) {
			if (this.inView(position) && c.isSelected()
					&& button == PConstants.LEFT) {
				properties.show();
				properties.plugTo(c);
			} else if (this.inView(position) && c.isSelected()
					&& button == PConstants.RIGHT) {
				this.draggingCutout = true;
				Vec2D currentMousePosition = this
						.positionRelativeToView(position);
				this.originalMousePosition.set(currentMousePosition);
				noneSelected = false;
			}
		}
		for (Connection c : connections) {
			if (this.inView(position) && c.isSelected()
					&& button == PConstants.LEFT) {
				properties.show();
				properties.plugTo(c);
			}
		}
		if (this.inView(position) && button == PConstants.RIGHT && noneSelected) {
			this.dragging = true;
			Vec2D currentMousePosition = this.positionRelativeToView(position);
			this.originalMousePosition.set(currentMousePosition);
		}
	}

	public void mouseButtonReleased(Vec2D position, int button) {
		if (button == PConstants.RIGHT) {
			this.dragging = false;
			this.draggingCutout = false;
		}
	}

	public void mouseMoved(Vec2D position) {
		if (this.inView(position)) {
			Vec2D relativePosition = this.positionRelativeToView(position);
	        this.updateMousePositon(relativePosition.scale(0.1f));

			boolean noneSelected = true;
			for (Shape s : shapes) {
				s.getShape().setSelected(
						s.getShape().mouseOver(relativePosition));

				if (s.getShape().isSelected() && this.dragging) {
					Vec2D currentMousePosition = this
							.positionRelativeToView(position);
					s.getShape().translate2D(
							currentMousePosition.sub(originalMousePosition));
					originalMousePosition.set(currentMousePosition);
					noneSelected = false;
				}
			}
			for (Cutout c : Cutout.getAllCutouts()) {
				c.setSelected(c.mouseOver(relativePosition));
				if (c.isSelected())
				{
					c.getMasterShape().getShape().setSelected(false);
				}

				if (c.isSelected() && this.draggingCutout) {
					Vec2D currentMousePosition = this
							.positionRelativeToView(position);
					c.translate2D(
							currentMousePosition.sub(originalMousePosition));
					originalMousePosition.set(currentMousePosition);
					noneSelected = false;
				}
			}
			for (Connection c : connections) {
				c.setSelected(c.mouseOver(relativePosition));

				if (c.isSelected()) {
					noneSelected = false;
				}
			}
			if (noneSelected && this.dragging) {
				Vec2D currentMousePosition = this
						.positionRelativeToView(position);

				transform.translate(currentMousePosition
						.sub(originalMousePosition));
				originalMousePosition
						.set(this.positionRelativeToView(position));
			}
		} else {
			for (Shape s : shapes) {
				s.getShape().setSelected(false);
			}
		}
	}
	
	

	@Override
	public void wasSelected() {
		this.displayStatus("Click left on a shape to select it, drag a shape with the right mouse button to move it and drag anywhere on the 2D view to move the camera");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		super.wasUnselected();
		this.properties.hide();
	}
}
