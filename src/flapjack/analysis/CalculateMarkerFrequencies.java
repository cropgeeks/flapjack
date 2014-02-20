// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
				if (map.getMarkerByIndex(marker).frequencies() != null)
					break;

				float[] freqs = getGenotypeFrequencies(ch, marker);
//				float[] freqs = getAlleleFrequencies(ch, marker);

				map.getMarkerByIndex(marker).setFrequencies(freqs);
			}
		}
	}

	float[] getGenotypeFrequencies(int chromosome, int marker)
	{
		// An array where each element holds a count of alleles for the state at
		// that index location (eg, 0=unknown, 1=A, 2=A/T, etc)
		float[] freqs = new float[stateCount];
		float lineCount = 0;

		for (Line line: dataSet.getLines())
		{
			int state = line.getState(chromosome, marker);
			freqs[state]++;
			lineCount++;
		}

		// Convert the counts into frequency ratios (eg 0.5 for 50%)
		for (int i = 0; i < freqs.length; i++)
			freqs[i] = freqs[i] / lineCount;

		return freqs;
	}

	class Mapper
	{
		int[] indexes;
		float value;

		Mapper(int size)
		{
			indexes = new int[size];
			value = (size == 1) ? 1.0f: 0.5f;
		}

		void set(int myIndex, int index)
		{
			indexes[myIndex] = index;
		}

		void increment(float[] freqs)
		{
			for (int fIndex: indexes)
			{
				freqs[fIndex] += value;
			}
		}
	}

	float[] getAlleleFrequencies(int chromosome, int marker)
	{
		StateTable stateTable = dataSet.getStateTable();

		// We need to start by working out how many total alleles there are.
		// This count is not the same as the total number of states: A, T, A/T
		// is three states but two alleles: A and T
		Hashtable<String, String> names = stateTable.getUniqueAlleles();
		String[] alleles = names.keySet().toArray(new String[0]);


		// Build a mapping construct that maps states (such as A/T) back to the
		// indexes in the frequencies array (for A and T) so their count can be
		// incremented when these states are encountered in the data
		ArrayList<Mapper> mapping = new ArrayList<>();
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			Mapper m = new Mapper(state.getStates().length);
			mapping.add(m);

			for (int j = 0; j < state.getStates().length; j++)
				for (int k = 0; k < alleles.length; k++)
					if (alleles[k].equals(state.getStates()[j]))
						m.set(j, k);
		}


		float[] freqs = new float[alleles.length];
		float lineCount = 0;

		for (Line line: dataSet.getLines())
		{
			int state = line.getState(chromosome, marker);
			mapping.get(state).increment(freqs);
			lineCount++;
		}

		// Convert the counts into frequency ratios (eg 0.5 for 50%)
		for (int i = 0; i < freqs.length; i++)
			freqs[i] = freqs[i] / lineCount;

		return freqs;
	}
}