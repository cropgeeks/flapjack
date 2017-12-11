// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

public interface ISerializableDB
{
	// These methods start with db* so as not to clash with castor get() methods
	public Object dbGetObject();

	public void dbSetObject(Object obj);

	// Removes (hopefully) the object from memory
	public void dbClear();

	public String dbGetType();


	// An object's database ID will need to be stored by castor, so it's ok to
	// just use get() here
	public String getDatabaseID();
}