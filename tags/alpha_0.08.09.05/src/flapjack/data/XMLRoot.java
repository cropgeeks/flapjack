package flapjack.data;

import scri.commons.gui.*;

public class XMLRoot
{
	// A globally unique ID for this object
	protected String guid = SystemUtils.createGUID(16).toLowerCase();

	public XMLRoot()
	{
	}

	public String getGuid()
		{ return guid; }

	public void setGuid(String guid)
		{ this.guid = guid; }
}