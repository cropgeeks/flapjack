// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

import flapjack.io.*;

public class ChromosomeMap extends XMLRoot implements Iterable<Marker>, Comparable<ChromosomeMap>
{
	private String name;
	private float length;
	private boolean isSpecialChromosome;

	private ArrayList<Marker> markers = new ArrayList<>();

	// A list of vectors of Features (basically, each vector of features
	// represents one "track" - and we may have multiple tracks
	private ArrayList<QTL> qtls = new ArrayList<>();

	// Stores the GraphData objects for this chromosome
	private ArrayList<GraphData> graphs = new ArrayList<>();

	public ChromosomeMap()
	{
	}

	public ChromosomeMap(String name)
	{
		this.name = new String(name);
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();
		for (Marker marker: markers)
			marker.validate();
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

	public ArrayList<Marker> getMarkers()
		{ return markers; }

	public void setMarkers(ArrayList<Marker> markers)
		{ this.markers = markers; }

	public ArrayList<QTL> getQtls()
		{ return qtls; }

	public void setQtls(ArrayList<QTL> qtls)
		{ 	this.qtls = qtls; }

	public boolean isSpecialChromosome()
		{ return isSpecialChromosome; }

	public void setSpecialChromosome(boolean isSpecialChromosome)
		{ this.isSpecialChromosome = isSpecialChromosome; }

	public ArrayList<GraphData> getGraphs()
		{ return graphs; }

	public void setGraphs(ArrayList<GraphData> graphs)
		{ this.graphs = graphs; }


	// Other methods

	public String toString()
	{
		return name;
	}



	public Iterator<Marker> iterator()
		{ return markers.iterator(); }

	public int compareTo(ChromosomeMap other)
	{
		return name.compareTo(other.name);
	}

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
		if (getMarkerIndex(markerName) != -1)
			return true;

		return false;
	}

	public void addMarker(Marker marker)
	{
		markers.add(marker);
	}

	public void sort()
	{
		markers.trimToSize();
		Collections.sort(markers);

		length = markers.get(markers.size()-1).getPosition();

		System.out.println("Map " + name + " has length " + length);
	}

	/**
	 * Returns the index positions (position in the marker list) of the marker
	 * with the given name.
	 */
	public int getMarkerIndex(String markerName)
	{
		for (int i = 0; i < markers.size(); i++)
			if (markers.get(i).getName().equals(markerName))
				return i;

		return -1;
	}

	/**
	 * Simple wrapper class around a chromosome map, that stores the map itself
	 * along with the index within whatever data set is currently holding it.
	 * This is only used at load time, and is done this way because too many
	 * existing projects (Oct 2009) exist to force existing map objects to now
	 * hold the index too.
	 */
	public static class Wrapper
	{
		public ChromosomeMap map;
		public short index;

		public Wrapper(ChromosomeMap map, int index)
		{
			this.map = map;
			this.index = (short) index;
		}
	}
}