// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

/**
 * Wrapper class that represents a marker within a view. We store a reference to
 * the marker itself, along with its index position in the original dataset.
 */
public class MarkerInfo extends XMLRoot implements Comparable<MarkerInfo>
{
	// A reference to the marker, and to its index within the original data
	Marker marker;
	int index;

	boolean selected = true;

	public MarkerInfo()
	{
	}

	// Copy constructor
	MarkerInfo(MarkerInfo markerInfo)
	{
		this.marker = markerInfo.marker;
		this.index = markerInfo.index;
		this.selected = markerInfo.selected;
	}

	MarkerInfo(Marker marker, int index)
	{
		this.marker = marker;
		this.index = index;
	}


	// Methods required for XML serialization

	public Marker getMarker()
		{ return marker; }

	public void setMarker(Marker marker)
		{ this.marker = marker; }

	public int getIndex()
		{ return index; }

	public void setIndex(int index)
		{ this.index = index; }

	public boolean getSelected()
		{ return selected; }

	public void setSelected(boolean selected)
		{ this.selected = selected; }

	public boolean dummyMarker()
		{ return marker.dummyMarker(); }


	// Other methods

	public float position()
	{
		if (dummyMarker())
			return marker.getRealPosition();
		else
			return marker.getPosition();
	}

	public int compareTo(MarkerInfo markerInfo)
	{
		return marker.compareTo(markerInfo.marker);
	}
}