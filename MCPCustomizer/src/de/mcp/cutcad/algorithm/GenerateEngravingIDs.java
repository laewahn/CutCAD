package de.mcp.cutcad.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import de.mcp.cutcad.model.Connection;
import de.mcp.cutcad.model.primitives.Edge;

/**
 *  This class is used to generate and associate IDs with each edge that is connected to another edge.
 *  Two connected edges are associated with the same ID. The IDs can be engraved on the shapes while laser-cutting,
 *  thus making it easier to find out which edge of which shape needs to be connected to which edge of which other shape.
 *  
 *  IDs consist of two upper case letters of the alphabet (e.g. "AE") and are unique as long as there aren't more than 676 connections. 
 *  If more than 676 connections are created, IDs may be duplicated. However, in such big constructions it should not be likely to have
 *  two pieces which seemingly fit together AND have the same ID but don't actually belong together. Also, duplicate IDs should be rare
 *  unless the user builds something truly enormous.
 */
public class GenerateEngravingIDs
{	
	private static char c1, c2;
	
	/**
	 * This method generates a HashMap which maps each Edge which is part of a connection to an ID.
	 * The IDs of two connected Edges are always the same.
	 * 
	 * @param connections The list of connections the HashMap is generated for
	 * @return The HashMap containing the mapping between Edges and IDs
	 */
	public static HashMap<Edge, String> getEngravingIDMap(ArrayList<Connection> connections)
	{
		resetConnectionID();
		return createIDs(connections);
	}

	private static HashMap<Edge, String> createIDs(ArrayList<Connection> connections)
	{
		HashMap<Edge, String> result = new HashMap<Edge, String>();
		for (Connection c: connections)
		{
			result.put(c.getMasterEdge(), getConnectionID());
			result.put(c.getSlaveEdge(), getConnectionID());
			incrementConnectionID();
		}
		return result;
	}
	
	private static void incrementConnectionID()
	{
		if (c2 < 'Z')
		{
			c2++;
		}
		else if (c1 < 'Z')
		{
			c2 = 'A';
			c1++;
		}
		else
		{
			// If we actually went through all 676 possible connections, we'll just reset the IDs.
			// If anyone is crazy enough to create something this big, he/she can be expected to 
			// try out the different possible connections.
			resetConnectionID();
		}
	}
	
	private static String getConnectionID()
	{
		return new StringBuilder().append(c1).append(c2).toString();
	}
	
	private static void resetConnectionID()
	{
		c1 = 'A';
		c2 = 'A';
	}
}
