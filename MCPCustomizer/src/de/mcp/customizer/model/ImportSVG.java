package de.mcp.customizer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mcp.customizer.model.primitives.Shape;
import de.mcp.customizer.model.primitives.Vector2D;
import de.mcp.customizer.model.primitives.Vector3D;
import de.mcp.customizer.model.shapes.PolygonShape;
import processing.core.PApplet;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import geomerative.*;

/**
 * Import svg to create shapes - Works only with (closed) path objects
 */
public class ImportSVG extends PApplet {
	private static final long serialVersionUID = 1L;
	private RPoint[][] pointPaths;
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
	}

	/**
	 * Extract paths from the selected svg file and stores them in an array
	 * 
	 * @param selection
	 *            Path name
	 */
	public void createPathsFromSVG(File selection) {
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
				
				List<Vec2D> vec2DVertices = new ArrayList<Vec2D>();
				for(Vector2D vertex : original.getGShape().getVertices()) {
					vec2DVertices.add(vertex.getVec2D());
				}
				
				Polygon2D formOriginal = new Polygon2D(vec2DVertices);
				Vector2D offset = possibleCutout.getGShape().getPosition2D().sub(original.getGShape().getPosition2D());
				for (Vector2D v : possibleCutout.getGShape().getVertices()) {
					if(!formOriginal.containsPoint(v.add(offset).getVec2D())) {
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
		ArrayList<Vector2D> path = new ArrayList<Vector2D>();
		float MinX = Integer.MAX_VALUE;
		float MinY = Integer.MAX_VALUE;
		for (int j = 0; j < pointPaths[i].length; j++) {
			MinX = ((pointPaths[i][j].x < MinX) ? pointPaths[i][j].x : MinX);
			MinY = ((pointPaths[i][j].y < MinY) ? pointPaths[i][j].y : MinY);
		}
		Vector3D position = new Vector3D(MinX * scalingInkscape, MinY * scalingInkscape,
				0);
		for (int j = 0; j < pointPaths[i].length; j++) {
			path.add(new Vector2D((pointPaths[i][j].x) * scalingInkscape
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
