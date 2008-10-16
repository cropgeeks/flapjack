package flapjack.data;

import java.util.*;

import flapjack.io.*;

public class ChromosomeMap extends XMLRoot implements Iterable<Marker>
{
	private String name;
	private float length;

	private Vector<Marker> markers = new Vector<Marker>();

	// A list of vectors of Features (basically, each vector of features
	// represents one "track" - and we may have multiple tracks
	private Vector<Vector<Feature>> features = new Vector<Vector<Feature>>();

	public ChromosomeMap()
	{
	}

	public ChromosomeMap(String name)
	{
		this.name = new String(name);

		Vector<Feature> track1 = new Vector<Feature>();
		track1.add(new QTL("test1-1", 12, 10, 15, 0));
		track1.add(new QTL("test2-1", 25, 22, 30, 0));
		features.add(track1);

		Vector<Feature> track2 = new Vector<Feature>();
		track2.add(new QTL("test2-1", 5, 3, 8, 0));
		track2.add(new QTL("test2-2", 25, 22, 30, 0));
		track2.add(new QTL("test2-3", 46, 41, 48, 0));
		track2.add(new QTL("test2-4", 56, 51, 58, 0));
		features.add(track2);
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

	public Vector<Marker> getMarkers()
		{ return markers; }

	public void setMarkers(Vector<Marker> markers)
		{ this.markers = markers; }

	public Vector<Vector<Feature>> getFeatures()
		{ return features; }

	public void setFeatures(Vector<Vector<Feature>> features)
	{ 	this.features = features; }


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
		if (getMarkerIndex(markerName) != -1)
			return true;

		return false;
	}

	public void addMarker(Marker marker)
		throws DataFormatException
	{
		markers.add(marker);
	}

	void sort()
	{
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
}