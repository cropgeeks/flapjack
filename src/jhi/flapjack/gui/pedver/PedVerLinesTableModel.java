// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

class PedVerLinesTableModel extends LineDataTableModel
{
	private GTViewSet viewSet;

	private int commentsIndex;

	PedVerLinesTableModel(DataSet dataSet, GTViewSet viewSet)
	{
		this.dataSet = dataSet;
		this.viewSet = viewSet;
		
		setLines(new ArrayList<>(viewSet.getLines()));
		initModel();
	}

	void initModel()
	{
		PedVerLinesLineStats stats = lines.get(0).results().getPedVerLinesStats();
		columnNames = new String[9 + stats.getChrMatchCount().size()];
		commentsIndex = columnNames.length-1;

		columnNames[0] = "Line";
		columnNames[1] = "Selected";
		columnNames[2] = "Marker count";
		columnNames[3] = "% missing";
		columnNames[4] = "Het count";
		columnNames[5] = "% het";
		columnNames[6] = "Match count";
		columnNames[7] = "% match";
		for (int i=0; i < stats.getChrMatchCount().size(); i++)
			columnNames[i+8] = "Match in " + viewSet.getView(i).getChromosomeMap().getName();
		columnNames[commentsIndex] = "Comments";
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
		PedVerLinesLineStats stats = line.results().getPedVerLinesStats();

		if (col == 0)
			return line;

		if (stats == null)
			return null;

		else if (col == 1)
			return line.getSelected();
		else if (col == 2)
			return stats.getMarkerCount();
		else if (col == 3)
			return stats.getMissingPerc();
		else if (col == 4)
			return stats.getHetCount();
		else if (col == 5)
			return stats.getHetPerc();
		else if (col == 6)
			return stats.getMatchCount();
		else if (col == 7)
			return stats.getMatchPerc();
		else if (col >= 8 && col < commentsIndex)
		{
			return stats.getChrMatchCount().get(col-8);
		}
		else if (col == commentsIndex)
		{
			String comment = line.results().getComments();
			return comment == null ? "" : comment;
		}

		return null;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;
		else if (col == 1)
			return Boolean.class;
		else if (col == commentsIndex)
			return String.class;
		else
			return Double.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return (col == 1 || col == commentsIndex);
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		LineInfo line = (LineInfo) getValueAt(row, 0);

		if (col == 1)
			line.setSelected((boolean)value);
		else if (col == commentsIndex)
			line.results().setComments((String)value);
	}
}