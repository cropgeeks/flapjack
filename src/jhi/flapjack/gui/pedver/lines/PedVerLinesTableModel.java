// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver.lines;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

import java.util.*;

public class PedVerLinesTableModel extends LineDataTableModel
{
	private static final int lineIndex = 0;
	private static final int dataCountIndex = 1;
	private static final int percDataIndex = 2;
	private static final int hetCountIndex = 3;
	private static final int percHetIndex = 4;
	private static final int similarityToP1Index = 5;
	private static final int similarityToP2Index = 6;
	private static final int similarlityToParentsIndex = 7;
	private static final int rankIndex = 8;
	private static final int commentsIndex = 9;
	private static final int sortIndex = 10;
	private static final int selectedIndex = 11;
	private static final int decisionIndex = 12;

	PedVerLinesTableModel(GTViewSet viewSet)
	{
		this.dataSet = viewSet.getDataSet();

		setLines(viewSet.tableHandler().linesForTable());
		initModel();
	}

	void initModel()
	{
		columnNames = new String[13];

		columnNames[lineIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.line");
		columnNames[dataCountIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.dataCount");
		columnNames[percDataIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.percData");
		columnNames[hetCountIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.hetCount");
		columnNames[percHetIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.percHet");
		columnNames[similarityToP1Index] = RB.getString("gui.pedver.PedVerLinesTableModel.similarityToP1");
		columnNames[similarityToP2Index] = RB.getString("gui.pedver.PedVerLinesTableModel.similarityToP2");
		columnNames[similarlityToParentsIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.similarityToParents");
		columnNames[rankIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.rank");
		columnNames[commentsIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.comments");
		columnNames[sortIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.sortFilter");
		columnNames[selectedIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.selected");
		columnNames[decisionIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.decision");
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
		PedVerLinesResult stats = line.getLineResults().getPedVerLinesResult();

		// Name, Selected and Sort can work without results
		if (col == lineIndex)
			return line;
		else if (col == selectedIndex)
			return line.getSelected();
		else if (col == rankIndex)
			return line.getLineResults().getRank();
		else if (col == sortIndex)
			return line.getLineResults().isSortToTop();
		if (col == dataCountIndex)
			return stats.getDataCount();
		else if (col == percDataIndex)
			return stats.getPercentData();
		else if (col == hetCountIndex)
			return stats.getHetCount();
		else if (col == percHetIndex)
			return stats.getPercentHet();
		else if (col == similarityToP1Index)
			return stats.getSimilarityToP1();
		else if (col == similarityToP2Index)
			return stats.getSimilarityToP2();
		else if (col == similarlityToParentsIndex)
			return stats.getSimilarityToParents();
		else if (col == commentsIndex)
		{
			String comment = line.getLineResults().getComments();
			return comment == null ? "" : comment;
		}
		else if (col == decisionIndex)
		{
			return stats.calculateDecisionString();
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
			selectLine(line, (boolean)value);
		else if (col == rankIndex)
			line.getLineResults().setRank((int)value);
		else if (col == commentsIndex)
			line.getLineResults().setComments((String)value);
		else if (col == sortIndex)
			line.getLineResults().setSortToTop((boolean)value);

		fireTableRowsUpdated(row, row);

		Actions.projectModified();
	}

	int getRankIndex()
	{
		return rankIndex;
	}

	// Returns only those columns that make sense for filtering (by numbers)
	public FilterColumn[] getFilterableColumns()
	{
		ArrayList<FilterColumn> cols = new ArrayList<>();

		for (int i = 0; i < getColumnCount(); i++)
		{
			Class c = getObjectColumnClass(i);

			if (i == decisionIndex)
				cols.add(new FilterColumn(i, c, columnNames[i], FilterColumn.NONE));

			else if (c == Double.class || c == Float.class || c == Integer.class || c == Boolean.class)
				cols.add(new FilterColumn(i, c, columnNames[i], FilterColumn.NONE));
		}

		return cols.toArray(new FilterColumn[] {});
	}


	// Returns only those columns that make sense for filtering (by numbers)
	public FilterColumn[] getFilterColsVerifiedLinesSelected()
	{
		ArrayList<FilterColumn> cols = new ArrayList<>();

		for (int i = 0; i < getColumnCount(); i++)
		{
			Class c = getObjectColumnClass(i);

			if (i == decisionIndex)
				cols.add(new PedVerLinesFilterColumn(i, c, columnNames[i], PedVerLinesFilterColumn.VERIFIED));

			else if (c == Double.class || c == Float.class || c == Integer.class || c == Boolean.class)
				cols.add(new FilterColumn(i, c, columnNames[i], FilterColumn.NONE));
		}

		return cols.toArray(new FilterColumn[] {});
	}
}