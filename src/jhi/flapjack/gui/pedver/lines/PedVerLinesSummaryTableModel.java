// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver.lines;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

public class PedVerLinesSummaryTableModel extends SummaryTableModel
{
	private PedVerLinesBatchList batchList;

	public PedVerLinesSummaryTableModel(PedVerLinesBatchList batchList)
	{
		this.batchList = batchList;

		columnNames = new String[] { "Analysis", "Parent 1", "Parent 2",
			"Family Size", "Proportion Selected" };
	}

	PedVerLinesBatchList getBatchList()
		{ return batchList; }

	@Override
	public Object getValueAt(int row, int col)
	{
		PedVerLinesSummary summary = batchList.getSummary(row);

		switch (col)
		{
			case 0:  return summary.name();
			case 1:  return summary.parent1();
			case 2:  return summary.parent2();

			case 3:  return summary.getFamilySize();
			case 4:  return summary.proportionSelected();
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