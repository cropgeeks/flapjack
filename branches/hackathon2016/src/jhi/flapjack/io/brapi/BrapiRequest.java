// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

public class BrapiRequest
{
	// The resource selected by the user for use
	private XmlResource resource;

	private String username;
	private String password;

	private String mapID;
	private String methodID;


	public String getUsername()
		{ return username; }

	public void setUsername(String username)
		{ this.username = username; }

	public String getPassword()
		{ return password; }

	public void setPassword(String password)
		{ this.password = password; }

	public String getMethodID()
		{ return methodID; }

	public void setMethodID(String methodID)
		{ this.methodID = methodID;	}

	public XmlResource getResource()
		{ return resource; }

	public void setResource(XmlResource resource)
		{ this.resource = resource; }

	public String getMapID()
		{ return mapID; }

	public void setMapID(String mapIndex)
		{ this.mapID = mapIndex; }
}