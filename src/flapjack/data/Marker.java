package flapjack.data;

import java.util.*;

public class Marker extends XMLRoot implements Comparable<Marker>
{
	private String name;
	private float position;
	private int dbKey;

	public Marker()
	{
	}

	public Marker(String name, float position)
	{
		this.name = new String(name);
		this.position = position;
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public float getPosition()
		{ return position; }

	public void setPosition(float position)
		{ this.position = position; }

	public int getDbKey()
		{ return dbKey; }

	public void setDbKey(int dbKey)
		{ this.dbKey = dbKey; }


	// Other methods

	public String toString()
		{ return name; }

	public int compareTo(Marker marker)
	{
		if (marker.position > position)
			return -1;
		else if (marker.position == position)
			return 0;
		else
			return 1;
	}
}