package de.mcp.customizer.application.tools;
import java.util.List;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Edge;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Transformation;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public class ConnectTool extends Tool
{

    boolean selectedFirst;
    Vec2D lastMousePosition;

    Connection previewConnection;
    List<Connection> connections;

    List<Shape> shapes;
    

    public ConnectTool(Rect view, Properties properties, Statusbar statusbar, List<Shape> shapes, List<Connection> connections, Transformation transform)
    {
        super(view, properties, statusbar, transform, "ConnectTool");
        this.shapes = shapes;
        this.selectedFirst = false;
        this.connections = connections;
    }

    public PGraphics getIcon(PGraphics context) 
    {
        context.beginDraw();
        context.noFill();
        context.stroke(0);
        context.strokeWeight(2);
        context.line(25, 10, 25, 40);
        context.line(25, 25, 125, 25);
        context.line(125, 10, 125, 40);
        context.endDraw();

        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
        for (Shape s : shapes)
        {
            for (Edge e : s.getShape().getEdges())
            {
                if (e.isHighlighted() && button == PConstants.LEFT)
                {
                    if (!selectedFirst)
                    {
                		this.displayStatus("Select another edge to connect it to the first edge");
                        this.previewConnection = new Connection(connections);
                        this.previewConnection.setMasterEdge(e);
                        e.setSelected(true);
                        selectedFirst = true;                        
                    }
                    else
                    {
                        this.previewConnection.setSlaveEdge(e);
                        if(this.previewConnection.connect()) 
                        {
                    		this.displayStatus("Connection created! If you want to create another connection, select another edge");
                            this.connections.add(this.previewConnection);
                        }
                        else
                        {
                        	// TODO: Find out why the connection couldn't be created and tell the user
                    		this.displayStatus("Could not create the connection!");
                        }
                        // println("Added Connection between " + this.previewConnection.getEdge1() + " and " + this.previewConnection.getEdge2());
                        this.previewConnection.getMasterEdge().setSelected(false);
                        this.previewConnection = null;
                        selectedFirst = false;
                    }
                }
            }
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        // no actions required
    }   
    
    public void mouseMoved(Vec2D position)
    {
        this.lastMousePosition = position;
        Vec2D relativePosition = this.positionRelativeToView(position);
        this.updateMousePositon(relativePosition.scale(0.1f));
    
        for (Shape s : shapes)
        {
            for (Edge e : s.getShape().getEdges())
            {
                boolean canBeSelected = e.mouseOver(relativePosition);
                
                if(this.previewConnection != null) {
                	Edge firstEdge = this.previewConnection.getMasterEdge(); 
                	if(firstEdge != null) {
                		//canBeSelected = canBeSelected && (firstEdge.getLength() == e.getLength());
                		canBeSelected = canBeSelected && (Math.abs(firstEdge.getLength()-e.getLength()) < 5f);
                	}
                }
                
                e.setHighlighted(canBeSelected);
            }
        }
    }

    public void draw2D(PGraphics p)
    {
        if (selectedFirst) {
            Vec2D mid = previewConnection.getMasterEdge().getMid().add(previewConnection.getMasterEdge().getShape().getPosition2D());
            p.stroke(255,0,0);
            Vec2D lineStart = mid;
            Vec2D lineEnd = this.positionRelativeToView(this.lastMousePosition);
            p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
            p.stroke(0);
        }
    }
    
    

	@Override
	public void wasSelected() {
		this.displayStatus("Select an edge to create a connection");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.displayStatus("");
		selectedFirst = false;
        if(this.previewConnection != null)
        {
        	this.previewConnection.getMasterEdge().setSelected(false);
        }
		this.previewConnection = null;
		super.wasUnselected();
	}
    
    

}
