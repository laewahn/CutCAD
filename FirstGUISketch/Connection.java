import processing.core.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.processing.*;

public class Connection 
{
    private Edge edge1, edge2;

    public Connection()
    {
        
    }

    public Connection(Edge edge1, Edge edge2)
    {
        this.edge1 = edge1;
        this.edge2 = edge2;
    }

    public Edge getEdge1()
    {
        return this.edge1;
    }

    public Edge getEdge2()
    {
        return this.edge2;
    }

    public void setEdge1(Edge e)
    {
        this.edge1 = e;
    }

    public void setEdge2(Edge e)
    {
        this.edge2 = e;
    }
}