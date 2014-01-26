package de.mcp.customizer.application;

import geomerative.RG;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.*;
import toxi.processing.ToxiclibsSupport;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import de.mcp.customizer.application.tools.*;
import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.model.Shape;
import de.mcp.customizer.view.Drawable2D;
import de.mcp.customizer.view.Transformation;

public class MCPCustomizer extends PApplet {

	class CustomizerFrame {
		public Vec2D origin;
		public Vec2D size;
	}
	
	class CustomizerView {
		
		private PGraphics context;
		private CustomizerFrame frame;
		
		private Transformation transform;
		private Grid grid;
		private Drawable2D axes;
		
		public CustomizerView(PGraphics context, CustomizerFrame frame, Grid grid) {
			this.context = context;
			this.frame = frame;
			this.grid = grid;
		}
		
		public void applyTransformation(Transformation transform) {
			context.scale(transform.getScale());
	        context.translate(-transform.getTranslation().x(), -transform.getTranslation().y());
		}
		
		public void draw(List<Drawable2D> drawables) {
			context.beginDraw();
			context.background(150);
			
			applyTransformation(this.transform);
			
			drawables.add(axes);
			drawables.add(grid);
			
//			axes.draw2D(context);
//			grid.draw2D(context);

			for(Drawable2D d : drawables) {
				d.draw2D(context);
			}
			
//			toolbar.getSelectedTool().draw2D(context);

			context.endDraw();

			image(context, frame.origin.x(), frame.origin.y());
		}		
	}
	
	class Axes2D implements Drawable2D {
		@Override
		public void draw2D(PGraphics context) {
			context.strokeWeight(2);
			context.textSize(32);
			context.fill(color(255, 0, 0));
			context.stroke(color(255, 0, 0));
			context.line(0, 0, 350, 0);
			context.text("X", 350, 12);
			context.fill(color(0, 255, 0));
			context.stroke(color(0, 255, 0));
			context.line(0, 0, 0, 350);
			context.text("Y", -10, 385);
			context.stroke(color(0, 0, 0));
			context.strokeWeight(1);
		}
	}
	
	private static final long serialVersionUID = 6945013714741954254L;
	Toolbar toolbar;

	public Properties properties;
	Statusbar statusbar;
	ControlP5 cp5;

	ToxiclibsSupport gfx;
	PGraphics view2D, view3D;

	ObjectContainer container = new ObjectContainer();

	TriangleMesh mesh;

	int startX = 0;
	int startY = 0;

	int gridWidth = 50; // 5 mm

	int viewSizeX;
	int viewSizeY;

	int view2DPosX;
	int view2DPosY;
	Rect view2DRect;

	int view3DPosX;
	int view3DPosY;

	int cameraX = 45;
	int cameraY = 1000;

	public Transformation transform2D = new Transformation((float) 1.0,
			new Vec2D(0, 0));
	Transformation transform3D = new Transformation((float) 1.0,
			new Vec2D(0, 0));

	Grid grid3D, grid2D;

	Vec3D cameraPosition;
	Tool tools[];

	CustomizerView customizerView2D;
	
	public STLMesh meshSTL;

