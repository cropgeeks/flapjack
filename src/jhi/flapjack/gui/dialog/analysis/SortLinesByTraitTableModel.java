// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class SortLinesByTraitTableModel extends AbstractTableModel
{
	private DataSet dataSet;
	private ArrayList<Trait> traits;

	private ArrayList<TableRow> rows;
	private String[] columnNames;

	SortLinesByTraitTableModel(DataSet dataSet)
	{
		this.dataSet = dataSet;
		this.traits = dataSet.getTraits();

		columnNames = new String[] {
			RB.getString("gui.dialog.analysis.NBSortLinesByTraitPanel.col1"),
			RB.getString("gui.dialog.analysis.NBSortLinesByTraitPanel.col2")
		};

		// Initialize the data we'll be showing
		rows = new ArrayList<TableRow>();

		// Pre-fill the first row if we can
		if (traits.size() > 0)
			rows.add(new TableRow());
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	public int getRowCount()
	{
		return rows.size();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		if (col == 0)
		{
			int traitIndex = rows.get(row).traitIndex;
			return traits.get(traitIndex).getName();
		}
		else
			return rows.get(row).isAscending;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return String.class;

		return Boolean.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return true;
	}

	JComboBox getTraitComboBox()
	{
		JComboBox<TraitEntry> combo = new JComboBox<>();

		for (int i = 0; i < traits.size(); i++)
			combo.addItem(new TraitEntry(i, traits.get(i).getName()));

		return combo;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		TableRow tRow = rows.get(row);

		if (col == 0)
			tRow.traitIndex = ((TraitEntry)value).index;
		else
			tRow.isAscending = (Boolean) value;

		fireTableCellUpdated(row, col);
	}

	void addRow()
	{
		rows.add(new TableRow());
		fireTableDataChanged();
	}

	void deleteRow(int row)
	{
		rows.remove(row);
		fireTableDataChanged();
	}

	// Work out the indexes for each selected trait, and return them as an array
	int[] getTraitIndices()
	{
		int[] selected = new int[rows.size()];
		for (int i = 0; i < selected.length; i++)
			selected[i] = rows.get(i).traitIndex;

		return selected;
	}

	// Work out the asc/dec state for each selected trait; return as an array
	public boolean[] getAscendingIndices()
	{
		boolean[] selected = new boolean[rows.size()];
		for (int i = 0; i < selected.length; i++)
			selected[i] = rows.get(i).isAscending;

		return selected;
	}

	// Simple wrapper around a trait so that it can be displayed in the combo
	// box (and we know what index it has when the setValue stuff is called)
	private static class TraitEntry
	{
		int index;
		String traitName;

		TraitEntry(int index, String traitName)
		{
			this.index = index;
			this.traitName = traitName;
		}

		public String toString()
		{
			return traitName;
		}
	}

	// Simple wrapper around each data row within the table. We want to know the
	// index of the trait to be sorted by, and if the sort is ascending or not.
	private static class TableRow
	{
		int traitIndex;
		boolean isAscending;
	}
}