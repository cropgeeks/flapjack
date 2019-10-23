// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import java.util.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.undo.*;

public class AnalysisSummaryTableModel extends AbstractTableModel
{
	private PedVerF1sBatchList batchList;

	protected String[] columnNames, ttNames;

	AnalysisSummaryTableModel(PedVerF1sBatchList batchList)
	{
		this.batchList = batchList;
	}

	@Override
	public String getColumnName(int col)
//		{ return columnNames[col]; }
		{ return "" + col; }

	public String getToolTip(int col)
	{
		// Return the tooltip text (if it was defined) and if not, just use the
		// standard column name instead
		return ttNames != null && ttNames[col] != null ? ttNames[col] : columnNames[col];
	}

	@Override
	public int getColumnCount()
//		{ return columnNames.length; }
		{ return 5; }


	@Override
	public Object getValueAt(int row, int col)
	{
		PedVerF1sSummary summary = batchList.getSummary(row);

		if (col == 0)
			return summary.getName();

		else
			return new String(row + "," + col);
	}

	@Override
	public final Class getColumnClass(int col)
		{ return String.class; }

	@Override
	public int getRowCount()
	{
		return batchList.size();
	}
}