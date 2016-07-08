// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

class PedVerLinesTableModel extends LineDataTableModel
{
	// This results table is *linked* with the given view
	private GTViewSet viewSet;

	PedVerLinesTableModel(DataSet dataSet, GTViewSet viewSet)
	{
		this.dataSet = dataSet;
		this.viewSet = viewSet;

		initModel();
	}

	void initModel()
	{
		PedVerLinesLineStats stats = viewSet.getLines().get(0).results().getPedVerLinesStats();
		columnNames = new String[7 + stats.getChrMatchCount().size()];

		columnNames[0] = "Line";
		columnNames[1] = "Marker count";
		columnNames[2] = "% missing";
		columnNames[3] = "Het count";
		columnNames[4] = "% het";
		columnNames[5] = "Match count";
		columnNames[6] = "% match";
		for (int i=0; i < stats.getChrMatchCount().size(); i++)
			columnNames[i+7] = "Match in " + viewSet.getView(i).getChromosomeMap().getName();
	}

	public int getRowCount()
	{
		return viewSet.getLines().size();
	}

	public Object getValueAt(int row, int col)
	{
		LineInfo line = viewSet.getLines().get(row);
		PedVerLinesLineStats stats = line.results().getPedVerLinesStats();

		if (col == 0)
			return line;

		if (stats == null)
			return null;

		else if (col == 1)
			return stats.getMarkerCount();
		else if (col == 2)
			return stats.getMissingPerc();
		else if (col == 3)
			return stats.getHetCount();
		else if (col == 4)
			return stats.getHetPerc();
		else if (col == 5)
			return stats.getMatchCount();
		else if (col == 6)
			return stats.getMatchPerc();
		else if (col >= 7)
		{
			return stats.getChrMatchCount().get(col-7);
		}

		return -1;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;
		else
			return Double.class;
	}
}