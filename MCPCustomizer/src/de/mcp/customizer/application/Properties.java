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
  private Slider setSizeX, setSizeY;
  private Slider setPositionXCutout, setPositionYCutout, setAngleCutout;
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

    setSizeX = cp5.addSlider("setSizeX").setPosition(100, 25).setRange(10, 255).setCaptionLabel("Width");

    setSizeY = cp5.addSlider("setSizeY")
      .setPosition(300, 25)
        .setRange(10, 255)
          .setCaptionLabel("Length");
          
    setAngle = cp5.addSlider("setAngle").setPosition(100, 25).setRange(0, 360).setCaptionLabel("Angle");
    
    setPositionXCutout = cp5.addSlider("setPositionXCutout").setPosition(300, 25).setRange(0, 255).setCaptionLabel("x-Position");
    setPositionYCutout = cp5.addSlider("setPositionYCutout").setPosition(500, 25).setRange(0, 255).setCaptionLabel("y-Position");
    setAngleCutout = cp5.addSlider("setAngleCutout").setPosition(100, 25).setRange(0, 360).setCaptionLabel("Angle");

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
    controllers.add(setSizeX);
    controllers.add(setSizeY);
    
    controllers.add(setPositionXCutout);
    controllers.add(setPositionYCutout);
    controllers.add(setAngleCutout);
  }

  void changeMaterial(float eventNumber)
  {
    shapeCurrentlyPluggedTo.getShape().setMaterial(materials.get((int)eventNumber));
  }
  
  public void plugTo(Connection c)
  {
    if (this.shapeCurrentlyPluggedTo != null)
    {
      setSizeX.unplugFrom(this.shapeCurrentlyPluggedTo);
      setSizeY.unplugFrom(this.shapeCurrentlyPluggedTo);
    }
    if (this.connectionCurrentlyPluggedTo != null)
    {
      setAngle.unplugFrom(this.connectionCurrentlyPluggedTo);
    }
    if (this.cutoutCurrentlyPluggedTo != null)
    {
      setPositionXCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
      setPositionYCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
      setAngleCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
    }
    showConnectionProperties();
    setAngle.plugTo(c).setValue(c.getAngle());

    this.connectionCurrentlyPluggedTo = c;
  }

  public void plugTo(Shape s)
  {
    if (this.shapeCurrentlyPluggedTo != null)
    {
      setSizeX.unplugFrom(this.shapeCurrentlyPluggedTo);
      setSizeY.unplugFrom(this.shapeCurrentlyPluggedTo);
    }
    if (this.connectionCurrentlyPluggedTo != null)
    {
      setAngle.unplugFrom(this.connectionCurrentlyPluggedTo);
    }
    if (this.cutoutCurrentlyPluggedTo != null)
    {
      setPositionXCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
      setPositionYCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
      setAngleCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
    }
    showShapeProperties();
    setSizeX.plugTo(s).setValue(s.getValue(0));
    setSizeY.plugTo(s).setValue(s.getValue(1));
    setMaterial.setCaptionLabel(s.getShape().getMaterial().getMaterialName());

    this.shapeCurrentlyPluggedTo = s;
  }
  
  public void plugTo(Cutout c)
  {
    if (this.shapeCurrentlyPluggedTo != null)
    {
      setSizeX.unplugFrom(this.shapeCurrentlyPluggedTo);
      setSizeY.unplugFrom(this.shapeCurrentlyPluggedTo);
    }
    if (this.connectionCurrentlyPluggedTo != null)
    {
      setAngle.unplugFrom(this.connectionCurrentlyPluggedTo);
    }
    if (this.cutoutCurrentlyPluggedTo != null)
    {
      setPositionXCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
      setPositionYCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
      setAngleCutout.unplugFrom(this.connectionCurrentlyPluggedTo);
    }
    showCutoutProperties();
    setAngleCutout.plugTo(c).setValue(c.getAngleCutout());
    setPositionXCutout.plugTo(c).setValue(c.getPositionXCutout());
    setPositionYCutout.plugTo(c).setValue(c.getPositionYCutout());

    this.cutoutCurrentlyPluggedTo = c;
  }
  
  private void showCutoutProperties()
  {
    setAngle.hide();
    setPositionXCutout.show();
    setPositionYCutout.show();
    setAngleCutout.show();
    setMaterial.hide();
    setSizeX.hide();
    setSizeY.hide();
  }
  
  private void showConnectionProperties()
  {
    setAngle.show();
    setMaterial.hide();
    setSizeX.hide();
    setSizeY.hide();
    setPositionXCutout.hide();
    setPositionYCutout.hide();
    setAngleCutout.hide();
  }
  
  private void showShapeProperties()
  {
    setAngle.hide();
    setMaterial.show();
    setSizeX.show();
    setSizeY.show();
    setPositionXCutout.hide();
    setPositionYCutout.hide();
    setAngleCutout.hide();
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
    }
  }
}

