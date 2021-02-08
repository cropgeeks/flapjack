// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import javax.swing.table.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class CalculateSimMatrixTableModel extends AbstractTableModel
{
	private GTViewSet viewSet;
	private String[] columnNames;
	private boolean[] selected;

	CalculateSimMatrixTableModel(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		columnNames = new String[] {
			RB.getString("gui.dialog.analysis.CalculateSimMatrixDialog.column1"),
			RB.getString("gui.dialog.analysis.CalculateSimMatrixDialog.column2"),
			RB.getString("gui.dialog.analysis.CalculateSimMatrixDialog.column3")
		};

		selected = new boolean[viewSet.chromosomeCount()];
		for (int i=0; i < selected.length; i++)
			selected[i] = true;
	}

	@Override
	public int getRowCount()
	{
		return viewSet.chromosomeCount();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnNames[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		GTView view = viewSet.getView(rowIndex);

		switch (columnIndex)
		{
			case 0: return selected[rowIndex];
			case 1: return view.getChromosomeMap().getName();
			case 2: return view.countSelectedMarkers() + " / "
				+ view.markerCount();

			default: return null;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		if (columnIndex == 0)
			selected[rowIndex] = (boolean) value;

		fireTableCellUpdated(rowIndex, columnIndex);
	}

	@Override
	public Class getColumnClass(int c)
	{
		return getValueAt(0, c).getClass();
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return col == 0;
	}
}