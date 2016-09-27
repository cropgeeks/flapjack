// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

class PedVerLinesTableModel extends LineDataTableModel
{
	private GTViewSet viewSet;

	private int selectedIndex;
	private int commentsIndex;
	private int sortIndex;

	PedVerLinesTableModel(DataSet dataSet, GTViewSet viewSet)
	{
		this.dataSet = dataSet;
		this.viewSet = viewSet;

		setLines(viewSet.tableHandler().linesForTable());
		initModel();
	}

	void initModel()
	{
		PedVerLinesResult stats = lines.get(0).getResults().getPedVerLinesResult();
		columnNames = new String[10 + stats.getChrMatchCount().size()];
		selectedIndex = columnNames.length-3;
		commentsIndex = columnNames.length-2;
		sortIndex = columnNames.length-1;

		columnNames[0] = "Line";
		columnNames[1] = "Marker count";
		columnNames[2] = "% Missing";
		columnNames[3] = "Het Count";
		columnNames[4] = "% Het";
		columnNames[5] = "Match Count";
		columnNames[6] = "% Match";
		for (int i=0; i < stats.getChrMatchCount().size(); i++)
			columnNames[i+7] = "Match in " + viewSet.getView(i).getChromosomeMap().getName();
		columnNames[selectedIndex] = "Selected";
		columnNames[commentsIndex] = "Comments";
		columnNames[sortIndex] = "Don't Sort/Filter";
	}

	@Override
	public boolean skipExport(int col)
	{
		// We don't want to export the don't sort column
		return col == sortIndex;
	}

	@Override
	public int getRowCount()
	{
		return lines.size();
	}

	@Override
	public Object getObjectAt(int row, int col)
	{
		LineInfo line = lines.get(row);
		PedVerLinesResult stats = line.getResults().getPedVerLinesResult();

		// Name, Selected and Sort can work without results
		if (col == 0)
			return line;
		else if (col == selectedIndex)
			return line.getSelected();
		else if (col == sortIndex)
			return line.getResults().isSortToTop();

		if (stats == null)
			return null;

		if (col == 1)
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
		else if (col >= 7 && col < commentsIndex)
		{
			return stats.getChrMatchCount().get(col-7);
		}
		else if (col == commentsIndex)
		{
			String comment = line.getResults().getComments();
			return comment == null ? "" : comment;
		}

		return null;
	}

	@Override
	public Class getObjectColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;
		else if (col == selectedIndex || col == sortIndex)
			return Boolean.class;
		else if (col == commentsIndex)
			return String.class;
		else
			return Double.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return (col == selectedIndex || col == commentsIndex || col == sortIndex);
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		LineInfo line = (LineInfo) getObjectAt(row, 0);

		if (col == selectedIndex)
			line.setSelected((boolean)value);
		else if (col == commentsIndex)
			line.getResults().setComments((String)value);
		else if (col == sortIndex)
			line.getResults().setSortToTop((boolean)value);

		fireTableRowsUpdated(row, row);

		Actions.projectModified();
	}
}