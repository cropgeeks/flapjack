// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.forwardbreeding;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

public class FBSummaryTableModel extends SummaryTableModel
{
	private FBBatchList batchList;

	public FBSummaryTableModel(FBBatchList batchList)
	{
		this.batchList = batchList;

		columnNames = new String[] { "Analysis", "Family Size", "Proportion Selected" };
	}

	public FBBatchList getBatchList()
		{ return batchList; }

	@Override
	public Object getValueAt(int row, int col)
	{
		FBSummary summary = batchList.getSummaries().get(row);

		switch (col)
		{
			case 0:  return summary.name();

			case 1:  return summary.getFamilySize();
			case 2:  return summary.proportionSelected();
		}

		return null;
	}

	@Override
	public final Class getColumnClass(int col)
	{
		if (col < 1)
			return String.class;
		else
			return Double.class;
	}

	@Override
	public int getRowCount()
	{
		return batchList.size();
	}
}