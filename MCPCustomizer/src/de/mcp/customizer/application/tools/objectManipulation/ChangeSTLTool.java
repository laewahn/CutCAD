package de.mcp.customizer.application.tools.objectManipulation;

import de.mcp.customizer.application.MCPCustomizer;
import de.mcp.customizer.application.Tool;
import de.mcp.customizer.model.ObjectContainer;
import de.mcp.customizer.model.STLMesh;

/**
 * The ChangeSTLTool is used to move and rotate an imported STL mesh in the 3D-view
 */
public class ChangeSTLTool extends Tool {

	private STLMesh mesh;

	/**
	 * Creates a ChangeSTLTool
	 * 
	 * @param customizer the main class of the project
	 * @param container the currently loaded ObjectContainer
	 */
	public ChangeSTLTool(MCPCustomizer customizer, ObjectContainer container) {
		super(customizer, container);
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
			this.customizer.properties.show();
			this.customizer.properties.plugTo(mesh);
		}
		super.toolWasSelected();
	}
	
	@Override
	public void toolWasUnselected() {
		super.toolWasUnselected();
	}
}
