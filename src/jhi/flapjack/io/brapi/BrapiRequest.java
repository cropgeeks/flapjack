// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

public class BrapiRequest
{
	// The resource selected by the user for use
	private XmlResource resource;

	private int mapID;

	private String methodID;

	public String getMethodID()
		{ return methodID; }

	public void setMethodID(String methodID)
		{ this.methodID = methodID;	}

	public XmlResource getResource()
		{ return resource; }

	public void setResource(XmlResource resource)
		{ this.resource = resource; }

	public int getMapID()
		{ return mapID; }

	public void setMapID(int mapIndex)
		{ this.mapID = mapIndex; }
}