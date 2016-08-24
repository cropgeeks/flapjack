// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

class PedVerF1sTableModel extends LineDataTableModel
{
	private PedVerKnownParentsResults results;

	private static final int selectedIndex = 12;
	private static final int commentIndex = 13;

	PedVerF1sTableModel(DataSet dataSet, GTViewSet viewSet)
	{
		this.dataSet = dataSet;

		setLines(new ArrayList<>(viewSet.getLines()));
		initModel();
	}

	void initModel()
	{
		columnNames = new String[] { "Line", "Marker count", "% Missing",
			"Het count", "% Het", "% Deviation from Expected", "Count P1 Contained",
			"% P1 Contained", "Count P2 Contained", "% P2 Contained",
			"Count Allele Match to Expected", "% Allele Match to Expected",
			"Selected", "Comments" };
	}

	@Override
	public int getRowCount()
	{
		return lines.size();
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		LineInfo line = lines.get(row);
		PedVerKnownParentsLineStats stats = line.results().getPedVerStats();

		// Line name and Selected can work without results
		if (col == 0)
			return line;
		else if (col == selectedIndex)
			return line.getSelected();

		if (stats == null)
			return null;

		switch (col)
		{
			case 1: return stats.getMarkerCount();
			case 2: return stats.getPercentMissing();
			case 3: return stats.getHeterozygousCount();
			case 4: return stats.getPercentHeterozygous();
			case 5: return stats.getPercentDeviationFromExpected();
			case 6: return stats.getCountP1Contained();
			case 7: return stats.getPercentP1Contained();
			case 8: return stats.getCountP2Contained();
			case 9: return stats.getPercentP2Contained();
			case 10: return stats.getCountAlleleMatchExpected();
			case 11: return stats.getPercentAlleleMatchExpected();

			case commentIndex:
				String comment = line.results().getComments();
				return comment == null ? "" : comment;

			default: return null;
		}
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;
		else if (col == selectedIndex)
			return Boolean.class;
		else if (col == commentIndex)
			return String.class;
		else
			return Double.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return (col == selectedIndex || col == commentIndex);
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		LineInfo line = (LineInfo) getValueAt(row, 0);

		if (col == selectedIndex)
			line.setSelected((boolean)value);
		else if (col == commentIndex)
			line.results().setComments((String)value);
	}
}