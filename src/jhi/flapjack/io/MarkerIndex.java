// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

public class MarkerIndex
{
	// The index of the chromosome map within the data set
	public short mapIndex;
	// The index of the marker itself within the map
	public int mkrIndex;

	public MarkerIndex(int mapIndex, int mkrIndex)
	{
		this.mapIndex = (short) mapIndex;
		this.mkrIndex = mkrIndex;
	}
}