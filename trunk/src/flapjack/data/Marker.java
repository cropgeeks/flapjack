package flapjack.data;

import java.util.*;

public class Marker implements Comparable<Marker>
{
	private String name;
	private float position;

	public Marker(String name, float position)
	{
		this.name = name;
		this.position = position;
	}

	public String toString()
		{ return name; }

	public String getName()
		{ return name; }

	public float getPosition()
		{ return position; }

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