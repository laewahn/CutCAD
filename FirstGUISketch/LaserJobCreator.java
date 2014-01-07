import processing.core.*;

import com.t_oster.liblasercut.dithering.*;
import com.t_oster.liblasercut.vectoroptimizers.*;
import com.t_oster.liblasercut.utils.*;
import com.t_oster.liblasercut.drivers.*;
import com.t_oster.liblasercut.laserscript.*;
import com.t_oster.liblasercut.platform.*;

import com.t_oster.liblasercut.*;

import java.util.*;

import toxi.geom.*;

public class LaserJobCreator
{
  private EpilogZing epilogZing = null;
  private PowerSpeedFocusFrequencyProperty psffProperty = null;
  private RasterPart rp = null;
  private VectorPart vp = null;
  private int DPI;
  
  public LaserJobCreator()
  {
    
  }
  
  public void setLaserCutter(String device, String ipAddress)
  {
    int deviceNumber = new LaserCutter(device).returnDeviceNumber();
    switch(deviceNumber)
    {
       case 0: epilogZing = new EpilogZing(ipAddress); 
    }
  }
  
  public void setPsffProperty(int power, int speed, float focus, int frequency)
  {
    psffProperty = new PowerSpeedFocusFrequencyProperty();
    psffProperty.setProperty("power", power);
    psffProperty.setProperty("speed", speed);
    psffProperty.setProperty("focus", focus);
    psffProperty.setProperty("frequency", frequency);
  }
  
  public void setDPI(int DPI)
  {
    this.DPI = DPI; 
  }
  
  public void newVectorPart()
  {
    vp = new VectorPart(psffProperty,DPI); 
  }
  
  public void addVerticesToVectorPart(ArrayList<Vec2D> shape)
  {
     if(shape.get(0) != null)
     {
        vp.moveto((int)shape.get(0).getComponent(0),(int)shape.get(0).getComponent(1));
        for(int i = 1; i < shape.size(); i++)
        {
          vp.lineto((int)shape.get(i).getComponent(0),(int)shape.get(i).getComponent(1));
        }
        vp.lineto((int)shape.get(0).getComponent(0),(int)shape.get(0).getComponent(1));
     }
  }
  
  public void sendLaserjob()
  {
    LaserJob job = new LaserJob("Processing", "123", "username");//title, name, user
    if (rp != null)
    {
      job.addPart(rp);
    }
    if (vp != null)
    {
      job.addPart(vp);
    }
    try
    {
      epilogZing.sendJob(job);
    }
    catch(IllegalJobException ije)
    {
      System.out.println(ije.toString());
    }
    catch(Exception e)
    {
      System.out.println(e.toString());
    }
  }
}
