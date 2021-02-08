// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

import jhi.flapjack.io.*;

public class ChromosomeMap extends XMLRoot implements Iterable<Marker>, Comparable<ChromosomeMap>
{
	private String name;
	private double length;
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

	public double getLength()
		{ return length; }

	public void setLength(double length)
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

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public Iterator<Marker> iterator()
		{ return markers.iterator(); }

	@Override
	public int compareTo(ChromosomeMap other)
	{
		// Try and sort numerical "names" in numerical order
		if (nameIsNumber() && other.nameIsNumber())
			return Double.compare(Double.parseDouble(name), Double.parseDouble(other.name));
		else if (nameIsNumber() && !other.nameIsNumber())
			return -1;
		else if (!nameIsNumber() && other.nameIsNumber())
			return 1;

		// Then stick string names at the end of any numbered list
		return name.compareTo(other.name);
	}

	private boolean nameIsNumber()
	{
		try { Double.parseDouble(name); return true; }
		catch (Exception e) { return false; }
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

		// If the length hasn't been set at import time, then we'll use the
		// position of the last marker as the map's length
		if (length == 0f && markers.size() > 0)
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