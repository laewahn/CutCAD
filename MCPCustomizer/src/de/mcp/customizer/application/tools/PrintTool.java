package de.mcp.customizer.application.tools;

import java.util.List;

import de.mcp.customizer.application.Properties;
import de.mcp.customizer.application.Statusbar;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.printdialog.PrintDialog;
import de.mcp.customizer.view.Transformation;
import processing.core.PGraphics;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

public class PrintTool extends Tool {
	
	private List<Shape> shapes;
	
    public PrintTool(Rect view, Properties properties, Statusbar statusbar, Transformation transform, List<Shape> shapes) 
    {
        super(view, properties, statusbar, transform, "PrintTool");
        this.shapes = shapes;
    }

    public PGraphics getIcon(PGraphics context)
    {
        context.beginDraw();

        context.fill(0);
        context.textSize(30);
        //context.text("print",40,33);

        context.noFill();
        context.stroke(0);
        context.strokeWeight(2);
        context.rect(85, 2, 6, 18);
        context.line(88, 24, 88, 38);
        context.line(10, 50, 100, 25);
        context.line(100, 25, 120, 50);
        context.line(20, 50, 88, 43);


        context.endDraw();

        return context;
    }

    public void mouseButtonPressed(Vec2D position, int button)
    {
    }

    public void mouseButtonReleased(Vec2D position, int button)
    {
    }
    
    public void mouseMoved(Vec2D position)
    {
    }
    
    @Override
    public void wasSelected() {
    	super.wasSelected();
    	new PrintDialog(shapes);
    }
}
