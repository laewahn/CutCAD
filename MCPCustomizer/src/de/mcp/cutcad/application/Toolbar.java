package de.mcp.cutcad.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ControllerView;
import controlP5.ListBox;
import de.mcp.cutcad.application.tools.drawing.PolygonTool;
import de.mcp.cutcad.application.tools.drawing.RectangleTool;
import de.mcp.cutcad.application.tools.drawing.SymmetricPolygonTool;
import de.mcp.cutcad.application.tools.drawing.TrapeziumTool;
import de.mcp.cutcad.application.tools.fileManagement.LoadTool;
import de.mcp.cutcad.application.tools.fileManagement.NewProjectTool;
import de.mcp.cutcad.application.tools.fileManagement.PrintTool;
import de.mcp.cutcad.application.tools.fileManagement.SaveTool;
import de.mcp.cutcad.application.tools.objectImport.ImportSTLTool;
import de.mcp.cutcad.application.tools.objectImport.ImportSVGTool;
import de.mcp.cutcad.application.tools.objectManipulation.ChangeSTLTool;
import de.mcp.cutcad.application.tools.objectManipulation.ConnectTool;
import de.mcp.cutcad.application.tools.objectManipulation.CopyTool;
import de.mcp.cutcad.application.tools.objectManipulation.CutoutTool;
import de.mcp.cutcad.application.tools.objectManipulation.DeleteTool;
import de.mcp.cutcad.application.tools.objectManipulation.SelectTool;

/**
 * Defines an interface for an observer of the toolbar. The observer will be informed whenever a new tool was selected in the toolbar.
 * @author dennis
 *
 */
interface ToolbarDelegate {
	
	/**
	 * This method will be called when a new tool was selected in the toolbar.
	 * @param theTool The tool that was selected.
	 */
	public void toolWasSelected(Tool theTool);
}


class Toolbar extends ListBox
{

  public static final int DEFAULT_TOOLBAR_WIDTH = 40;
  
  private List<Tool> tools;
  private ToolbarDelegate delegate;
  
  public static Toolbar createDefaultToolbar(CutCADApplet application) {
	  Toolbar newToolbar = new Toolbar(application.cp5, application);
	  
	  newToolbar.setPosition(0, 50).setSize(DEFAULT_TOOLBAR_WIDTH, 900).setItemHeight(DEFAULT_TOOLBAR_WIDTH).disableCollapse().hideBar();
	    
	  Tool[] tools = new Tool[]{
	    	  new NewProjectTool(application, application.container),
	    	  new LoadTool(application, application.container),
	    	  new SaveTool(application, application.container),
	  	      new SelectTool(application, application.container),
	  	      new RectangleTool(application, application.container),
	  	      new SymmetricPolygonTool(application, application.container),
	  	      new TrapeziumTool(application, application.container),
	  	      new PolygonTool(application, application.container),
	  	      new ConnectTool(application, application.container),
	  	      new DeleteTool(application, application.container),
	  	      new CutoutTool(application, application.container),
	  	      new CopyTool(application, application.container),
	  	      new PrintTool(application, application.container),
	  	      new ImportSVGTool(application, application.container),
	  	      new ImportSTLTool(application, application.container),
	  	      new ChangeSTLTool(application, application.container)
	  	    };
	    
	    newToolbar.addTools(Arrays.asList(tools));
	    newToolbar.delegate.toolWasSelected(tools[3]);
	    
	    return newToolbar;
  }
  
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