	public void setup() {
		size(displayWidth, displayHeight, P3D);
		ortho();

		viewSizeX = (displayWidth - 50 - 30) / 2;
		viewSizeY = (displayHeight - 50 - 30);
		view2DPosX = 50;
		view2DPosY = 50;
		view3DPosX = view2DPosX + viewSizeX + 15;
		view3DPosY = 50;
		view2DRect = new Rect(view2DPosX, view2DPosY, viewSizeX, viewSizeY);

		view2D = createGraphics(viewSizeX, viewSizeY, P3D);
		view3D = createGraphics(viewSizeX, viewSizeY, P3D);

		grid2D = new Grid(transform2D, view2D);
		grid3D = new Grid(transform3D, view3D);

		CustomizerFrame theFrame = new CustomizerFrame();
		theFrame.origin = new Vec2D(view2DPosX, view2DPosY);
		theFrame.size = new Vec2D(viewSizeX, viewSizeY);
		customizerView2D = new CustomizerView(view2D, null, grid2D);
		
		gfx = new ToxiclibsSupport(this, view3D);
		RG.init(this);
		RG.ignoreStyles(false);

		RG.setPolygonizer(RG.ADAPTATIVE);

		meshSTL = new STLMesh();

		new AllMaterials().addMaterialsFromFile(sketchPath("") + "/materials");
		AllMaterials.setBaseMaterial(AllMaterials.getMaterials().get(40));

		cp5 = new ControlP5(this);

		createProperties();
		statusbar = new Statusbar();
		createToolbar();

		cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY)
				.getRotatedAroundAxis(new Vec3D((float) 0.0, (float) 0.0,
						(float) 1.0), radians(cameraX));
	}

	public void draw() {
		background(255);
		fill(0);

//		draw2DView();
		
		draw3DView();
		
		properties.drawProperties(this);
		statusbar.drawStatusbar(this);
	}

	void draw2DView() {
		view2D.beginDraw();
		transform2D.transform(view2D);

		view2D.background(150);

		draw2DAxes(view2D);
		grid2D.drawGrid();

		for (Shape s : this.container.allShapes()) {
			s.getShape().draw2D(view2D);
		}

		for (Connection c : this.container.allConnections()) {
			c.draw2D(view2D);
		}

		for (Cutout c : this.container.allCutouts()) {
			c.draw2D(view2D);
		}

		this.toolbar.getSelectedTool().draw2D(view2D);

		view2D.endDraw();

		image(view2D, view2DPosX, view2DPosY);
	}

	void draw3DView() {
		view3D.beginDraw();

		view3D.ortho();
		view3D.beginCamera();
		view3D.camera(cameraPosition.x(), cameraPosition.y(),
				cameraPosition.z(), (float) 0.0, (float) 0.0, (float) 0.0,
				(float) 0.0, (float) 0.0, (float) -1.0);
		view3D.translate(-viewSizeX / 2, -viewSizeY / 2);
		view3D.endCamera();

		view3D.background(150);

		float scale = transform3D.getScale();
		view3D.scale(scale);

		draw3DAxes(view3D);
		grid3D.drawGrid();

		for (Shape s : container.allShapes()) {
			s.getShape().draw3D(view3D);
		}

		if (meshSTL.isStlImported()) {
			if (meshSTL.isPosChanged()) {
				meshSTL.center();
				System.out.println("changed position");
			}
			if (meshSTL.isRotChanged()) {
				meshSTL.rotate();
				System.out.println("rotation changed");
			}
			gfx.mesh(meshSTL.getSTLMesh());
		}

		view3D.endDraw();

		image(view3D, view3DPosX, view3DPosY);
	}

	private void draw3DAxes(PGraphics p) {
		p.strokeWeight(2);
		p.textSize(32);
		p.fill(color(255, 0, 0));
		p.stroke(color(255, 0, 0));
		p.line(0, 0, 0, 350, 0, 0);
		p.text("X", 350, 12, 0);
		p.fill(color(0, 255, 0));
		p.stroke(color(0, 255, 0));
		p.line(0, 0, 0, 0, 350, 0);
		p.text("Y", -10, 385, 0);
		p.fill(color(0, 0, 255));
		p.stroke(color(0, 0, 255));
		p.line(0, 0, 0, 0, 0, 350);
		p.text("Z", 0, 0, 350);
		p.stroke(color(0, 0, 0));
		p.strokeWeight(1);
	}

	private void draw2DAxes(PGraphics p) {
		p.strokeWeight(2);
		p.textSize(32);
		p.fill(color(255, 0, 0));
		p.stroke(color(255, 0, 0));
		p.line(0, 0, 350, 0);
		p.text("X", 350, 12);
		p.fill(color(0, 255, 0));
		p.stroke(color(0, 255, 0));
		p.line(0, 0, 0, 350);
		p.text("Y", -10, 385);
		p.stroke(color(0, 0, 0));
		p.strokeWeight(1);
	}

	void createToolbar() {
		toolbar = new Toolbar(cp5, this);

		toolbar.setPosition(0, 50).setSize(50, 700).setItemHeight(50)
				.disableCollapse().hideBar();

		tools = new Tool[] { new SelectTool(this, container),
				new DrawTool(this, container),
				new SymmetricPolygonTool(this, container),
				new TrapeziumTool(this, container),
				new PolygonTool(this, container),
				new ConnectTool(this, container),
				new DeleteTool(this, container),
				new CutoutTool(this, container), new CopyTool(this, container),
				new ImportSVGTool(this, container),
				new ImportSTLTool(this, container),
				new ChangeSTLTool(this, container),
				new PrintTool(this, container) };

		toolbar.addTools(Arrays.asList(tools));
		toolbar.setSelectedTool(tools[0]);
	}

	void createProperties() {
		properties = new Properties(cp5, 0, 0, width, 50);
		properties.hide();
	}

	public void displayStatus(String status) {
		this.statusbar.setStatus(status);
	}

	public void displayMousePosition(Vec2D position) {
		this.statusbar.setMousePosition(position);
	}

	public void controlEvent(ControlEvent theEvent) {
		if (theEvent.isGroup()
				&& theEvent.getGroup().getName() == "setMaterial") {
			properties.changeMaterial(theEvent.getGroup().getValue());
		}
	}

	public void mousePressed() {
		if (mouseOver3DView()) {
			startX = mouseX - view3DPosX;
			startY = mouseY - view3DPosY;
		}

		Vec2D mousePosition = new Vec2D(mouseX, mouseY);
		toolbar.getSelectedTool()
				.mouseButtonPressed(mousePosition, mouseButton);
	}

	public void mouseDragged() {
		if (mouseOver3DView()) {
			if (mouseButton == PConstants.LEFT) {
				cameraPosition = new Vec3D(viewSizeX, viewSizeY, cameraY + 5
						* (mouseY - view3DPosY - startY)).getRotatedAroundAxis(
						new Vec3D((float) 0.0, (float) 0.0, (float) 1.0),
						radians(cameraX + mouseX - view3DPosX - startX));
			} else if (mouseButton == PConstants.RIGHT) {
				// do nothing... later: translate 3D view. This is problematic
				// due to the way the camera is handled.
			}
		}

		toolbar.getSelectedTool().mouseMoved(new Vec2D(mouseX, mouseY));
	}

	public void mouseReleased() {
		toolbar.getSelectedTool().mouseButtonReleased(
				new Vec2D(mouseX, mouseY), mouseButton);

		if (mouseOver3DView() && mouseButton == PConstants.LEFT) {
			cameraX += mouseX - view3DPosX - startX;
			cameraY += 5 * (mouseY - view3DPosY - startY);
		}
	}

	public void mouseMoved() {
		toolbar.getSelectedTool().mouseMoved(new Vec2D(mouseX, mouseY));
	}

	boolean mouseOver2DView() {
		return mouseX > view2DPosX && mouseX <= view2DPosX + viewSizeX
				&& mouseY > view2DPosY && mouseY <= view2DPosY + viewSizeY;
	}

	boolean mouseOver3DView() {
		return mouseX > view3DPosX && mouseX <= view3DPosX + viewSizeX
				&& mouseY > view3DPosY && mouseY <= view3DPosY + viewSizeY;
	}

	public void keyPressed() {
		if (key == '+') {
			if (mouseOver2DView()) {
				transform2D.scaleUp(0.01f);
			}
			if (mouseOver3DView()) {
				transform3D.scaleUp(0.01f);
			}
		}
		if (key == '-') {
			if (mouseOver2DView()) {
				transform2D.scaleDown(0.01f);
			}
			if (mouseOver3DView()) {
				transform3D.scaleDown(0.01f);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void mouseWheel(MouseEvent event) {
		if (mouseOver2DView()) {
			transform2D.scaleUp((float) (0.01 * -event.getAmount()));
		}
		if (mouseOver3DView()) {
			transform3D.scaleUp((float) (0.01 * -event.getAmount()));
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { /* "--present", */"de.mcp.customizer.application.MCPCustomizer" });
	}

	public boolean sketchFullScreen() {
		return true;
	}
}
