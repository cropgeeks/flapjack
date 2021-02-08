// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import java.io.*;
import java.text.*;
import java.util.*;

public class AutoBinner implements IBinner
{
	private int numBins = 10;

	private ArrayList<Integer> histogram;
	private ArrayList<Float> binSizes;
	private float[] binEnds;

	private NumberFormat nf = NumberFormat.getInstance();

	AutoBinner(int numBins, String histFile)
		throws Exception
	{
		this.numBins = numBins;

		readFile(histFile);
		setupBins(binSizes);
	}

	private void readFile(String histFile)
		throws Exception
	{
		binSizes = new ArrayList<Float>();
		histogram = new ArrayList<Integer>();

		try (BufferedReader reader = new BufferedReader(new FileReader(new File(histFile))))
		{
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("position"))
					continue;

				String[] tokens = line.split("\t");
				binSizes.add(nf.parse(tokens[0]).floatValue());
				histogram.add(Integer.parseInt(tokens[1]));
			}
		}
		catch (Exception e) { throw e; }
	}

	private float[] setupBins(ArrayList<Float> binSizes)
	{
		// Calculate total number of entries in all bins
		int total = 0;
		for (Integer bin : histogram)
			total += bin;

		// The point where one bin becomes another
		int cutoff = total / numBins;

		binEnds = new float[numBins];
		int binEnd = 0;
		int current = 0;
		for (int i=0; i < histogram.size(); i++)
		{
			current += histogram.get(i);

			// Set this binEnd and reset current...start looking for next binEnd
			if (current >= cutoff)
			{
				binEnds[binEnd] = binSizes.get(i);
				binEnd++;
				current = 0;
			}
		}
		// Make sure the final binEnd is set to 1 (the maximum value)
		binEnds[numBins-1] = 1f;

		return binEnds;
	}

	@Override
	public int bin(float value)
	{
		int bin = 0;
		while (binEnds[bin] < value)
			bin++;

		return bin;
	}

	@Override
	public ArrayList<float[]> getBinSummary()
	{
		ArrayList<float[]> list = new ArrayList<>();

		for (int i = 0; i < numBins; i++)
		{
			// The first bin starts at 0, but obviously isn't represented in binEnds
			float f1;
			if (i == 0)
				f1 = 0;
			else
				f1 = binEnds[i-1];
			float f2 = binEnds[i];

			list.add(new float[] { f1, f2 });
		}

		return list;
	}
}