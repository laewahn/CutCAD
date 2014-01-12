package de.mcp.customizer.application;
import java.util.ArrayList;

import processing.core.PApplet;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.DropdownList;
import controlP5.Slider;
import de.mcp.customizer.model.AllMaterials;
import de.mcp.customizer.model.Connection;
import de.mcp.customizer.model.Cutout;
import de.mcp.customizer.model.Material;
import de.mcp.customizer.model.Shape;

public class Properties
{
  private ArrayList<Controller> controllers;
  private ArrayList<Material> materials;
  private ArrayList<Slider> sliders;
  private Slider setValue0, setValue1, setValue2, setValue3;
  private Slider setPositionXCutout, setPositionYCutout;
  private Slider setAngle;
  private DropdownList setMaterial;
  private Shape shapeCurrentlyPluggedTo;
  private Cutout cutoutCurrentlyPluggedTo;
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

    this.controllers = new ArrayList<Controller>();
    this.materials = AllMaterials.getMaterials();
    this.shapeCurrentlyPluggedTo = null;
    this.connectionCurrentlyPluggedTo = null;
    
    sliders = new ArrayList<Slider>();
    sliders.add(setValue0 = cp5.addSlider("setValue0"));
    sliders.add(setValue1 = cp5.addSlider("setValue1"));
    sliders.add(setValue2 = cp5.addSlider("setValue2"));
    sliders.add(setValue3 = cp5.addSlider("setValue3"));
    
    for (int i=0; i<sliders.size(); i++)
    {
    	sliders.get(i).setPosition(100+i*200, 25);
    	controllers.add(sliders.get(i));
    }
          
    setAngle = cp5.addSlider("setAngle").setPosition(100, 25).setRange(0, 360).setCaptionLabel("Angle");
    
    setPositionXCutout = cp5.addSlider("setPositionXCutout").setPosition(300, 25).setRange(0, 255).setCaptionLabel("x-Position");
    setPositionYCutout = cp5.addSlider("setPositionYCutout").setPosition(500, 25).setRange(0, 255).setCaptionLabel("y-Position");

    setMaterial = cp5.addDropdownList("setMaterial")
      .setPosition(sizeX-225, (sizeY-25)/2+25)
        .setSize(200,400)
    	.setItemHeight(25)
    	.setBarHeight(25);
    setMaterial.captionLabel().style().setMarginTop(7); //should be central -> dependant on code???

    for(Material m : materials) 
    {
    	setMaterial.addItem(m.getMaterialName(), materials.indexOf(m));
    }

	controllers.add(setAngle);
    controllers.add(setPositionXCutout);
    controllers.add(setPositionYCutout);
  }

  public void changeMaterial(float eventNumber)
  {
    shapeCurrentlyPluggedTo.getShape().setMaterial(materials.get((int)eventNumber));
  }
  
  public void unplugAll()
  {
    if (this.shapeCurrentlyPluggedTo != null)
    {
      for(Slider s : sliders) s.unplugFrom(this.shapeCurrentlyPluggedTo);
      this.shapeCurrentlyPluggedTo.getShape().setActive(false);
    }
    if (this.connectionCurrentlyPluggedTo != null)
    {
      setAngle.unplugFrom(this.connectionCurrentlyPluggedTo);
      this.connectionCurrentlyPluggedTo.setActive(false);
    }
    if (this.cutoutCurrentlyPluggedTo != null)
    {
      setPositionXCutout.unplugFrom(this.cutoutCurrentlyPluggedTo);
      setPositionYCutout.unplugFrom(this.cutoutCurrentlyPluggedTo);
      setAngle.unplugFrom(this.cutoutCurrentlyPluggedTo);
      this.cutoutCurrentlyPluggedTo.setActive(false);
    }
    hideAll();
  }
  
  public void plugTo(Connection c)
  {
    unplugAll();
    showConnectionProperties();
    setAngle.plugTo(c).setValue(c.getAngle());

    c.setActive(true);
    this.connectionCurrentlyPluggedTo = c;
  }

  public void plugTo(Shape s)
  {
    unplugAll();
    showShapeProperties(s);
    for(int i=0; i<s.getNumberOfControls(); i++)
    {
    	int value = s.getValue(i);
    	sliders.get(i).plugTo(s)
    		.setRange(s.getMinValueOfControl(i), s.getMaxValueOfControl(i))
    		.setCaptionLabel(s.getNameOfControl(i))
    		.setValue(value)
    		.show();
    }
    setMaterial.setCaptionLabel(s.getShape().getMaterial().getMaterialName());
    setMaterial.show();
    
    s.getShape().setActive(true);
    this.shapeCurrentlyPluggedTo = s;
  }
  
  public void plugTo(Cutout c)
  {
    unplugAll();
    showCutoutProperties();
    setAngle.plugTo(c).setValue(c.getAngle());
    setPositionXCutout.plugTo(c).setValue(c.getPositionXCutout());
    setPositionYCutout.plugTo(c).setValue(c.getPositionYCutout());

    c.setActive(true);
    this.cutoutCurrentlyPluggedTo = c;
  }
  
  private void hideAll()
  {
    setAngle.hide();
    setPositionXCutout.hide();
    setPositionYCutout.hide();
    setMaterial.hide();
    for(Slider s : sliders) s.hide();
  } 
  
  private void showCutoutProperties()
  {
    setAngle.show();
    setPositionXCutout.show();
    setPositionYCutout.show();
  }
  
  private void showConnectionProperties()
  {
    setAngle.show();
  }
  
  private void showShapeProperties(Shape s)
  {
    setMaterial.show();
    for(int i=0; i<s.getNumberOfControls(); i++) sliders.get(i).show();
  }

  public void hide()
  {
    for (Controller c : controllers)
    {
      c.hide();
    }
    setMaterial.hide();

    this.hidden = true;
  }

  public void show()
  {
    for (Controller c : controllers)
    {
      c.show();
    }
    setMaterial.show();

    this.hidden = false;
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

