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
		// This lookup table will hold remapped references to the AlleleState
		// objests in the state table. As duplicates are found, these references
		// will be updated to point to the first instance in each case.
		ArrayList<AlleleState> remapRef = new ArrayList<>(stateTable.size());
		for (AlleleState as: stateTable.getStates())
			remapRef.add(as);

		ArrayList<Integer> toDelete = new ArrayList<>();

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

				// Update the reference to point to the first instance of this
				// state, then delete the duplicate from the state table
				remapRef.set(j, s1);
				toDelete.add(j);
			}
		}

		Collections.sort(toDelete);
		for (int i = toDelete.size()-1; i >= 0; i--)
			stateTable.deleteState(toDelete.get(i));

		ArrayList<Integer> remap = new ArrayList<>();
		for (AlleleState as: remapRef)
			remap.add(stateTable.getStates().indexOf(as));

		if (toDelete.size() > 0)
		{
			for (int i = 0; i < dataSet.countLines(); i++)
			{
				Line line = dataSet.getLineByIndex(i);
				line.collapseStates(remap);
			}

			Map<String, int[]> favAlleles = dataSet.getFavAlleleManager().getFavAlleles();

			for (int[] allelesForMarker : favAlleles.values())
				for (int i = 0; i < allelesForMarker.length; i++)
					allelesForMarker[i] = remap.get(allelesForMarker[i]);
		}
	}
}