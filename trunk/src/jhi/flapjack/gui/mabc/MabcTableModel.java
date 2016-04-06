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
	private ArrayList<MABCLineStats> lineStats;

	private String[] columnNames;

	MabcTableModel(DataSet dataSet, ArrayList<MABCLineStats> lineStats)
	{
		this.dataSet = dataSet;
		this.lineStats = lineStats;

		setColumnNames();
	}

	void setColumnNames()
	{
		// all chromosomes warning??
		int chrCount = dataSet.countChromosomeMaps();

		columnNames = new String[2 + chrCount];
		columnNames[0] = "Line";

		int c = 1;
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
			columnNames[c++] = map.getName();

		columnNames[columnNames.length-1] = "RPP Total";
	}

	@Override
	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getRowCount()
	{
		return lineStats.size();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		if (col == 0)
			return lineStats.get(row).getLineInfo().name();
		else if (col == (columnNames.length-1))
			return lineStats.get(row).getRPPTotal();
		else
			return lineStats.get(row).getSumRP().get(col-1);
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