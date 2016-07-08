// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

class PedVerF1sTableModel extends LineDataTableModel
{
	// This results table is *linked* with the given view
	private GTViewSet viewSet;

	private PedVerKnownParentsResults results;

	PedVerF1sTableModel(DataSet dataSet, GTViewSet viewSet)
	{
		this.dataSet = dataSet;
		this.viewSet = viewSet;

		initModel();
	}

	void initModel()
	{
		columnNames = new String[] { "Line", "Selected", "Marker count", "% missing",
			"Het count", "% het", "% deviation from expected", "Count P1 contained",
			"% P1 contained", "Count P2 contained", "% P2 contained",
			"Count allele match to expected", "% allele match to expected" };
	}

	public int getRowCount()
	{
		return viewSet.getLines().size();
	}

	public Object getValueAt(int row, int col)
	{
		LineInfo line = viewSet.getLines().get(row);
		PedVerKnownParentsLineStats stats = line.results().getPedVerStats();

		if (col == 0)
			return line;

		if (stats == null)
			return null;

		else if (col == 1)
			return line.getSelected();

		else if (col == 2)
			return stats.getMarkerCount();
		else if (col == 3)
			return stats.getPercentMissing();
		else if (col == 4)
			return stats.getHeterozygousCount();
		else if (col == 5)
			return stats.getPercentHeterozygous();
		else if (col == 6)
			return stats.getPercentDeviationFromExpected();
		else if (col == 7)
			return stats.getCountP1Contained();
		else if (col == 8)
			return stats.getPercentP1Contained();
		else if (col == 9)
			return stats.getCountP2Contained();
		else if (col == 10)
			return stats.getPercentP2Contained();
		else if (col == 11)
			return stats.getCountAlleleMatchExpected();
		else if (col == 12)
			return stats.getPercentAlleleMatchExpected();

		return -1;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;
		else if (col == 1)
			return Boolean.class;
		else
			return Double.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return (col == 1);
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		LineInfo line = (LineInfo) getValueAt(row, 0);

		if (col == 1)
			line.setSelected((boolean)value);
	}

	void selectLines(FilterColumn[] data)
	{
		for (int i = 0; i < getRowCount(); i++)
		{
			LineInfo line = (LineInfo) getValueAt(i, 0);
			line.setSelected(false);

			boolean newState = true;
			for (FilterColumn entry: data)
			{
				if (entry.disabled())
					continue;

				Object value = getValueAt(i, entry.colIndex);

				if (entry.matches(value) == false)
					newState = false;

				line.setSelected(newState);
			}
		}

		fireTableDataChanged();
	}
}