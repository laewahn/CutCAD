package de.mcp.customizer.application;

import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;

/**
 * This class is responsible for drawing a grid on a view
 */
public class Grid implements Drawable2D {

	private int gridWidth;
//	private Transformation transformation;
//	private PGraphics view;
	
	/**
	 * Creates a Grid that gets drawn on the passed PGraphics-object view and will be scaled depending on the Transformation-object t
	 * 
	 * @param t the transformation-object for the view
	 * @param view the view that the grid is drawn on
	 */
	public Grid(Transformation t, PGraphics view)
	{
		this.gridWidth = 50;
//		this.transformation = t;
//		this.view = view;
	}
	
	/**
	 * Draws the Grid.
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
	
	
//	public void drawGrid()
//	{		
//		int scaledGridWidth = getScaledGridWidth();
//		for (int i = -100; i < 100; i++)
//	    {
//			view.strokeWeight(1);
//			view.stroke(220);
//			view.line(-100 * scaledGridWidth, scaledGridWidth * i, 100 * scaledGridWidth, scaledGridWidth * i);
//			view.line(scaledGridWidth * i, -100 * scaledGridWidth, scaledGridWidth * i, 100 * scaledGridWidth);
//	    }
//	}
	
//	public void drawGrid()
//	{		
//		this.draw2D(this.view);
//	}

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
