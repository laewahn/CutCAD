package de.mcp.cutcad.application.tools.objectManipulation;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.Polygon2D;
import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.primitives.Edge;
import de.mcp.cutcad.model.primitives.Shape;
import de.mcp.cutcad.model.primitives.Vector2D;
import de.mcp.cutcad.view.Transformation;


/**
 * The CutoutTool is used to set a Shape to be cut out from another Shape
 */
public class CutoutTool extends Tool {

    boolean dragging;
    boolean selectedFirst;
    Vector2D originalMousePosition;
    Vector2D relativePosition;
    Shape masterShape;
    private float scalingFactor;
    
    /**
     * @param application the main class of the project
     * @param container the currently loaded ObjectContainer
     */
    public CutoutTool(CutCADApplet application, ObjectContainer container) {
    	super(application, container);
    	
    	this.dragging = false;
        this.selectedFirst = false;
        this.originalMousePosition = new Vector2D(0,0);
    }
    
    @Override
	public String getIconName() {
		return "Cutout.svg";
	}
    
    public void mouseButtonPressed(Vector2D position, int button)
    {
        for (Shape s : this.objectContainer.allShapes())
        {
            if (view.containsPoint(position) && button == PConstants.LEFT)
            {
            	if (!selectedFirst && s.getGShape().isSelected() )
            	{
            		this.application.displayStatus("Now select the shape you want to add as a cutout");
            		s.getGShape().setSelected(true);
            		Vector2D currentMousePosition = view.positionRelativeToView(position);
                    this.originalMousePosition.set(currentMousePosition);
            		masterShape = s;
            		selectedFirst = true;
            	}
            	else if (selectedFirst && s.getGShape().isSelected() && s != masterShape )
            	{
            		this.application.displayStatus("Cutout created! If you want to create another cutout, select the shape you want to add a cutout to");
            		masterShape.getGShape().addCutout(s.getGShape());
            		selectedFirst = false;
            	}
            }
        }
    }

    public void mouseButtonReleased(Vector2D position, int button)
    {
        if (button == PConstants.RIGHT) {
            this.dragging = false;
        }
    }
    
    public void mouseMoved(Vector2D position)
    {			
        if (view.containsPoint(position))
        {
            relativePosition = view.positionRelativeToView(position);
	        this.application.displayMousePosition(relativePosition.scale(0.1f));

            for (Shape s : this.objectContainer.allShapes()) {
                s.getGShape().setSelected(s.getGShape().mouseOver(relativePosition));
            }
        }
    }
    
    public void draw2D(PGraphics p, Transformation t)
    {
    	scalingFactor = t.getScale();
        if (selectedFirst) {	
    		Polygon2D findCenter = new Polygon2D();
    		for (Edge e : masterShape.getGShape().getEdges()) findCenter.add(e.getV1().copy().getVec2D());
    		Vector2D center = new Vector2D(findCenter.getCentroid());
            Vector2D mid = center.add(masterShape.getGShape().getPosition2D());
            p.stroke(255,0,0);
            Vector2D lineStart = mid.scale(scalingFactor);
            Vector2D lineEnd = this.relativePosition.scale(scalingFactor);
            p.line(lineStart.x(), lineStart.y(), lineEnd.x(), lineEnd.y());
            p.stroke(0);
        }
    }

	@Override
	public void toolWasSelected() {
		this.application.displayStatus("First, select the shape you want to add a cutout to");
		super.toolWasSelected();
	}

	@Override
	public void toolWasUnselected() {
		this.application.displayStatus("");
		selectedFirst = false;
		super.toolWasUnselected();
	}
    
}
