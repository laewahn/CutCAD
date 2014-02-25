package de.mcp.cutcad.application;

import de.mcp.cutcad.view.Drawable2D;
import de.mcp.cutcad.view.Transformation;
import processing.core.PGraphics;

/**
 * This class is responsible for drawing a grid on a view
 */
public class Grid implements Drawable2D {

	private int gridWidth;
	
	/**
	 * Creates a Grid with a default width of 5mm
	 */
	public Grid(Transformation t, PGraphics view)
	{
		this.gridWidth = 50;
	}
	
	/**
	 * Draws the Grid on the passed PGraphics-object view and scales it depending on the transformation.
	 */
	@Override
	public void draw2D(PGraphics context, Transformation transformation) {
		int scaledGridWidth = getScaledGridWidth(transformation);
		for (int i = -100; i < 100; i++)
	    {
			context.strokeWeight(1);
			context.stroke(220);
			context.line(-100 * scaledGridWidth, scaledGridWidth * i, 100 * scaledGridWidth, scaledGridWidth * i);
			context.line(scaledGridWidth * i, -100 * scaledGridWidth, scaledGridWidth * i, 100 * scaledGridWidth);
	    }
	}
	
	private float getScaleFactor(float scale) {

		float scaleFactor = 1;
		
		if (scale < 1.0)
		{
			while (1.0f / scaleFactor > scale)
			{
				scaleFactor *= 2.0;
			}
		}
		else
		{
			while (1.0f / scaleFactor < scale)
			{
				scaleFactor /= 2.0;
			}
			
		}
		return scaleFactor;		
	}
	
	/**
	 * @return the width of the grid in pixel
	 */
	public int getScaledGridWidth(Transformation transformation)
	{
		return (int) (gridWidth * getScaleFactor(transformation.getScale())*transformation.getScale());		
	}
}
