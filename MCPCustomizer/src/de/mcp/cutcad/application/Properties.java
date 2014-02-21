package de.mcp.cutcad.application;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.event.KeyEvent;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.DropdownList;
import controlP5.Numberbox;
import controlP5.Textfield;
import controlP5.Textlabel;
import de.mcp.cutcad.model.AllMaterials;
import de.mcp.cutcad.model.Material;
import de.mcp.cutcad.model.primitives.Shape;

/**
 * A property-bar that handles up to 6 sliders that can control parameters of
 * Shapes, Connections, Cutouts and STLMeshes.
 *
 */
public class Properties extends PApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Controller<?>> controllers;
	private ArrayList<Material> materials;
	private ArrayList<Numberbox> numberBoxes;
	private ArrayList<Textlabel> controlNames;
	private ArrayList<Textlabel> controlUnits;
	private Textfield shapeName;
	private Textlabel nameLabel;
	private DropdownList setMaterial;
	private Pluggable currentlyPluggedTo;
	private boolean hidden;

	/**
	 * Creates a properties bar at (posX, posY) with sizeX, sizeY.
	 * 
	 * @param cp5 the ControlP5 object
	 * @param posX the x position the properties bar will be created at
	 * @param posY the y position the properties bar will be created at
	 * @param sizeX the size of the properties bar on the x axis 
	 * @param sizeY the size of the properties bar on the y axis 
	 */
	public Properties(ControlP5 cp5, int posX, int posY, int sizeX, int sizeY)
	{
		this.hidden = false;

		this.controllers = new ArrayList<Controller<?>>();
		this.materials = AllMaterials.getMaterials();
		this.currentlyPluggedTo = null;
		
		this.nameLabel = cp5.addTextlabel("nameLabel")
				.setColor(0)
				.setText("Name")
				.setPosition(sizeX-390, (sizeY-10)/2)
				.setSize(50, 20)
                ;
		this.shapeName = cp5.addTextfield("setName")
				.setPosition(sizeX-350, (sizeY-25)/2)
				.setSize(100, 25)
		    	.setColorForeground(color(120))
		    	.setColorActive(color(80))
		    	.setColorBackground(color(150))
		    	.setLabelVisible(false);

		numberBoxes = new ArrayList<Numberbox>();
		numberBoxes.add(new NumberInputBox(this, cp5, "setValue0"));
		numberBoxes.add(new NumberInputBox(this, cp5, "setValue1"));
		numberBoxes.add(new NumberInputBox(this, cp5, "setValue2"));
		numberBoxes.add(new NumberInputBox(this, cp5, "setValue3"));
		numberBoxes.add(new NumberInputBox(this, cp5, "setValue4"));
		numberBoxes.add(new NumberInputBox(this, cp5, "setValue5"));

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

		for (int i = 0; i < numberBoxes.size(); i++)
		{
			numberBoxes.get(i).setPosition(50+i*100, 18)
					.setSize(50, 20)
			    	.setColorForeground(color(120))
			    	.setColorActive(color(80))
			    	.setColorBackground(color(150))
			        .setDecimalPrecision(1)
			        .setDirection(Controller.HORIZONTAL)
			    	.setLabelVisible(false);
			
			controllers.add(numberBoxes.get(i));
			
			numberBoxes.get(i).plugTo(this);

			controlUnits.get(i).setPosition(101+i*100, 25)
	    			.setColor(color(0));

			controlNames.get(i)
					.setPosition(60+i*100, 5)
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
	
	/**
	 * @param materialNumber number of the Material the shape currently plugged to the properties bar will be set to
	 */
	public void changeMaterial(float materialNumber)
	{
		if (this.currentlyPluggedTo instanceof Shape)
		{
			Shape s = (Shape) this.currentlyPluggedTo;
			s.getGShape().setMaterial(materials.get((int)materialNumber));
		}
	}

	/**
	 * Unplugs all controls from the object
	 */
	public void unplugAll()
	{
		if (this.currentlyPluggedTo != null)
		{
			for (Numberbox n : numberBoxes) n.unplugFrom(this.currentlyPluggedTo);
			this.shapeName.unplugFrom(this.currentlyPluggedTo);
			this.currentlyPluggedTo.setActive(false);
		}
		hideAll();
	}
	
	/**
	 * 
	 * Plugs the controls of the properties bar to the Pluggable p. 
	 * Until the properties bar is unplugged from the Pluggable p, 
	 * the controls will change the parameters of Pluggable p.
	 * 
	 * @param p the Pluggable the controls are plugged to
	 */
	public void plugTo(Pluggable p)
	{
		unplugAll();
		for (int i = 0; i < p.getNumberOfControls(); i++)
		{
			int value = p.getValue(i);
			numberBoxes.get(i)
			.plugTo(p)
			.setRange(getMinimum(p.getControlType(i)), getMaximum(p.getControlType(i)))
			.setCaptionLabel(p.getNameOfControl(i))
			.setValue(value)
			.show();
			controlUnits.get(i)
			.setText(getUnit(p.getControlType(i)))
			.show();
			controlNames.get(i).setText(p.getNameOfControl(i)).show();
		}
		p.setActive(true);
		this.currentlyPluggedTo = p;
		
		if (p instanceof Shape)
		{
			Shape s = (Shape) p;
			this.shapeName.plugTo(p);
			this.shapeName.setText(s.getName());
			this.shapeName.show();
			this.nameLabel.show();
			for (int i = 0; i < p.getNumberOfControls(); i++)
			{
				if (s.getGShape().getNumberOfConnections() > 0)
				{
					lockAll();
				}
				else
				{
					unlockAll();
				}
			}
			setMaterial.setCaptionLabel(s.getGShape().getMaterial().getMaterialName());
			setMaterial.show();

			s.getGShape().setActive(true);
		}
		else
		{
			unlockAll();
		}
	}

	private void unlockAll()
	{
		for (int i = 0; i < controllers.size(); i++)
		{
			controllers.get(i).unlock();
		}
	}
	
	private void lockAll()
	{
		for (int i = 0; i < controllers.size(); i++)
		{
			controllers.get(i).lock();
		}
	}
	
	private void hideAll()
	{
		setMaterial.hide();
		shapeName.hide();
		nameLabel.hide();
		for(Numberbox n : numberBoxes) n.hide();
		for(Textlabel t : controlUnits) t.hide();
		for(Textlabel t : controlNames) t.hide();
	} 
	
	public void hide()
	{
		for (Controller<?> c : controllers)
		{
			c.hide();
		}
		shapeName.hide();
		nameLabel.hide();
		setMaterial.hide();

		this.hidden = true;
	}

	public void show()
	{
		for (Controller<?> c : controllers)
		{
			c.show();
		}
		shapeName.show();
		nameLabel.show();
		setMaterial.show();

		this.hidden = false;
	}

	// Type 0 angle, 1 position/size (size of cutter), 2 number of edges

	private float getMaximum(int type) {
		if (type == 0) return 360;
		else if (type == 1) return 600;
		else if (type == 2) return 300;
		else if (type == 3) return 16;
		else return 0;
	}

	private float getMinimum(int type) {
		if (type == 0) return 0;
		else if (type == 1) return 2;
		else if (type == 2) return -300;
		else if (type == 3) return 3;
		else return 0;
	}

	private String getUnit(int type) {
		if (type == 0) return "degree";
		else if (type == 1) return "mm";
		else if (type == 2) return "mm";
		else if (type == 3) return "";
		else return "";
	}

	/**
	 * Draws the property-bar to the specified applet
	 * 
	 * @param p the applet the property-bar will be drawn on
	 */
	public void drawProperties(PApplet p)
	{
		p.fill(180);
		if (this.hidden)
		{
			unplugAll();
		}
	}
	
	public void keyEvent(KeyEvent k)
	{
		for(Numberbox n : numberBoxes)
		{
			n.keyEvent(k);
		}
	}
}

