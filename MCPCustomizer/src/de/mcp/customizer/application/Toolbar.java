package de.mcp.customizer.application;

import java.util.ArrayList;
import java.util.List;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ControllerView;
import controlP5.ListBox;


interface ToolbarDelegate {
	public void toolWasSelected(Tool theTool);
}


class Toolbar extends ListBox
{
	
  private List<Tool> tools;
  private ToolbarDelegate delegate;
  
  Toolbar(ControlP5 cp5, ToolbarDelegate delegate) {
	  
	  this(cp5, "Toolbar", delegate);
  }
  
  Toolbar(ControlP5 cp5, String name, ToolbarDelegate delegate)
  {
    super(cp5, cp5.getTab("default"), name, 0, 0, 100, 10);
    
    this.delegate = delegate;
    this.tools = new ArrayList<Tool>();
  }

  void addCustomItem(String theName, int theValue, ControllerView<Button> theView) 
  {
    addItem(theName, theValue);
    Button toolbarButton = buttons.get(buttons.size()-1);
    toolbarButton.setView(theView);
    toolbarButton.activateBy(ACTION_RELEASED);
  }

  void addTools(List<Tool> theTools) {
	  for(Tool theTool : theTools) {
		  this.addTool(theTool);
	  }
  }
  
  void addTool(Tool theTool) 
  {
	  int newIndex = buttons.size();
	  
      this.addCustomItem(theTool.getIconName(), newIndex, theTool.getButton());      
      this.tools.add(theTool);
  }

  public void controlEvent(ControlEvent theEvent)
  {
	  if(theEvent.getController() instanceof Button) {
		  Button theButton = (Button) theEvent.getController();
		  int buttonIdx = (int) theButton.getValue();
	  
		  Tool selectedTool = tools.get(buttonIdx);
		  this.delegate.toolWasSelected(selectedTool);
	  }
  } 
}

