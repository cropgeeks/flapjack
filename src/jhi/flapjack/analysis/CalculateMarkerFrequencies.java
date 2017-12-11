// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class CalculateMarkerFrequencies extends SimpleJob
{
	public static final int ALLELE_METHOD = 0;
	public static final int GENOTYPE_METHOD = 1;
	private int method;

	private DataSet dataSet;
	private StateTable stateTable;

	// Used by the allele method:
	private String[] alleles;
	private ArrayList<Mapper> mapping;


	public CalculateMarkerFrequencies(DataSet dataSet, int method)
	{
		this.dataSet = dataSet;
		this.method = method;
		stateTable = dataSet.getStateTable();

		// Quick count of total markers...
		for (int ch = 0; ch < dataSet.countChromosomeMaps(); ch++)
			maximum += dataSet.getMapByIndex(ch).countLoci();
	}

	public void runJob(int index)
		throws Exception
	{
		if (method == ALLELE_METHOD)
			prepareAlleleMapping();

		for (int ch = 0; ch < dataSet.countChromosomeMaps(); ch++)
		{
			ChromosomeMap map = dataSet.getMapByIndex(ch);

			for (int marker = 0; marker < map.countLoci() && okToRun; marker++)
			{
				progress++;

				// Quit if frequencies have already been calculated
				if (map.getMarkerByIndex(marker).frequencies() != null)
					continue;

				// Count alleles
				if (method == ALLELE_METHOD)
				{
					float[] freqs = getAlleleFrequencies(ch, marker);
					map.getMarkerByIndex(marker).setFrequencies(freqs);
				}
				// Or count genotypes
				else
				{
					float[] freqs = getGenotypeFrequencies(ch, marker);
					map.getMarkerByIndex(marker).setFrequencies(freqs);
				}
			}
		}
	}

	private void prepareAlleleMapping()
	{
		// We need to start by working out how many total alleles there are.
		// This count is not the same as the total number of states: A, T, A/T
		// is three states but two alleles: A and T
		HashMap<String, String> names = stateTable.uniqueAlleles();
		alleles = names.keySet().toArray(new String[0]);

		// Build a mapping construct that maps states (such as A/T) back to the
		// indexes in the frequencies array (for A and T) so their count can be
		// incremented when these states are encountered in the data
		mapping = new ArrayList<>();
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			Mapper m = new Mapper(state.getStates().length);
			mapping.add(m);

			for (int j = 0; j < state.getStates().length; j++)
				for (int k = 0; k < alleles.length; k++)
					if (alleles[k].equals(state.getStates()[j]))
						m.indexes[j] = k;
		}

		Marker.setAlleles(alleles);
	}

	float[] getGenotypeFrequencies(int chromosome, int marker)
	{
		// An array where each element holds a count of alleles for the state at
		// that index location (eg, 0=unknown, 1=A, 2=A/T, etc)
		float[] freqs = new float[stateTable.size()];
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

	float[] getAlleleFrequencies(int chromosome, int marker)
	{
		float[] freqs = new float[alleles.length];
		float lineCount = 0;

		for (Line line: dataSet.getLines())
		{
			int state = line.getState(chromosome, marker);

			// Only count this line if the state isn't unknown
			if (state != 0)
			{
				mapping.get(state).increment(freqs);
				lineCount++;
			}
		}

		// Convert the counts into frequency ratios (eg 0.5 for 50%)
		for (int i = 0; i < freqs.length; i++)
			freqs[i] = freqs[i] / lineCount;

		return freqs;
	}

	// This class holds simple mapping info that lets us map from an AlleleState
	// object (eg A/T) to the indexes within the frequency array holding the
	// counts for its alleles (eg A and T). It also holds a "value" that should
	// be used to increment the frequency count by each time this state is found
	static class Mapper
	{
		int[] indexes;
		float value;

		Mapper(int size)
		{
			indexes = new int[size];
			value = (size == 1) ? 1.0f: 0.5f;
		}

		private void increment(float[] freqs)
		{
			for (int fIndex: indexes)
				freqs[fIndex] += value;
		}
	}
}