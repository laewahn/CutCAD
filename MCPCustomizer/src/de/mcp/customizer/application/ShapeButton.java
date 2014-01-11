package de.mcp.customizer.application;

import processing.core.PApplet;
import processing.core.PGraphics;

import controlP5.Button;
import controlP5.ControllerView;

public class ShapeButton implements ControllerView<Button> {

  private PGraphics icon;

  public ShapeButton(PGraphics icon)
  {
    super();
    this.icon = icon;
  }

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

