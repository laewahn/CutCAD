package de.mcp.customizer.application;
import java.util.ArrayList;

import processing.core.PApplet;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.DropdownList;
import controlP5.Slider;
import controlP5.Textfield;
import controlP5.Textlabel;
import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Material;
import de.mcp.customizer.model.STLMesh;
import de.mcp.customizer.model.Shape;

public class Properties extends PApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Controller<?>> controllers;
	private ArrayList<Material> materials;
	private ArrayList<Slider> sliders;
	private ArrayList<Slider> dummySliders;
	private ArrayList<Textfield> textfields;
	private ArrayList<Textlabel> controlNames;
	private ArrayList<Textlabel> controlUnits;
	private DropdownList setMaterial;
	private Shape shapeCurrentlyPluggedTo;
	private Cutout cutoutCurrentlyPluggedTo;
	private STLMesh stlMeshCurrentlyPluggedTo;
	private Connection connectionCurrentlyPluggedTo;
	private int posX, posY, sizeX, sizeY;
	private boolean hidden;

	public Properties(ControlP5 cp5, int posX, int posY, int sizeX, int sizeY)
	{
		this.posX = posX;
		this.posY = posY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.hidden = false;

		this.controllers = new ArrayList<Controller<?>>();
		this.materials = AllMaterials.getMaterials();
		this.shapeCurrentlyPluggedTo = null;
		this.connectionCurrentlyPluggedTo = null;
		this.cutoutCurrentlyPluggedTo = null;
		this.stlMeshCurrentlyPluggedTo = null;

		sliders = new ArrayList<Slider>();
		sliders.add(cp5.addSlider("setSlider0"));
		sliders.add(cp5.addSlider("setSlider1"));
		sliders.add(cp5.addSlider("setSlider2"));
		sliders.add(cp5.addSlider("setSlider3"));
		sliders.add(cp5.addSlider("setSlider4"));
		sliders.add(cp5.addSlider("setSlider5"));

		dummySliders = new ArrayList<Slider>();
		dummySliders.add(cp5.addSlider("setValue0"));
		dummySliders.add(cp5.addSlider("setValue1"));
		dummySliders.add(cp5.addSlider("setValue2"));
		dummySliders.add(cp5.addSlider("setValue3"));
		dummySliders.add(cp5.addSlider("setValue4"));
		dummySliders.add(cp5.addSlider("setValue5"));

		textfields = new ArrayList<Textfield>();
		textfields.add(cp5.addTextfield("setText0"));
		textfields.add(cp5.addTextfield("setText1"));
		textfields.add(cp5.addTextfield("setText2"));
		textfields.add(cp5.addTextfield("setText3"));
		textfields.add(cp5.addTextfield("setText4"));
		textfields.add(cp5.addTextfield("setText5"));

		controlNames = new ArrayList<Textlabel>();
		controlNames.add(cp5.addTextlabel("setName0"));
		controlNames.add(cp5.addTextlabel("setName1"));
		controlNames.add(cp5.addTextlabel("setName2"));
		controlNames.add(cp5.addTextlabel("setName3"));
		controlNames.add(cp5.addTextlabel("setName4"));
		controlNames.add(cp5.addTextlabel("setName5"));

		controlUnits = new ArrayList<Textlabel>();
		controlUnits.add(cp5.addTextlabel("setUnit0"));
		controlUnits.add(cp5.addTextlabel("setUnit1"));
		controlUnits.add(cp5.addTextlabel("setUnit2"));
		controlUnits.add(cp5.addTextlabel("setUnit3"));
		controlUnits.add(cp5.addTextlabel("setUnit4"));
		controlUnits.add(cp5.addTextlabel("setUnit5"));

		for (int i=0; i<sliders.size(); i++)
		{
			sliders.get(i).setPosition(50+i*200, 18)
					.setSize(100, 20)
			    	.setColorForeground(color(120))
			    	.setColorActive(color(80))
			    	.setColorBackground(color(150))
			    	.setLabelVisible(false);
			
			controllers.add(sliders.get(i));
			
			sliders.get(i).plugTo(this);

			dummySliders.get(i)
					.setRange(0, Float.MAX_VALUE)
					.setVisible(false);

			textfields.get(i).setPosition(152+i*200, 18)
					.setAutoClear(false)
					.setSize(50, 20)
					.setCaptionLabel("")
			    	.setColorForeground(color(0))
			    	.setColorActive(color(0))
			    	.setColorBackground(color(150))
					.setInputFilter(ControlP5.INTEGER);
			
			controllers.add(textfields.get(i));
			
			textfields.get(i).plugTo(this);

			controlUnits.get(i).setPosition(202+i*200, 22)
	    			.setColor(color(0));

			controlNames.get(i)
					.setPosition(120+i*200, 5)
	    			.setColor(color(0));
		}

		setMaterial = cp5.addDropdownList("setMaterial")
				.setPosition(sizeX-225, (sizeY-25)/2+25)
				.setSize(200,400)
				.setItemHeight(25)
				.setBarHeight(25)
		    	.setColorForeground(color(0))
		    	.setColorActive(color(0))
		    	.setColorBackground(color(150));
		setMaterial.getCaptionLabel().getStyle().setMarginTop(7); //should be central -> dependant on code???

		for(Material m : materials) 
		{
			setMaterial.addItem(m.getMaterialName(), materials.indexOf(m));
		}
	}

	public void setText0(String text)
	{
		sliders.get(0).setValue(Integer.valueOf(text));
	}

	public void setText1(String text)
	{
		sliders.get(1).setValue(Integer.valueOf(text));
	}

	public void setText2(String text)
	{
		sliders.get(2).setValue(Integer.valueOf(text));
	}

	public void setText3(String text)
	{
		sliders.get(3).setValue(Integer.valueOf(text));
	}
	
	public void setText4(String text)
	{
		sliders.get(4).setValue(Integer.valueOf(text));
	}
	
	public void setText5(String text)
	{
		sliders.get(5).setValue(Integer.valueOf(text));
	}

	public void setSlider1(int value)
	{
		textfields.get(1).setText(value+"");
		dummySliders.get(1).setValue(value);
	}

	public void setSlider2(int value)
	{
		textfields.get(2).setText(value+"");
		dummySliders.get(2).setValue(value);
	}

	public void setSlider0(int value)
	{
		textfields.get(0).setText(value+"");
		dummySliders.get(0).setValue(value);
	}

	public void setSlider3(int value)
	{
		textfields.get(3).setText(value+"");
		dummySliders.get(3).setValue(value);
	}
	
	public void setSlider4(int value)
	{
		textfields.get(4).setText(value+"");
		dummySliders.get(4).setValue(value);
	}
	
	public void setSlider5(int value)
	{
		textfields.get(5).setText(value+"");
		dummySliders.get(5).setValue(value);
	}

	public void changeMaterial(float eventNumber)
	{
		shapeCurrentlyPluggedTo.getShape().setMaterial(materials.get((int)eventNumber));
	}

	public void unplugAll()
	{
		if (this.shapeCurrentlyPluggedTo != null)
		{
			for(Slider s : dummySliders) s.unplugFrom(this.shapeCurrentlyPluggedTo);
			this.shapeCurrentlyPluggedTo.getShape().setActive(false);
		}
		if (this.connectionCurrentlyPluggedTo != null)
		{
			for(Slider s : dummySliders) s.unplugFrom(this.connectionCurrentlyPluggedTo);
			this.connectionCurrentlyPluggedTo.setActive(false);
		}
		if (this.cutoutCurrentlyPluggedTo != null)
		{
			for(Slider s : dummySliders) s.unplugFrom(this.cutoutCurrentlyPluggedTo);
			this.cutoutCurrentlyPluggedTo.setActive(false);
		}
		if (this.stlMeshCurrentlyPluggedTo != null)
		{
			for(Slider s : dummySliders) s.unplugFrom(this.stlMeshCurrentlyPluggedTo);
			//this.stlMeshCurrentlyPluggedTo.setActive(false);
		}
		hideAll();
	}

	public void plugTo(Connection c)
	{
		unplugAll();
		for(int i=0; i<c.getNumberOfControls(); i++)
		{
			int value = c.getValue(i);
			sliders.get(i)
			.setRange(getMinimum(c.getControlType(i)), getMaximum(c.getControlType(i)))
			.setCaptionLabel(c.getNameOfControl(i))
			.setValue(value)
			.show();
			textfields.get(i)
			.setText((int)sliders.get(i).getValue()+"")
			.show();
			controlUnits.get(i)
			.setText(getUnit(c.getControlType(i)))
			.show();
			controlNames.get(i).setText(c.getNameOfControl(i)).show();
			dummySliders.get(i).plugTo(c);
		}
		c.setActive(true);
		this.connectionCurrentlyPluggedTo = c;
	}

	public void plugTo(Shape s)
	{
		unplugAll();
		if(s.getShape().getNumberOfConnections()==0)
		{
			for(int i=0; i<s.getNumberOfControls(); i++)
			{
				int value = s.getValue(i);
				sliders.get(i)
				.setRange(getMinimum(s.getControlType(i)), getMaximum(s.getControlType(i)))
				.setCaptionLabel(s.getNameOfControl(i))
				.setValue(value)
				.show();
				textfields.get(i)
				.setText((int)sliders.get(i).getValue()+"")
				.show();
				controlUnits.get(i)
				.setText(getUnit(s.getControlType(i)))
				.show();
				controlNames.get(i).setText(s.getNameOfControl(i)).show();
				dummySliders.get(i).plugTo(s);
			}
		}
		setMaterial.setCaptionLabel(s.getShape().getMaterial().getMaterialName());
		setMaterial.show();

		s.getShape().setActive(true);
		this.shapeCurrentlyPluggedTo = s;
	}

	public void plugTo(Cutout c)
	{
		unplugAll();
		for(int i=0; i<c.getNumberOfControls(); i++)
		{
			int value = c.getValue(i);
			sliders.get(i)
			.setRange(getMinimum(c.getControlType(i)), getMaximum(c.getControlType(i)))
			.setCaptionLabel(c.getNameOfControl(i))
			.setValue(value)
			.show();
			textfields.get(i)
			.setText((int)sliders.get(i).getValue()+"")
			.show();
			controlUnits.get(i)
			.setText(getUnit(c.getControlType(i)))
			.show();
			controlNames.get(i).setText(c.getNameOfControl(i)).show();
			dummySliders.get(i).plugTo(c);
		}
		c.setActive(true);
		this.cutoutCurrentlyPluggedTo = c;
	}
	
	public void plugTo(STLMesh mesh) {
		unplugAll();
		for(int i=0; i<mesh.getNumberOfControls(); i++)
		{
			float value = mesh.getValue(i);
			sliders.get(i)
			.setRange(getMinimum(mesh.getControlType(i)), getMaximum(mesh.getControlType(i)))
			.setCaptionLabel(mesh.getNameOfControl(i))
			.setValue(value)
			.show();
			textfields.get(i)
			.setText((int)sliders.get(i).getValue()+"")
			.show();
			controlUnits.get(i)
			.setText(getUnit(mesh.getControlType(i)))
			.show();
			controlNames.get(i).setText(mesh.getNameOfControl(i)).show();
			dummySliders.get(i).plugTo(mesh);
		}
		this.stlMeshCurrentlyPluggedTo = mesh;
	}

	private void hideAll()
	{
		setMaterial.hide();
		for(Slider s : sliders) s.hide();
		for(Textfield t : textfields) t.hide();
		for(Textlabel t : controlUnits) t.hide();
		for(Textlabel t : controlNames) t.hide();
	} 

	public void hide()
	{
		for (Controller<?> c : controllers)
		{
			c.hide();
		}
		setMaterial.hide();

		this.hidden = true;
	}

	public void show()
	{
		for (Controller<?> c : controllers)
		{
			c.show();
		}
		setMaterial.show();

		this.hidden = false;
	}

	// Type 0 angle, 1 position/size (size of cutter), 2 number of edges

	private float getMaximum(int type) {
		if (type == 0) return 360;
		else if (type == 1) return 600;
		else if (type == 2) return 300;
		else return 16;
	}

	private float getMinimum(int type) {
		if (type == 0) return 0;
		else if (type == 1) return 2;
		else if (type == 2) return -300;
		else return 3;
	}

	private String getUnit(int type) {
		if (type == 0) return "degree";
		else if (type == 1) return "mm";
		else if (type == 2) return "mm";
		else return "";
	}

	public void drawProperties(PApplet p)
	{
		p.fill(180);
		// p.rect(posX, posY, sizeX, sizeY); // overwrites drop-down menu, move to show & if(hidden) branch????
		if (this.hidden)
		{
			p.textSize(24);
			p.fill(255);
			p.text("No Object selected", p.width/2-125, 30);
			unplugAll();
		}
	}
}

