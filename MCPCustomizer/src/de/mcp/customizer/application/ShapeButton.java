package de.mcp.customizer.application;

import processing.core.PApplet;
import processing.core.PGraphics;
import controlP5.Button;
import controlP5.ControllerView;
import de.mcp.customizer.view.SVGIcon;

/**
 * A simple extension of the ControlP5-button that displays a predefined icon instead of the standard-button *
 */
public class ShapeButton implements ControllerView<Button> {

  private SVGIcon icon;
  private boolean isSelected;
  private PGraphics graphics;

  /**
  * Creates a ShapeButton
  * 
  * @param icon the icon to be displayed instead of the standard-button
  */
  public ShapeButton(SVGIcon icon, PGraphics graphics)
  {
    super();
    this.graphics = graphics;
    this.icon = icon;
    this.isSelected = false; 
  }

  /* (non-Javadoc)
 * @see controlP5.ControllerView#display(processing.core.PApplet, java.lang.Object)
 */
public void display(PApplet theApplet, Button theButton) 
  {    
	graphics.background(255);	   
	graphics.stroke(140);
    if (theButton.isInside()) 
    {
    	graphics.stroke(0);
    }
    if (this.isSelected)
    {
    	graphics.stroke(0,0,255);
    }
	icon.draw2D(graphics);
    theApplet.image(graphics, 0, 0);
  }

public void setSelected(boolean selected)
{
	this.isSelected = selected;
}
}

