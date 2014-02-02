package de.mcp.customizer.model;

import java.io.File;
import java.util.ArrayList;

import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.shapes.PolygonShape;
import processing.core.PApplet;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import geomerative.*;

/**
 * Import svg to create shapes - Works only with (closed) path objects
 */
public class ImportSVG extends PApplet {
	private static final long serialVersionUID = 1L;
	private RPoint[][] pointPaths;
//	private List<Shape> shapes;
	private ObjectContainer container;
	// ToDO: scalingFactor only correct for inkscape, illustrator needs another
	private float scalingInkscape = 2.82222229120988f; // ->Preferences (SVG
	// Resolution)

	/**
	 *        Open standard file dialog for selecting a file
	 * 
	 * @param shapes
	 *            List of shapes, where the produced shapes are registered
	 */
	public ImportSVG(ObjectContainer container) {
		this.container = container;
		selectInput("Select a SVG file to process:", "fileSelected");
	}

	/**
	 * Load a file and checks, if it is an svg
	 * 
	 * @param selection
	 *            Path name
	 */
	public void fileSelected(File selection) {
		if (!(selection == null)) {
			if (checkExtension(selection.getAbsolutePath()).equals("svg")) {
				createPathsFromSVG(selection);
			}
		}
	}

	/**
	 * Extract paths from the selected svg file and stores them in an array
	 * 
	 * @param selection
	 *            Path name
	 */
	private void createPathsFromSVG(File selection) {
		pointPaths = RG.loadShape(selection.getAbsolutePath())
				.getPointsInPaths();

		ArrayList<Shape> newShapes = new ArrayList<Shape>();
		for (int i = 0; i < pointPaths.length; i++) {
			if (pointPaths[i] != null) {
				newShapes.add(createShapeFromPath(i));
			}
		}
		createCutouts(newShapes);
	}

	private void createCutouts(ArrayList<Shape> newShapes) {
		for (Shape original : newShapes) {
			for (Shape possibleCutout : newShapes) {
				boolean isCutout = true;
				Polygon2D formOriginal = new Polygon2D(original.getGShape().getVertices());
				Vec2D offset = possibleCutout.getGShape().getPosition2D().sub(original.getGShape().getPosition2D());
				for (Vec2D v : possibleCutout.getGShape().getVertices()) {
					if(!formOriginal.containsPoint(v.add(offset))) {
						isCutout = false;
					}
				}
				if (isCutout) {
					original.getGShape().addCutout(possibleCutout.getGShape());
				}
			}
		}
	}

	/**
	 * Takes one path from all paths extracted from the svg file and creates a
	 * shape form it
	 * 
	 * @param i
	 *            number of path in the list of paths
	 */
	private Shape createShapeFromPath(int i) {
		ArrayList<Vec2D> path = new ArrayList<Vec2D>();
		float MinX = Integer.MAX_VALUE;
		float MinY = Integer.MAX_VALUE;
		for (int j = 0; j < pointPaths[i].length; j++) {
			MinX = ((pointPaths[i][j].x < MinX) ? pointPaths[i][j].x : MinX);
			MinY = ((pointPaths[i][j].y < MinY) ? pointPaths[i][j].y : MinY);
		}
		Vec3D position = new Vec3D(MinX * scalingInkscape, MinY * scalingInkscape,
				0);
		for (int j = 0; j < pointPaths[i].length; j++) {
			path.add(new Vec2D((pointPaths[i][j].x) * scalingInkscape
					- position.x(), (pointPaths[i][j].y) * scalingInkscape
					- position.y()));
		}

		while (Math.abs(path.get(path.size() - 1).x() - path.get(0).x()) < 0.1f
				&& Math.abs(path.get(path.size() - 1).y() - path.get(0).y()) < 0.1f) {
			path.remove(path.size() - 1);
		}
		Shape pathShape = new PolygonShape(path, position);
		container.addShape(pathShape);
		return pathShape;
	}
}
