package de.mcp.customizer.application;
import controlP5.Button;
import controlP5.ControlP5;
import controlP5.ControllerView;
import controlP5.ListBox;

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

  void addTool(Tool theTool) 
  {

  }

  Tool getSelectedTool() 
  {
  	return null;
  }
}

