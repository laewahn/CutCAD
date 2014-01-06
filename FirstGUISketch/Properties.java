import processing.core.*;
import controlP5.*;
import java.util.*;

class Properties
{
  private ArrayList<Controller> controllers;
  // private ArrayList<Material> materials;
  private Slider setSizeX, setSizeY, setThickness;
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
    // this.materials = materials;
    this.currentlyPluggedTo = null;

    setSizeX = cp5.addSlider("setSizeX")
      .setPosition(100, 25)
        .setRange(10, 255)
          .setCaptionLabel("Width");

    setSizeY = cp5.addSlider("setSizeY")
      .setPosition(300, 25)
        .setRange(10, 255)
          .setCaptionLabel("Length");

    setThickness = cp5.addSlider("setThickness")
      .setPosition(500, 25)
        .setRange(1, 255)
          .setCaptionLabel("Thickness");

    // setMaterial = cp5.addDropdownList("setMaterial")
    //   // .setPosition(500, 25)
    //     // .setRange(1, 255)
    //       // .setCaptionLabel("Material");
    //       customize(setMaterial);

    controllers.add(setSizeX);
    controllers.add(setSizeY);
    controllers.add(setThickness);
    // controllers.add(setMaterial);
  }

  // void customize(DropdownList ddl) 
  // {
  //   // ddl.setBackgroundColor(color(190));
  //   ddl.setItemHeight(20);
  //   ddl.setBarHeight(15);
  //   ddl.captionLabel().set("Material");
  //   ddl.captionLabel().style().marginTop = 3;
  //   ddl.captionLabel().style().marginLeft = 3;
  //   ddl.valueLabel().style().marginTop = 3;
  //   for(Material m : materials) 
  //   {
  //     ddl.addItem(m.getMaterialName(), materials.indexOf(m));
  //   }
  //   ddl.scroll(materials.indexOf(GShape.getMaterial());
  //   // ddl.setColorBackground(color(60));
  //   // ddl.setColorActive(color(255,128));
  // }

  // void controlEvent(ControlEvent theEvent) 
  // {
  //   // PulldownMenu is if type ControlGroup.
  //   // A controlEvent will be triggered from within the ControlGroup.
  //   // therefore you need to check the originator of the Event with
  //   // if (theEvent.isGroup())
  //   // to avoid an error message from controlP5.

  //   if (theEvent.isGroup()) {
  //     // check if the Event was triggered from a ControlGroup
  //     println(theEvent.group().value()+" from "+theEvent.group());
  //   } else if(theEvent.isController()) {
  //    println(theEvent.controller().value()+" from "+theEvent.controller());
  //    // GShape.setMaterial(materials.get(theEvent.controller().value())); //???
  //   }
  // }


  public void plugTo(Shape s)
  {
    if (this.currentlyPluggedTo != null)
    {
      setSizeX.unplugFrom(this.currentlyPluggedTo);
      setSizeY.unplugFrom(this.currentlyPluggedTo);
      setThickness.unplugFrom(this.currentlyPluggedTo.getShape());
      // setMaterial.unplugFrom(this.currentlyPluggedTo.getShape());
    }
    setSizeX.plugTo(s).setValue(s.getValue(0));
    setSizeY.plugTo(s).setValue(s.getValue(1));
    setThickness.plugTo(s.getShape()).setValue(s.getShape().getThickness());
    // setMaterial.plugTo(s.getShape()).setValue(s.getShape().getMaterial());

    this.currentlyPluggedTo = s;
  }

  public void hide()
  {
    for (Controller c : controllers)
    {
      c.hide();
    }
    this.hidden = true;
  }

  public void show()
  {
    for (Controller c : controllers)
    {
      c.show();
    }
    this.hidden = false;
  }

  public void drawProperties(PApplet p)
  {
    p.fill(180);
    p.rect(posX, posY, sizeX, sizeY);
    if (this.hidden)
    {
      p.textSize(24);
      p.fill(255);
      p.text("No Object selected", p.width/2-125, 30);
    }
  }
}

