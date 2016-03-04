// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import java.util.*;

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
	}

	float getSplit()
		{ return split; }

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

	public ArrayList<float[]> getBinSummary()
	{
		ArrayList<float[]> list = new ArrayList<>();

		// Left bins
		for (int i = 0; i < lBinCount; i++)
		{
			float f1 = i*lBinSize;
			float f2 = i*lBinSize + lBinSize;

			list.add(new float[] { f1, f2 });
		}

		// Right bins
		for (int i = 0; i < rBinCount; i++)
		{
			float f1 = split + i*rBinSize;
			float f2 = split + i*rBinSize + rBinSize;

			list.add(new float[] { f1, f2 });
		}

		return list;
	}
}