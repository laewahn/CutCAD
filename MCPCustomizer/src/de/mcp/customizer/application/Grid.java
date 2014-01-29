package de.mcp.customizer.application;

import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;

public class Grid implements Drawable2D {
	private int gridWidth;
//	private Transformation transformation;
//	private PGraphics view;
	
	public Grid(Transformation t, PGraphics view)
	{
		this.gridWidth = 50;
//		this.transformation = t;
//		this.view = view;
	}
	
	@Override
	public void draw2D(PGraphics context, Transformation transformation) {
		int scaledGridWidth = getScaledGridWidth(transformation);
		for (int i = -100; i < 100; i++)
	    {
			context.strokeWeight(getScaleFactor(transformation.getScale()));
			context.stroke(220);
			context.line(-100 * scaledGridWidth, scaledGridWidth * i, 100 * scaledGridWidth, scaledGridWidth * i);
			context.line(scaledGridWidth * i, -100 * scaledGridWidth, scaledGridWidth * i, 100 * scaledGridWidth);
	    }
	}
	
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
	
	public int getScaledGridWidth(Transformation transformation)
	{
		return (int) (gridWidth * getScaleFactor(transformation.getScale()));		
	}
}
