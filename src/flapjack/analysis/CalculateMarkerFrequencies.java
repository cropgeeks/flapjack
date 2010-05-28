// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class CalculateMarkerFrequencies
{
	private DataSet dataSet;
	private int stateCount;

	public CalculateMarkerFrequencies(DataSet dataSet)
	{
		this.dataSet = dataSet;

		stateCount = dataSet.getStateTable().size();
	}

	public void calculate()
	{
		int chromosomeCount = dataSet.countChromosomeMaps();

		for (int ch = 0; ch < chromosomeCount; ch++)
		{
			ChromosomeMap map = dataSet.getMapByIndex(ch);

			for (int marker = 0; marker < map.countLoci(); marker++)
			{
				// Quit if frequencies have already been calculated
				if (map.getMarkerByIndex(marker).getFrequencies() != null)
					break;

				float[] freqs = getFrequencies(ch, marker);

				map.getMarkerByIndex(marker).setFrequencies(freqs);
			}
		}
	}

	float[] getFrequencies(int chromosome, int marker)
	{
		// An array where each element holds a count of alleles for the state at
		// that index location (eg, 0=unknown, 1=A, 2=A/T, etc)
		float[] freqs = new float[stateCount];

		for (Line line: dataSet.getLines())
		{
			int state = line.getState(chromosome, marker);
			freqs[state]++;
		}

		// This is the number of alleles we expect to find in each marker
		float alleleCount = dataSet.countLines();

		// Convert the counts into frequency ratios (eg 0.5 for 50%)
		for (int i = 0; i < freqs.length; i++)
			freqs[i] = freqs[i] / alleleCount;

		return freqs;
	}
}