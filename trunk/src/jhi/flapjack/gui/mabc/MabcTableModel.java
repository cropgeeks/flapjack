// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class MabcTableModel extends AbstractTableModel
{
	private DataSet dataSet;
	private ArrayList<MABCLineStats> stats;

	private String[] columnNames;

	private int chrCount, qtlCount;

	// Indices to track what goes where
	private int rppIndex, rppTotalIndex, rppCoverageIndex;
	private int qtlIndex;


	MabcTableModel(DataSet dataSet, ArrayList<MABCLineStats> stats)
	{
		this.dataSet = dataSet;
		this.stats = stats;

		initModel();
	}

	void initModel()
	{
		// Use information from the first result to determine the UI
		MABCLineStats s = stats.get(0);
		chrCount = s.getChrScores().size();
		qtlCount = s.getQTLScores().size();

		// Column indices
		rppIndex = 1;
		rppTotalIndex = rppIndex + chrCount;
		rppCoverageIndex = rppTotalIndex + 1;
		qtlIndex = rppCoverageIndex + 1;

		// TODO: UPDATE!
		int colCount = qtlIndex + (qtlCount*2);
		columnNames = new String[colCount];

		// LineInfo column
		columnNames[0] = "Line";

		// For each chromosome's RPP result:
		for (int i = 0; i < s.getChrScores().size(); i++)
		{
			MABCLineStats.ChrScore cs = s.getChrScores().get(i);
			ChromosomeMap map = dataSet.getChromosomeMaps().get(cs.chrMapIndex);
			columnNames[rppIndex+i] = map.getName();
		}

		columnNames[rppTotalIndex] = "RPP Total";
		columnNames[rppCoverageIndex] = "RPP Coverage";

		// QTL section of the table
		int qtl = 0;
		for (MABCLineStats.QTLScore score: s.getQTLScores())
		{
			columnNames[qtlIndex+(qtl*2)] = score.qtl.getQTL().getName() + " LD";
			columnNames[qtlIndex+(qtl*2)+1] = score.qtl.getQTL().getName() + " Status";
			qtl++;
		}
	}

	@Override
	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getRowCount()
	{
		return stats.size();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		// Line name
		if (col == 0)
			return stats.get(row).getLineInfo().name();

		// RPP values
		else if (col >= rppIndex && col < rppTotalIndex)
		{
			return stats.get(row).getChrScores().get(col-rppIndex).sumRP;
		}

		// RPP Total
		else if (col == rppTotalIndex)
			return stats.get(row).getRPPTotal();

		// RPP Coverage
		else if (col == rppCoverageIndex)
			return stats.get(row).getGenomeCoverage();

		// QTL values
		else if (col >= qtlIndex)
		{
			col = col-qtlIndex;
			int qtl = col / 2;

			MABCLineStats.QTLScore score = stats.get(row).getQTLScores().get(qtl);

			if (col % 2 == 0)
				return score.drag;
			else
				return score.status ? 1 : 0;
		}

		return -9;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return String.class;
		else
			return Double.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
//		return getColumnClass(col) == String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
	}
}