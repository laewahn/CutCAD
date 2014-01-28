package de.mcp.customizer.application.tools;

import processing.core.PConstants;
import processing.core.PGraphics;

import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Edge;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.Shape;


/**
 * The CutoutTool is used to set a Shape to be cut out from another Shape
 */
public class CutoutTool extends Tool {

    boolean dragging;
    boolean selectedFirst;
    Vec2D originalMousePosition;
    Vec2D relativePosition;
    Shape masterShape;
    private float scalingFactor;
    
    /**
     * @param customizer the main class of the project
     * @param container the currently loaded ObjectContainer
     */
    public CutoutTool(MCPCustomizer customizer, ObjectContainer container) {
    	super(customizer, container, "Cutout.svg");
    	
    	this.dragging = false;
        this.selectedFirst = false;
        this.originalMousePosition = new Vec2D(0,0);
    }
    
    public void mouseButtonPressed(Vec2D position, int button)
    {
        for (Shape s : this.objectContainer.allShapes())
        {
            if (this.inView(position) && button == PConstants.LEFT)
            {
            	if (!selectedFirst && s.getGShape().isSelected() )
            	{
            		this.customizer.displayStatus("Now select the shape you want to add as a cutout");
            		s.getGShape().setSelected(true);
            		Vec2D currentMousePosition = this.positionRelativeToView(position);
                    this.originalMousePosition.set(currentMousePosition);
            		masterShape = s;
            		selectedFirst = true;
            	}
            	else if (selectedFirst && s.getGShape().isSelected() && s != masterShape )
            	{
            		this.customizer.displayStatus("Cutout created! If you want to create another cutout, select the shape you want to add a cutout to");
            		masterShape.getGShape().addCutout(s.getGShape());
            		selectedFirst = false;
            	}
            }
        }
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
        if (button == PConstants.RIGHT) {
            this.dragging = false;
        }
    }
    
    public void mouseMoved(Vec2D position)
    {			
        if (this.inView(position))
        {
            relativePosition = this.positionRelativeToView(position);
	        this.customizer.displayMousePosition(relativePosition.scale(0.1f));

            for (Shape s : this.objectContainer.allShapes()) {
                s.getGShape().setSelected(s.getGShape().mouseOver(relativePosition));
            }
        }
    }
    
    public void draw2D(PGraphics p)
    {
        scalingFactor = super.getScalingFactor();
        if (selectedFirst) {	
    		Polygon2D findCenter = new Polygon2D();
    		for (Edge e : masterShape.getGShape().getEdges()) findCenter.add(e.getV1().copy());
    		Vec2D center = findCenter.getCentroid();
            Vec2D mid = center.add(masterShape.getGShape().getPosition2D());
            p.stroke(255,0,0);
            Vec2D lineStart = mid.scale(scalingFactor);
            Vec2D lineEnd = this.relativePosition.scale(scalingFactor);
            p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
            p.stroke(0);
        }
    }

	@Override
	public void wasSelected() {
		this.customizer.displayStatus("First, select the shape you want to add a cutout to");
		super.wasSelected();
	}

	@Override
	public void wasUnselected() {
		this.customizer.displayStatus("");
		selectedFirst = false;
		super.wasUnselected();
	}
    
}
