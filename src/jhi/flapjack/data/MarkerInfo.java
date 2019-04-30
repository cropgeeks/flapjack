// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
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

	// A reference to either a MarkerInfo on the all chromosomes view, or from
	// a MarkerInfo on the all chromosomes view back to its equivalent in a
	// real view
	MarkerInfo linkedMarkerInfo;

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
		this.linkedMarkerInfo = markerInfo.linkedMarkerInfo;
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

	public MarkerInfo getLinkedMarkerInfo()
		{ return linkedMarkerInfo; }

	public void setLinkedMarkerInfo(MarkerInfo linkedMarkerInfo)
		{ this.linkedMarkerInfo = linkedMarkerInfo; }

	// Other methods

	public double position()
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

	public void selectMarkerAndLinkedMarker(boolean selected)
	{
		this.selected = selected;
		if (linkedMarkerInfo != null)
			linkedMarkerInfo.setSelected(selected);
	}
}