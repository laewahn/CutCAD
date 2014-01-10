import java.util.ArrayList;

import processing.core.PApplet;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.DropdownList;
import controlP5.Slider;

class Properties
{
  private ArrayList<Controller> controllers;
  private ArrayList<Material> materials;
  private Slider setSizeX, setSizeY;
  private DropdownList setMaterial;
  private Shape currentlyPluggedTo;
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
    this.materials = new AllMaterials().getMaterials();
    this.currentlyPluggedTo = null;

    setSizeX = cp5.addSlider("setSizeX").setPosition(100, 25).setRange(10, 255).setCaptionLabel("Width");

    setSizeY = cp5.addSlider("setSizeY")
      .setPosition(300, 25)
        .setRange(10, 255)
          .setCaptionLabel("Length");

    setMaterial = cp5.addDropdownList("setMaterial")
      .setPosition(sizeX-225, (sizeY-25)/2+25)
        .setSize(200,400);

    controllers.add(setSizeX);
    controllers.add(setSizeY);
  }

  void customize(DropdownList ddl, Shape s) 
  {
    Material materialOfShape = s.getShape().getMaterial();

    ddl.setItemHeight(25);
    ddl.setBarHeight(25);
    ddl.setCaptionLabel(materialOfShape.getMaterialName());
    ddl.captionLabel().style().setMarginTop(7); //should be central -> dependant on code???


    for(Material m : materials) 
    {
      ddl.addItem(m.getMaterialName(), materials.indexOf(m));
    }
  }

  void changeMaterial(float eventNumber)
  {
    currentlyPluggedTo.getShape().setMaterial(materials.get((int)eventNumber));
  }

  public void plugTo(Shape s)
  {
    if (this.currentlyPluggedTo != null)
    {
      setSizeX.unplugFrom(this.currentlyPluggedTo);
      setSizeY.unplugFrom(this.currentlyPluggedTo);
    }
    setSizeX.plugTo(s).setValue(s.getValue(0));
    setSizeY.plugTo(s).setValue(s.getValue(1));
    customize(setMaterial, s);

    this.currentlyPluggedTo = s;
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

