// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binning;

class SplitBinner implements IBinner
{
	private Float min = 0f;
	private Float max = 1f;

	private int lBinCount, rBinCount;
	private float lBinSize, rBinSize;
	private float split;

	SplitBinner(int lBinCount, float split, int rBinCount)
	{
		this.lBinCount = lBinCount;
		this.rBinCount = rBinCount;
		this.split = split;

		// (max - max) / numBins but tweaked for either side of the split
		lBinSize = (split - min) / lBinCount;
		rBinSize = (max - split) / rBinCount;

		System.out.println("rBinSize: " + rBinSize);
	}

	public int bin(float value)
	{
		// Left of the split...
		if (value <= split)
		{
			int bin = (int) ((value - min) / lBinSize);

			if (bin == lBinCount)
				bin = lBinCount-1;

			return bin;
		}

		// Right of the split...
		else
		{
			int bin = (int) ((value - split) / rBinSize);

			if (bin == rBinCount)
				bin = rBinCount-1;

			// Offset the value by the number of bins to the left of the split
			bin += lBinCount;

			return bin;
		}
	}
}