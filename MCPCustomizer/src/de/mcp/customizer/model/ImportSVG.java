package de.mcp.customizer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import geomerative.*;

public class ImportSVG extends PApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPoint[][] pointPaths;
	private List<Shape> shapes;
	private float scalingFactor = 2.82222229120988f; //->Preferences (SVG Resolution)

	public ImportSVG(List<Shape> shapes) 
	{
		this.shapes = shapes;
		selectInput("Select a SVG file to process:", "fileSelected");
	}

	public void fileSelected(File selection) 
	{
		if (selection == null) 
		{
			println("No file selected");
		} else {
			if (checkExtension(selection.getAbsolutePath()).equals("svg"))
			{
				createPathsFromSVG(selection);
			}
			else
			{
				println("NoSVG");  
			}
		}
	}

	private void createPathsFromSVG(File selection) 
	{
		println("User selected " + selection.getAbsolutePath());
		RG.init(this);
		RG.ignoreStyles(false);

		RG.setPolygonizer(RG.ADAPTATIVE);

		pointPaths = RG.loadShape(selection.getAbsolutePath()).getPointsInPaths();
 
		for(int i = 0; i<pointPaths.length; i++) 
		{
			if (pointPaths[i] != null) {
				createShapeFromPath(i);
			}
		}
	}

	private void createShapeFromPath(int i) {
		ArrayList<Vec2D> path = new ArrayList<Vec2D>();
		float MinX = Integer.MAX_VALUE;
 		float MinY = Integer.MAX_VALUE;
		for(int j = 0; j<pointPaths[i].length; j++){
			MinX = ((pointPaths[i][j].x<MinX) ? pointPaths[i][j].x : MinX);
			MinY = ((pointPaths[i][j].y<MinY) ? pointPaths[i][j].y : MinY);
		}
		Vec3D position = new Vec3D(MinX*scalingFactor, MinY*scalingFactor, 0);
		for(int j = 0; j<pointPaths[i].length; j++){
			path.add(new Vec2D((pointPaths[i][j].x)*scalingFactor - position.x(), (pointPaths[i][j].y)*scalingFactor - position.y()));
		}
		
		while (Math.abs(path.get(path.size()-1).x() - path.get(0).x()) < 0.1f && 
			Math.abs(path.get(path.size()-1).y() - path.get(0).y()) < 0.1f)
			{
				path.remove(path.size()-1);
			}
		Shape pathShape = new PolygonShape(path, position);
		shapes.add(pathShape);
	}

}
