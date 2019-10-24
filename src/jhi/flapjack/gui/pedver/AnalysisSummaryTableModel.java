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

		columnNames = new String[] { "Analysis", "Parent 1", "Parent 2", "# Lines", "# Selected Lines", "% Parental Het",
			"% F1 Het", "% Het", "% Error Rate", "% Match to F1"};
	}

	@Override
	public String getColumnName(int col)
		{ return columnNames[col]; }

	public String getToolTip(int col)
	{
		// Return the tooltip text (if it was defined) and if not, just use the
		// standard column name instead
		return ttNames != null && ttNames[col] != null ? ttNames[col] : columnNames[col];
	}

	@Override
	public int getColumnCount()
		{ return columnNames.length; }

	@Override
	public Object getValueAt(int row, int col)
	{
		PedVerF1sSummary summary = batchList.getSummary(row);

		switch (col)
		{
			case 0: return summary.name();
			case 1: return summary.parent1();
			case 2: return summary.parent2();
			case 3: return summary.lineCount();
			case 4: return summary.selectedCount();
			case 5: return summary.thresholds().getParentHetThreshold();
			case 6: return summary.thresholds().getF1isHetThreshold();
			case 7: return summary.thresholds().getHetThreshold();
			case 8: return summary.thresholds().getErrorThreshold();
			case 9: return summary.thresholds().getF1Threshold();
		}

		return null;
	}

	@Override
	public final Class getColumnClass(int col)
	{
		if (col < 3)
			return String.class;
		else
			return Integer.class;
	}

	@Override
	public int getRowCount()
	{
		return batchList.size();
	}
}