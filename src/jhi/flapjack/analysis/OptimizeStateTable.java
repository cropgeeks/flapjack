// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

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


				// Update the array, rewriting all the duplicates
				System.out.println("Collapsing " + s1 + " and " + s2);
				collapse(i, j);

				// But also rewrite every value greater than the duplicate
				// position, because if we remove an element, all successive
				// elements need to point to an index one less than before
				for (int k = j; k < stateTable.size()-1; k++)
					collapse(k, k+1);

				stateTable.deleteState(j);

				// Drop j back down one value, so the search continues from the
				// (new) proper position in the state table
				j--;
			}
		}
	}

	private void collapse(int s1, int s2)
	{
		for (int i = 0; i < dataSet.countLines(); i++)
		{
			Line line = dataSet.getLineByIndex(i);
			line.collapseStates(s1, s2);
		}
	}
}