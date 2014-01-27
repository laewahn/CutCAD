package de.mcp.customizer.application;

import processing.core.PApplet;
import processing.core.PGraphics;

import controlP5.Button;
import controlP5.ControllerView;

/**
 * A simple extension of the ControlP5-button that displays a predefined icon instead of the standard-button *
 */
public class ShapeButton implements ControllerView<Button> {

  private PGraphics icon;

  /**
  * Constructor
  * 
  * Creates a ShapeButton
  * 
  * @param icon the icon to be displayed instead of the standard-button
  */
  public ShapeButton(PGraphics icon)
  {
    super();
    this.icon = icon;
  }

  /* (non-Javadoc)
 * @see controlP5.ControllerView#display(processing.core.PApplet, java.lang.Object)
 */
public void display(PApplet theApplet, Button theButton) 
  {
    theApplet.fill(255);
    if (theButton.isInside()) 
    {
      theApplet.fill(220);
    }
    if (theButton.isPressed())
    {
      theApplet.fill(255, 0, 0);
    }
    theApplet.rect(0, 0, theButton.getWidth(), theButton.getHeight());
    theApplet.image(icon, 0, 0);
  }
}

