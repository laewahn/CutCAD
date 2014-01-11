package de.mcp.customizer.printdialog.lasercutter;
public class LaserCutter
{
  private int device;
  
  public LaserCutter(String device)
  {
    setDevice(device); 
  }
  
  public String returnDevice()
  {
    switch (device)
    {
       case 0: return "epilogZing";
       default: return "no selected";
    } 
  }
  
  public int returnDeviceNumber()
  {
    return device;  
  }
  
  public void setDevice(String device)
  {
    if(device == "epilogZing")
    {
       this.device = 0;
    } else
    {
      this.device = -1; 
    }
  }
}

/*public enum LaserCutter extends java.lang.Enum
{
  
}*/
