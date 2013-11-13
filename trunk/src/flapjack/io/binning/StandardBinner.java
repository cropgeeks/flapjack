// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binning;

class StandardBinner implements IBinner
{
	private Float min = 0f;
	private Float max = 1f;

	private int numBins = 10;
	private float binSize;

	StandardBinner(int numBins)
	{
		this.numBins = numBins;

		binSize = (max - min) / numBins;
	}

	public int bin(float value)
	{
		int bin = (int) ((value - min) / binSize);

		// With 10 bins, "1" would map to bin 10, but we really want
		// to include it in the 0.9-1.0 range, so tweak it to bin 9
		if (bin == numBins)
			bin = numBins-1;

		return bin;
	}

	// Test method...
	void range(int bin)
	{

	}
}