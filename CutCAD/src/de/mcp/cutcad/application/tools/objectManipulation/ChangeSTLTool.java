package de.mcp.cutcad.application.tools.objectManipulation;

import de.mcp.cutcad.application.CutCADApplet;
import de.mcp.cutcad.application.Tool;
import de.mcp.cutcad.model.ObjectContainer;
import de.mcp.cutcad.model.STLMesh;

/**
 * The ChangeSTLTool is used to move and rotate an imported STL mesh in the 3D-view
 */
public class ChangeSTLTool extends Tool {

	private STLMesh mesh;

	/**
	 * Creates a ChangeSTLTool
	 * 
	 * @param application the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ChangeSTLTool(CutCADApplet application, ObjectContainer container) {
		super(application, container);
		this.mesh = container.getSTLMesh();
	}
	
	@Override
	public String getIconName() {
		return "MoveSTL.svg";
	}
	
	@Override
	public void toolWasSelected() {
		if(mesh.isStlImported())
		{
			this.application.properties.show();
			this.application.properties.plugTo(mesh);
		}
		super.toolWasSelected();
	}
	
	@Override
	public void toolWasUnselected() {
		super.toolWasUnselected();
	}
}
