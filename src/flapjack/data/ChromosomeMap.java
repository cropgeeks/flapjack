package flapjack.data;

import java.util.*;

import flapjack.io.*;

public class ChromosomeMap implements Iterable<Marker>
{
	private String name;

	private Vector<Marker> markers = new Vector<Marker>();

	// Hashtable that stores the marker names for quick determination of non-
	// unique entries. Stores <name>, <index> (in markers Vector)
	private Hashtable<String,String> names = new Hashtable<String,String>();

	private float length;

	public ChromosomeMap()
	{
	}

	public ChromosomeMap(String name)
	{
		this.name = new String(name);
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public float getLength()
		{ return length; }

	public void setLength(float length)
		{ this.length = length; }

	public Vector<Marker> getMarkers()
		{ return markers; }

	public void setMarkers(Vector<Marker> markers)
		{ this.markers = markers; }

	public Hashtable<String,String> getNames()
		{ return names; }

	public void setNames(Hashtable<String,String> names)
	{ this.names = names; }


	// Other methods

	public String toString()
	{
		return name;
	}



	public Iterator<Marker> iterator()
		{ return markers.iterator(); }

	public Marker getMarkerByIndex(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return markers.get(index);
	}

	public int countLoci()
	{
		return markers.size();
	}

	boolean containsMarker(String markerName)
	{
		return names.containsKey(markerName);
	}

	public void addMarker(Marker marker)
		throws DataFormatException
	{
		if (names.containsKey(marker.getName()))
			System.out.println("Duplicate marker '" + marker.getName()
				+ "' in map '" + name + "'");

//		if (names.containsKey(marker.getName())
//		{
//			throw new DataFormatException("A marker with the name '"
//				+ marker.getName() + "' already exists in chromosome '"
//				+ name + "'");
//		}

		names.put(marker.getName(), "");

		markers.add(marker);
	}

	void sort()
	{
		Collections.sort(markers);

		length = markers.get(markers.size()-1).getPosition();

		System.out.println("Map " + name + " has length " + length);

		// Once the vector is sorted, we can update the hashtable to quickly
		// find index positions given a marker name
		names = new Hashtable<String,String>(markers.size());

		for (int i = 0; i < markers.size(); i++)
		{
			String markerName = markers.get(i).getName();

			// Search for the name. If it doesn't exist, we add it to the hash
			// with the current index position. If it does, we update the entry
			// to contain all positions (eg "4 5 6")
			String index = names.get(markerName);

			if (index == null)
				names.put(markerName, "" + i);
			else
				names.put(markerName, index + " " + i);
		}
	}

	/**
	 * Returns the index positions (position in the marker list) of the marker
	 * with the given name. Remember it could be in more than one location
	 */
	public int[] getMarkerLocations(String markerName)
	{
		String value = names.get(markerName);

		if (value == null)
			return null;

		// If there's no spaces in the index string, then the marker only exists
		// at a single location
		if (value.indexOf(' ') == -1)
			return new int[] { Integer.parseInt(value) };

		// But if there are, then we need to parse out each element
		else
		{
			String[] values = value.split(" ");

			int indices[] = new int[values.length];
			for (int i = 0; i < values.length; i++)
				indices[i] = Integer.parseInt(values[i]);

			return indices;
		}
	}
}