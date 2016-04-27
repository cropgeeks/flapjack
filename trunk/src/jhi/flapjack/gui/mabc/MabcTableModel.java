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

	private int chrCount, qtlCount;
	private int qtlColStartIndex;
	private String[] columnNames;

	MabcTableModel(DataSet dataSet, ArrayList<MABCLineStats> stats)
	{
		this.dataSet = dataSet;
		this.stats = stats;

		setColumnNames();
	}

	void setColumnNames()
	{
		// all chromosomes warning??
		chrCount = dataSet.countChromosomeMaps();
		qtlCount = stats.get(0).getQTLScores().size(); // <- fix for better determinatation needed

		columnNames = new String[2 + chrCount + (2*qtlCount)];
		columnNames[0] = "Line";

		int c = 1;
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
			columnNames[c++] = map.getName();

		columnNames[1+chrCount] = "RPP Total";
		qtlColStartIndex = 2+chrCount;

		// QTL section of the table
		int qtlIndex = 0;
		ArrayList<MABCLineStats.QTLScore> scores = stats.get(0).getQTLScores();
		for (MABCLineStats.QTLScore score: scores)
		{
			System.out.println(score.qtl.getQTL().getName());

			columnNames[qtlColStartIndex+qtlIndex] = score.qtl.getQTL().getName();
			columnNames[qtlColStartIndex+qtlIndex+1] = score.qtl.getQTL().getName() + " Status";
			qtlIndex += 2;
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
		if (col == 0)
			return stats.get(row).getLineInfo().name();

		// RPP values
/*		else if (col <= (chrCount+1))
		{
			if (col == (chrCount+1))
				return lineStats.get(row).getRPPTotal();
			else
				return lineStats.get(row).getSumRP().get(col-1);
		}
*/
		// QTL values
		else
		{
//			int offset = qtlColStartIndex % col;

//			ArrayList<MABCLineStats.QTLScore> scores = lineStats.get(row).getQTLScores();
//			MABCLineStats.QTLScore score = scores.get(qtlColStartIndex+col+offset);

//			if (offset == 0)
//				return score.drag;
//			else
//				return score.status;
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
		return getColumnClass(col) == String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
	}
}