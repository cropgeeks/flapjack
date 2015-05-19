// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.brapi;

import uk.ac.hutton.brapi.resource.*;

public class BrapiRequest
{
	// The resource selected by the user for use
	private XmlResource resource;

	private int mapID;

	public XmlResource getResource()
		{ return resource; }

	public void setResource(XmlResource resource)
		{ this.resource = resource; }

	public int getMapID()
		{ return mapID; }

	public void setMapID(int mapIndex)
		{ this.mapID = mapIndex; }
}