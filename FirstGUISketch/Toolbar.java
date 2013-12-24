import processing.core.*;
import controlP5.*;

class Toolbar extends ListBox
{
  Toolbar(ControlP5 cp5, String theName)
  {
    super(cp5, cp5.getTab("default"), theName, 0, 0, 100, 10);
  }

  void addCustomItem(String theName, int theValue, ControllerView<Button> theView) 
  {
    addItem(theName, theValue);
    buttons.get(buttons.size()-1).setView(theView);
  }
}

