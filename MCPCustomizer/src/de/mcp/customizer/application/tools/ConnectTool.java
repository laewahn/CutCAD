package de.mcp.customizer.application.tools;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Vec2D;
import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Edge;
import de.mcp.customizer.model.ObjectContainer;

/**
 * The ConnectTool is used to create a connection between two edges.
 */
public class ConnectTool extends Tool {

	boolean selectedFirst;
	Vec2D lastMousePosition;

	Connection previewConnection;

	private float scalingFactor;
	private String lastMessage;

	/**
	 * @param mcpCustomizer the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ConnectTool(MCPCustomizer mcpCustomizer, ObjectContainer container) {
		super(mcpCustomizer, container, "Connect.svg");

		this.selectedFirst = false;
	}

	public void mouseButtonPressed(Vec2D position, int button) {
		for (Edge e : this.objectContainer.allEdges()) {
			if (e.isHighlighted() && button == PConstants.LEFT) {
				if (!selectedFirst) {
					this.customizer.displayStatus("Select another edge to connect it to the first edge");
					this.lastMessage = "Select another edge to connect it to the first edge";
					this.previewConnection = new Connection(
							this.objectContainer.allConnections());
					this.previewConnection.setMasterEdge(e);
					e.setSelected(true);
					selectedFirst = true;
				} else {
					this.previewConnection.setSlaveEdge(e);
					String connectMessage = this.previewConnection.connect();
					if (connectMessage == "Connection created!") {
						this.customizer.displayStatus("Connection created! If you want to create another connection, select another edge");
						this.lastMessage = "Connection created! If you want to create another connection, select another edge";
						// this.connections.add(this.previewConnection);
						this.objectContainer
								.addConnection(this.previewConnection);
					} else {
						// TODO: Find out why the connection couldn't be created
						// and tell the user
						this.customizer.displayStatus("Could not create the connection!");
						this.customizer.displayStatus(connectMessage);
						this.lastMessage = connectMessage;
					}
					// println("Added Connection between " +
					// this.previewConnection.getEdge1() + " and " +
					// this.previewConnection.getEdge2());
					this.previewConnection.getMasterEdge().setSelected(false);
					this.previewConnection = null;
					selectedFirst = false;
				}
			}
		}
	}

	public void mouseButtonReleased(Vec2D position, int button) {
		// no actions required
	}

	public void mouseMoved(Vec2D position) {
		this.customizer.displayStatus(this.lastMessage);
		this.lastMousePosition = position;
		Vec2D relativePosition = this.positionRelativeToView(position);
		this.customizer.displayMousePosition(relativePosition.scale(0.1f));

		for (Edge e : this.objectContainer.allEdges()) {
			if (e.mouseOver(relativePosition))
				this.customizer.displayStatus("Length of this edge: " + e.getLength() / 10
						+ " mm");
			boolean canBeSelected = e.mouseOver(relativePosition);

			if (this.previewConnection != null) {
				Edge firstEdge = this.previewConnection.getMasterEdge();
				if (firstEdge != null) {
					// canBeSelected = canBeSelected && (firstEdge.getLength()
					// == e.getLength());
					canBeSelected = canBeSelected
							&& (Math.abs(firstEdge.getLength() - e.getLength()) < 5f);
				}
			}

			e.setHighlighted(canBeSelected);
		}
	}

	public void draw2D(PGraphics p) {
		scalingFactor = super.getScalingFactor();
		if (selectedFirst) {
			Vec2D mid = previewConnection
					.getMasterEdge()
					.getMid()
					.add(previewConnection.getMasterEdge().getGShape()
							.getPosition2D());
			p.stroke(255, 0, 0);
			Vec2D lineStart = mid.scale(scalingFactor);
			Vec2D lineEnd = this.positionRelativeToView(this.lastMousePosition)
					.scale(scalingFactor);
			p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
			p.stroke(0);
		}
	}

	@Override
	public void wasSelected() {
		this.customizer.displayStatus("Select an edge to create a connection");
		this.lastMessage = "Select an edge to create a connection";
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.customizer.displayStatus("");
		this.lastMessage = "";
		selectedFirst = false;
		if (this.previewConnection != null) {
			this.previewConnection.getMasterEdge().setSelected(false);
		}
		this.previewConnection = null;
		super.wasUnselected();
	}

}
