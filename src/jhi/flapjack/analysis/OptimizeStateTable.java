// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;

public class OptimizeStateTable
{
	private DataSet dataSet;
	private StateTable stateTable;

	public OptimizeStateTable(DataSet dataSet)
	{
		this.dataSet = dataSet;

		stateTable = dataSet.getStateTable();
	}

	public void collapseHomzEncodedAsHet()
	{
		// Search the state table, looking for instances of homozygotes that
		// have been encoded in the heterozygote style (eg A/A when we want A)
		for (int i = 1; i < stateTable.size(); i++)
		{
			AlleleState s = stateTable.getAlleleState(i);

			if (s.isHomzEncodedAsHet())
			{
				s = new AlleleState(s.getState(0), "");
				stateTable.getStates().set(i, s);
			}
		}

		optimize(true);
	}

	public void optimize(boolean compareHomzOnly)
	{
		// This lookup table will hold original index (in the state table) value
		// compared against new index (eg 1=1 if no change, or 4=2 if remapped)
		HashMap<Integer,Integer> remap = new HashMap<>();
		for (int i = 0; i < stateTable.size(); i++)
			remap.put(i,i);

		System.out.println("BEFORE");
		for (int key: remap.keySet())
		{
			System.out.println("Remap: " + key + ": " + remap.get(key) + " - " + stateTable.getAlleleState(remap.get(key)));
		}

		ArrayList<Integer> toRemove = new ArrayList<>();

		// Start at index 1, because we never attempt to match the unknown state
		// Compare every allele state with every other allele state...
		for (int i = 1; i < stateTable.size(); i++)
		{
			for (int j = i+1; j < stateTable.size(); j++)
			{
				AlleleState s1 = stateTable.getAlleleState(i);
				AlleleState s2 = stateTable.getAlleleState(j);

				// Comparing A/T against T/A or A against A/T
				if (!compareHomzOnly && s1.matches(s2) == false)
					continue;
				// Comparing A against A (which might happen due to one of them
				// originally appearing in the imported data as A/A
				else if (compareHomzOnly && s1.isSameHomzAs(s2) == false)
					continue;

				// Overwrite the original (higher,duplicate) value with the
				// current index (which is the one we want to use instead)
				remap.put(j, i);

				// Decrement all higher values too
				for (int k = j+1; k < stateTable.size(); k++)
					remap.put(k, k-1);

				// Track which element to remove (sticking it at the start of
				// the list, so higher elements are removed first)
				toRemove.add(0, j);
			}
		}

		// Remove the duplicates from the state table
		for (int index: toRemove)
			stateTable.deleteState(index);

		System.out.println("AFTER");
		for (int key: remap.keySet())
		{
			System.out.println("Remap: " + key + ": " + remap.get(key));
		}

		// And finally update the data arrays to these new values
		if (toRemove.size() > 0)
		{
			for (int i = 0; i < dataSet.countLines(); i++)
			{
				Line line = dataSet.getLineByIndex(i);
				line.collapseStates(remap);
			}
		}
	}
}