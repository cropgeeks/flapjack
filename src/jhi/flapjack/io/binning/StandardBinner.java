// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import java.text.*;
import java.util.*;

class StandardBinner implements IBinner
{
	private float min = 0f;
	private float max = 1f;

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

	public ArrayList<float[]> getBinSummary()
	{
		ArrayList<float[]> list = new ArrayList<>();

		for (int i = 0; i < numBins; i++)
		{
			float f1 = i*binSize;
			float f2 = i*binSize + binSize;

			list.add(new float[] { f1, f2 });
		}

		return list;
	}
}