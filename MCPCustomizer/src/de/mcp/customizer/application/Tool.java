package de.mcp.customizer.application;

import java.util.Timer;
import java.util.TimerTask;

import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.DrawingView2D;
import de.mcp.customizer.view.SVGIcon;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;

/**
 * Template class for the implementation of a tool. Provides a set of event-based methods that need to be overriden to achieve custom behaviour.
 * Tools can be informed about mouse events (mouseButtonPressed, mouseButtonReleased, mouseMoved) and tool selection events (toolWasSelected, toolWasUnselected).
 * 
 * Tools are by now associated with the 2D drawing view of the application. By overriding the draw2D method, it is possible for a tool to draw its own content into
 * the 2D drawing view.
 * 
 * @author dennis
 *
 */
public abstract class Tool implements Drawable2D {

	/**
	 * The 2D drawing view of the application.
	 */
    protected DrawingView2D view;
    
    /**
     * A reference to the application itself.
     */
    protected MCPCustomizer customizer;
    
    /**
     * A reference to the object container.
     */
    protected ObjectContainer objectContainer;
    
    private ShapeButton button;
    
    /**
     * Default constructor for a tool. 
     * @param customizer Reference to the application.
     * @param container Reference to the object container.
     */
    public Tool(MCPCustomizer customizer, ObjectContainer container) {
 
    	this.view = customizer.drawingView2D;
    	this.customizer = customizer;
    	this.objectContainer = container;
    	
		PGraphics p = this.customizer.createGraphics(50, 50);
		this.button = new ShapeButton(this.getIcon(), p, this.view.getTransformation());
    }
    
    /**
     * Return the name of the SVG for the icon for the tool. This method will be called on initialization to load the icon for later representation.
     * @return The fully qualified name of the SVG of the icon.
     */
    public abstract String getIconName();
    
    /**
     * Will be called by the application when a mouse button was pressed.
     * @param position The absolute position of the mouse cursor.
     * @param button The button that was pressed.
     */
    public void mouseButtonPressed(Vector2D position, int button) {};
    
    /**
     * Will be called by the application when a mouse button was released.
     * @param position The absolute position of the mouse cursor.
     * @param button The button that was released.
     */
    public void mouseButtonReleased(Vector2D position, int button) {};
    
    /**
     * Will be called by the application when the mouse was moved.
     * @param position The absolute position of the mouse cursor.
     */
    public void mouseMoved(Vector2D position) {};

    /**
     * Will be called by the application when this tool was selected.
     */
	public void toolWasSelected() {
		this.objectContainer.setSelectedTool(this);
		this.button.setSelected(true);
		
		if(!this.canStaySelected()) {
			
			// In order to show some feedback on the clicking of the button a timer
			// is started to keep the tool visibly selected for 100ms.
			final Timer unselectTimer = new Timer();
			unselectTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					toolWasUnselected();					
				};
			}, 100);	
		}
	};

	/**
	 * Will be called by the application when another tool was selected.
	 */
	public void toolWasUnselected() {
		this.button.setSelected(false);
	};

	/**
	 * Will be called by the drawing runloop of the application whenever the screen is redrawn.
	 * @param p The graphics context to draw into.
	 * @param transform The transformation currently applied to the context.
	 */
	public void draw2D(PGraphics p, Transformation transform) {};	
	
	/**
	 * Return whether this tool can be selected for a longer time (such as the drawing tools or select tool)
	 * or whether it is a click-and-forget-tool (such as the save, load or new tool).
	 * @return Return whether the tool will stay selected until a new tool is selected.
	 */
	public boolean canStaySelected() {
		return true;
	}

    /**
     * Returns the button to select this tool.
     * @return The button to select this tool.
     */
    public ShapeButton getButton() {
		return this.button;
	}
    
    /**
     * Loads the SVGIcon for the tool. Uses getIconName in order to get the icons file name.
     * @return A SVGIcon with the tools icon loaded from the icon SVG file.
     */
	private SVGIcon getIcon() {

		float iconScaling = 1.57f;

		SVGIcon icon = new SVGIcon(this.getIconName(), iconScaling);
		return icon;
	}	
}
