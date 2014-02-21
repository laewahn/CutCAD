 package de.mcp.customizer.application.tools.objectManipulation;

import processing.core.PConstants;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Cutout;
import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;

public class SelectTool extends Tool {

	boolean dragging, draggingCutout;
	Vector2D originalMousePosition;

	public SelectTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container);
		
		this.dragging = false;
		this.draggingCutout = false;
		this.originalMousePosition = new Vector2D(0, 0);
	}
	
	@Override
	public String getIconName() {
		return "Select.svg";
	}
	
	/**
	 * If the mouse pointer is above a shape, connection etc. and a mouse button is
	 * pressed, this function determines the correct reaction (updates properties bar)
	 * and indicates the corresponding form as selected.
	 * 
	 * @param position the mouse pointer position
	 * @param button check, if left or right button pressed
	 */
	public void mouseButtonPressed(Vector2D position, int button) {
		boolean noneSelected = true;
		for (Shape s : this.objectContainer.allShapes()) {
			if (this.inView(position) && s.getGShape().isSelected()
					&& button == PConstants.LEFT) {
				this.customizer.properties.show();
				this.customizer.properties.plugTo(s);
			} else if (this.inView(position) && s.getGShape().isSelected()
					&& button == PConstants.RIGHT) {
				this.dragging = true;
				// this.originalMousePosition.set(position.sub(new
				// Vector2D(view2DPosX, view2DPosY)));
				Vector2D currentMousePosition = this
						.positionRelativeToView(position);
				this.originalMousePosition.set(currentMousePosition);
				noneSelected = false;
			}
		}
		for (Cutout c : this.objectContainer.allCutouts()) {
			if (this.inView(position) && c.isSelected()
					&& button == PConstants.LEFT) {
				this.customizer.properties.show();
				this.customizer.properties.plugTo(c);
			} else if (this.inView(position) && c.isSelected()
					&& button == PConstants.RIGHT) {
				this.draggingCutout = true;
				Vector2D currentMousePosition = this
						.positionRelativeToView(position);
				this.originalMousePosition.set(currentMousePosition);
				noneSelected = false;
			}
		}
		for (Connection c : this.objectContainer.allConnections()) {
			if (this.inView(position) && c.isSelected()
					&& button == PConstants.LEFT) {
				this.customizer.properties.show();
				this.customizer.properties.plugTo(c);
			}
		}
		if (this.inView(position) && button == PConstants.RIGHT && noneSelected) {
			this.dragging = true;
			Vector2D currentMousePosition = this.positionRelativeToView(position);
			this.originalMousePosition.set(currentMousePosition);
		}
	}

	public void mouseButtonReleased(Vector2D position, int button) {
		if (button == PConstants.RIGHT) {
			this.dragging = false;
			this.draggingCutout = false;
		}
	}

	/**
	 * Checks (and set true) if the mouse pointer is above a shape, cutout edge etc
	 * and - if a button is additionally pressed - translates object positions corresponding 
	 * to a basic drag and drop functionality
	 * 
	 * @param position mouse pointer position
	 */
	public void mouseMoved(Vector2D position) {
		if (this.inView(position)) {
			Vector2D relativePosition = this.positionRelativeToView(position);
	        this.customizer.displayMousePosition(relativePosition.scale(0.1f));

			boolean noneSelected = true;
			for (Shape s : this.objectContainer.allShapes()) {
				s.getGShape().setSelected(
						s.getGShape().mouseOver(relativePosition));

				if (s.getGShape().isSelected() && this.dragging) {
					Vector2D currentMousePosition = this
							.positionRelativeToView(position);
					s.getGShape().translate2D(
							currentMousePosition.sub(originalMousePosition));
					originalMousePosition.set(currentMousePosition);
					noneSelected = false;
				}
			}
			for (Cutout c : this.objectContainer.allCutouts()) {
				c.setSelected(c.mouseOver(relativePosition));
				if (c.isSelected())
				{
					c.getMasterShape().getGShape().setSelected(false);
				}

				if (c.isSelected() && this.draggingCutout) {
					Vector2D currentMousePosition = this
							.positionRelativeToView(position);
					c.translate2D(
							currentMousePosition.sub(originalMousePosition));
					originalMousePosition.set(currentMousePosition);
					noneSelected = false;
				}
			}
			for (Connection c : this.objectContainer.allConnections()) {
				c.setSelected(c.mouseOver(relativePosition));

				if (c.isSelected()) {
					noneSelected = false;
				}
			}
			if (noneSelected && this.dragging) {
				Vector2D currentMousePosition = this
						.positionRelativeToView(position);

				this.customizer.transform2D.translate(currentMousePosition
						.sub(originalMousePosition));
				originalMousePosition
						.set(this.positionRelativeToView(position));
			}
		} else {
			for (Shape s : this.objectContainer.allShapes()) {
				s.getGShape().setSelected(false);
			}
		}
	}
	
	@Override
	public void toolWasSelected() {
		this.customizer.displayStatus("Click left on a shape to select it, drag a shape with the right mouse button to move it and drag anywhere on the 2D view to move the camera");
		super.toolWasSelected();
	}

	@Override
	public void toolWasUnselected() {
		this.customizer.displayStatus("");
		this.customizer.properties.hide();
		super.toolWasUnselected();
	}
}
