package de.mcp.customizer.application;

import de.mcp.customizer.model.primitives.Vector2D;
import processing.core.PApplet;
//import toxi.geom.Vector2D;

/**
 * A simple statusbar at the bottom of the UI, displaying a status message that
 * can be set with setStatus() and the current Position of the mouse, which can
 * be set with setMousePosition().
 */
public class Statusbar {

	private String status;
	private Vector2D mousePosition;

	/**
	 * Creates a Statusbar.
	 */
	public Statusbar() {
		this.mousePosition = new Vector2D(0, 0);
		this.status = null;
	}

	/**
	 * @return the message currently displayed by the statusbar
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the message currently displayed by the statusbar to status
	 * 
	 * @param status
	 *            the message that
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Sets the mouse position to be displayed on the statusbar.
	 * 
	 * @param mouse
	 *            the mouse position to be displayed on the statusbar
	 */
	public void setMousePosition(Vector2D mouse) {
		this.mousePosition = mouse;
	}

	/**
	 * Draws the statusbar on the PApplet p
	 * 
	 * @param p
	 *            the PApplet to draw the statusbar on
	 */
	public void drawStatusbar(PApplet p) {
		if (status != null) {
			p.fill(0);
			p.textSize(14);
			p.text(status, 10, p.height - 10);
			int mousePositionWidth = (int) p.textWidth("("
					+ (int) mousePosition.x() + "," + (int) mousePosition.y()
					+ ")");
			p.text("(" + (int) mousePosition.x() + ","
					+ (int) mousePosition.y() + ")", p.width
					- mousePositionWidth - 10, p.height - 10);
		}
	}

}
