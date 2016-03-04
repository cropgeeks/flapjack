// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

public class CollapseHeterozygotes
{
	private DataSet dataSet;
	private StateTable stateTable;

	public CollapseHeterozygotes(DataSet dataSet)
	{
		this.dataSet = dataSet;

		stateTable = dataSet.getStateTable();
	}

	public void collapse()
	{
		// Start at index 1, because we never attempt to match the unknown state
		// Compare every allele state with every other allele state...
		for (int i = 1; i < stateTable.size(); i++)
		{
			for (int j = i+1; j < stateTable.size(); j++)
			{
				AlleleState s1 = stateTable.getAlleleState(i);
				AlleleState s2 = stateTable.getAlleleState(j);

				if (s1.matches(s2) == false)
					continue;

				// Update the array, rewriting all the duplicates
				System.out.println("Matched on " + s1 + " and " + s2);
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