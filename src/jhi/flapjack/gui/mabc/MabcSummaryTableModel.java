// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

public class MabcSummaryTableModel extends SummaryTableModel
{
	private MabcBatchList batchList;

	public MabcSummaryTableModel(MabcBatchList batchList)
	{
		this.batchList = batchList;

		columnNames = new String[] { "Analysis", "RP", "DP",
			"Family Size", "Proportion Selected", "% Data Avg", "RPP Total Avg",
			"QTL Allele Count Avg"};
	}

	public MabcBatchList getBatchList()
		{ return batchList; }

	@Override
	public Object getValueAt(int row, int col)
	{
		MabcSummary summary = batchList.getSummaries().get(row);

		switch (col)
		{
			case 0:  return summary.name();
			case 1:  return summary.rp();
			case 2:  return summary.dp();

			case 3:  return summary.getFamilySize();
			case 4:  return summary.proportionSelected();

			case 5: return summary.percentDataAvg();
			case 6: return summary.rppTotalAvg();
			case 7: return summary.qtlStatusCountAvg();
		}

		return null;
	}

	@Override
	public final Class getColumnClass(int col)
	{
		if (col < 3)
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