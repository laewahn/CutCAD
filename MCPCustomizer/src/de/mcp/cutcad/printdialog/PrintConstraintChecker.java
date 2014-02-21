package de.mcp.cutcad.printdialog;

import java.util.ArrayList;

class PrintConstraintChecker {

	private ArrayList<PrintInstance> toBeCheckedInstances;
	
	private Printer parent;
	
	private String overlapMessage;
	
	PrintConstraintChecker(ArrayList<PrintInstance> toBeCheckedInstances, Printer parent) {
		this.toBeCheckedInstances = toBeCheckedInstances;
		this.parent = parent;
	}
	
	public String checkPrintConstraints()
	  {
		  this.overlapMessage = "";
		  if(!(toBeCheckedInstances.size() > 0))
		  {
			  return "There are no shapes that can be printed";
		  } else
		  {
			  return checkPlacedShapes();
		  }
	  }
	
	private String checkPlacedShapes()
	  {
		  boolean conditionsMet = false;
		  boolean placedShapeNoMaterial = false;
		  boolean overLapped = false;
		  for(int i = 0; i < this.toBeCheckedInstances.size(); i++)
		  {
			if(this.toBeCheckedInstances.get(i).checkPlacedShapes())
			{
				if(!checkOverlap(i))
				{
					if(this.toBeCheckedInstances.get(i).getMaterial().getMaterialName().equals("Nothing 0,5 mm"))
					{
						placedShapeNoMaterial = true;
					} else
					{
						conditionsMet = true;
					}
				} else
				{
					overLapped = true;
					conditionsMet = false;
				}
			}
		  }
		  if(!conditionsMet)
		  {
			  if(overLapped)
			  {
				  return overlapMessage;
			  } else if (placedShapeNoMaterial)
			  {
				  return "No shape has been placed with a material assigned";
			  } else
			  {
				  return "No shape has been placed";
			  }
		  } else
		  {
			  if(placedShapeNoMaterial)
			  {  
				  this.parent.confirmationPrint(0);
				  return "There are object placed without material assigned, continue?";
			  } else
			  {
				  return checkUnplacedShapes();
			  }
		  }
	  }
	
	  private boolean checkOverlap(int printInstanceIndex)
	  {
		  boolean overLapped = false;
		  String result = this.toBeCheckedInstances.get(printInstanceIndex).checkOverlap();
		  if(!result.equals("no overlap"))
		  {
			  if(!this.overlapMessage.equals(""))
			  {
				  this.overlapMessage = this.overlapMessage + ", " + result;
			  } else
			  {
				  this.overlapMessage = this.overlapMessage + result;
			  }
			  overLapped = true;
		  }
		  return overLapped;
	  }
	  
	  String checkUnplacedShapes()
	  {
		  boolean unplacedShapesFound = false;
		  String result = "";
		  for(int i = 0; i < this.toBeCheckedInstances.size(); i++)
		  {
			  if(this.toBeCheckedInstances.get(i).getUnplacedShapes().size() > 0)
			  {
				  if(result.equals(""))
				  {
					  result = "there are unplaced shapes for material(s): " + this.toBeCheckedInstances.get(i).getMaterial().getMaterialName();
				  } else
				  {
					  result = result + ", " + this.toBeCheckedInstances.get(i).getMaterial().getMaterialName();
				  }
				  unplacedShapesFound = true;
			  }
		  }
		  if(unplacedShapesFound)
		  {
			  this.parent.confirmationPrint(1);
			  return result;
		  } else
		  {
			  return "passed"; 
		  }
	  }
	
}
