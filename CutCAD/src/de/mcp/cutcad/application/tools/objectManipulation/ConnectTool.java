package de.mcp.cutcad.application.tools.objectManipulation;

import processing.core.PConstants;
import processing.core.PGraphics;
import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.Connection;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.primitives.Edge;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.view.Transformation;

/**
 * The ConnectTool is used to create a connection between two edges.
 */
public class ConnectTool extends Tool {

	boolean selectedFirst;
	Vector2D lastMousePosition;

	Connection previewConnection;

	private float scalingFactor;
	private String lastMessage;

	/**
	 * @param application the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ConnectTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);

		this.selectedFirst = false;
	}

	@Override
	public String getIconName() {
		return "Connect.svg";
	}
	
	public void mouseButtonPressed(Vector2D position, int button) {
		for (Edge e : this.objectContainer.allEdges()) {
			if (e.isHighlighted() && button == PConstants.LEFT) {
				if (!selectedFirst) {
					this.application.displayStatus("Select another edge to connect it to the first edge");
					this.lastMessage = "Select another edge to connect it to the first edge";
					this.previewConnection = new Connection(this.objectContainer);
					this.previewConnection.setMasterEdge(e);
					e.setSelected(true);
					selectedFirst = true;
				} else {
					this.previewConnection.setSlaveEdge(e);
					String connectMessage = this.previewConnection.connect();
					if (connectMessage == "Connection created!") {
						this.application.displayStatus("Connection created! If you want to create another connection, select another edge");
						this.lastMessage = "Connection created! If you want to create another connection, select another edge";

						this.objectContainer
								.addConnection(this.previewConnection);
					} else {
						// TODO: Find out why the connection couldn't be created
						// and tell the user
						this.application.displayStatus("Could not create the connection!");
						this.application.displayStatus(connectMessage);
						this.lastMessage = connectMessage;
					}

					this.previewConnection.getMasterEdge().setSelected(false);
					this.previewConnection = null;
					selectedFirst = false;
				}
			}
		}
	}

	public void mouseMoved(Vector2D position) {
		this.application.displayStatus(this.lastMessage);
		this.lastMousePosition = position;
		Vector2D relativePosition = view.positionRelativeToView(position);
		this.application.displayMousePosition(relativePosition.scale(0.1f));

		for (Edge e : this.objectContainer.allEdges()) {
			if (e.mouseOver(relativePosition))
				this.application.displayStatus("Length of this edge: " + e.getLength() / 10
						+ " mm");
			boolean canBeSelected = e.mouseOver(relativePosition);

			if (this.previewConnection != null) {
				Edge firstEdge = this.previewConnection.getMasterEdge();
				if (firstEdge != null) {
					canBeSelected = canBeSelected
							&& (Math.abs(firstEdge.getLength() - e.getLength()) < 5f);
				}
			}

			e.setHighlighted(canBeSelected);
		}
	}


	public void draw2D(PGraphics p, Transformation t) {
		scalingFactor = t.getScale();

		if (selectedFirst) {
			Vector2D mid = previewConnection
					.getMasterEdge()
					.getMid()
					.add(previewConnection.getMasterEdge().getGShape()
							.getPosition2D());
			p.stroke(255, 0, 0);
			Vector2D lineStart = mid.scale(scalingFactor);
			Vector2D lineEnd = view.positionRelativeToView(this.lastMousePosition)
					.scale(scalingFactor);
			p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
			p.stroke(0);
		}
	}

	@Override
	public void toolWasSelected() {
		this.application.displayStatus("Select an edge to create a connection");
		this.lastMessage = "Select an edge to create a connection";
		super.toolWasSelected();
	}

	@Override
	public void toolWasUnselected() {
		this.application.displayStatus("");
		this.lastMessage = "";
		selectedFirst = false;
		if (this.previewConnection != null) {
			this.previewConnection.getMasterEdge().setSelected(false);
		}
		this.previewConnection = null;
		super.toolWasUnselected();
	}

}
