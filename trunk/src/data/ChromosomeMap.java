package flapjack.data;

import java.util.*;

import flapjack.io.*;

public class ChromosomeMap
{
	private String name;

	private Vector<Marker> markers = new Vector<Marker>();

	// Hashtable that stores the marker names for quick determination of non-
	// unique entries. Stores key<name>, int<count>
	private Hashtable<String,Integer> names = new Hashtable<String,Integer>();

	public ChromosomeMap(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void addMarker(Marker marker)
		throws DataFormatException
	{
		boolean exists = names.containsKey(marker.getName());

//		if (names.containsKey(marker.getName()))
//		{
//			throw new DataFormatException("A marker with the name '"
//				+ marker.getName() + "' already exists in chromosome '"
//				+ name + "'");
//		}

		if (exists == false)
			names.put(marker.getName(), 1);
		else
		{
//			System.out.println("Added duplicate marker '" + marker.getName()
//				+ "' to chromosome '" + name + "'");

			int count = names.get(marker.getName());
			names.put(marker.getName(), count+1);
		}

		markers.add(marker);
	}

	public void print()
	{
		System.out.println("Chromosome " + name);

		for (Marker marker: markers)
			System.out.println("  " + marker.getName() + "\t" + marker.getPosition());
	}

	void sort()
	{
		Collections.sort(markers);
	}
}