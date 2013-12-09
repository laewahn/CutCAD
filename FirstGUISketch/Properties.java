import processing.core.*;
import controlP5.*;
import java.util.*;

class Properties
{
    private ArrayList<Controller> controllers;
    private Slider setSizeX, setSizeY, setThickness;
    private Rectangle currentlyPluggedTo;
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
        this.currentlyPluggedTo = null;

        setSizeX = cp5.addSlider("setSizeX")
            .setPosition(100, 25)
            .setRange(0, 255)
            .setCaptionLabel("Width");

        setSizeY = cp5.addSlider("setSizeY")
            .setPosition(300, 25)
            .setRange(0, 255)
            .setCaptionLabel("Length");

        setThickness = cp5.addSlider("setThickness")
            .setPosition(500, 25)
            .setRange(0, 255)
            .setCaptionLabel("Thickness");

        controllers.add(setSizeX);
        controllers.add(setSizeY);
        controllers.add(setThickness);
    }

    public void plugTo(Rectangle r)
    {
        if (this.currentlyPluggedTo != null)
        {
                setSizeX.unplugFrom(this.currentlyPluggedTo);
                setSizeY.unplugFrom(this.currentlyPluggedTo);
                setThickness.unplugFrom(this.currentlyPluggedTo);
        }
        setSizeX.plugTo(r).setValue(r.getSizeX());
        setSizeY.plugTo(r).setValue(r.getSizeY());
        setThickness.plugTo(r).setValue(r.getThickness());

        this.currentlyPluggedTo = r;
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
        p.rect(posX,posY,sizeX,sizeY);
        if (this.hidden)
        {
            p.textSize(24);
            p.fill(255);
            p.text("No Object selected", p.width/2-125, 30);
        }
    }
}