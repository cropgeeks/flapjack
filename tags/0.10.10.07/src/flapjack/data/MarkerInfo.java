// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

/**
 * Wrapper class that represents a marker within a view. We store a reference to
 * the marker itself, along with its index position in the original dataset.
 */
public class MarkerInfo extends XMLRoot
{
	// A reference to the marker, and to its index within the original data
	Marker marker;
	int index;

	boolean selected = true;

	public MarkerInfo()
	{
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


	// Other methods
}