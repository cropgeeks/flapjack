// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver.f1s;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

import java.awt.*;
import java.util.*;

public class PedVerF1sTableModel extends LineDataTableModel
{
	private static final int lineIndex = 0;
	private static final int dataCountIndex = 1;
	private static final int percDataIndex = 2;
	private static final int hetCountIndex = 3;
	private static final int percHetIndex = 4;
	private static final int percDevExpectedIndex = 5;
	private static final int similarityToP1Index = 6;
	private static final int similarityToP2Index = 7;
	private static final int percAlleleMatchIndex = 8;
	private static final int rankIndex = 9;
	private static final int commentIndex = 10;
	private static final int sortIndex = 11;
	private static final int selectedIndex = 12;
	private static final int decisionIndex = 13;

	private GTViewSet viewSet;

	public PedVerF1sTableModel(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		this.dataSet = viewSet.getDataSet();

		setLines(viewSet.tableHandler().linesForTable());
		initModel();
	}

	private void initModel()
	{
		columnNames = new String[14];
		columnNames[lineIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.line");
		columnNames[dataCountIndex]	= RB.getString("gui.pedver.PedVerF1sTableModel.dataCount");
		columnNames[percDataIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percData");
		columnNames[hetCountIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.hetCount");
		columnNames[percHetIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percHet");
		columnNames[percDevExpectedIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percDevExpected");
		columnNames[similarityToP1Index] = RB.getString("gui.pedver.PedVerF1sTableModel.similarityToP1");
		columnNames[similarityToP2Index] = RB.getString("gui.pedver.PedVerF1sTableModel.similarityToP2");
		columnNames[percAlleleMatchIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percAlleleMatch");
		columnNames[selectedIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.selected");
		columnNames[rankIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.rank");
		columnNames[commentIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.comments");
		columnNames[sortIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.sortFilter");
		columnNames[decisionIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.decision");

		ttNames = new String[columnNames.length];
		ttNames[percDataIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percData.tt");
		ttNames[hetCountIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.hetCount.tt");
		ttNames[percHetIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percHet.tt");
		ttNames[percDevExpectedIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percDevExpected.tt");
		ttNames[percAlleleMatchIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percAlleleMatch.tt");


		for (int i = 0; i < columnNames.length; i++)
			if (ttNames[i] == null)
				ttNames[i] = columnNames[i];
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
		PedVerF1sResult stats = line.getLineResults().getPedVerF1sResult();
		PedVerDecisions decisionMethod = viewSet.getPedVerF1sBatchList().getDecisionMethod();;

		// Name, Selected and Sort can work without results
		if (col == lineIndex)
			return line;
		else if (col == selectedIndex)
			return line.getSelected();
		else if (col == sortIndex)
			return line.getLineResults().isSortToTop();
		else if (col == rankIndex)
			return line.getLineResults().getRank();

		if (stats == null)
			return null;

		switch (col)
		{
			case dataCountIndex: return stats.getDataCount();
			case percDataIndex: return stats.getPercentData();
			case hetCountIndex: return stats.getHeterozygousCount();
			case percHetIndex: return stats.getPercentHeterozygous();
			case percDevExpectedIndex: return stats.getPercentDeviationFromExpected();
			case similarityToP1Index: return stats.getSimilarityToP1();
			case similarityToP2Index: return stats.getSimilarityToP2();
			case percAlleleMatchIndex: return stats.getPercentAlleleMatchExpected();
			case decisionIndex: return decisionMethod.getDecisionString(stats);

			case commentIndex:
				String comment = line.getLineResults().getComments();
				return comment == null ? "" : comment;

			default: return null;
		}
	}

	@Override
	public Class getObjectColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;
		else if (col == selectedIndex || col == sortIndex)
			return Boolean.class;
		else if (col == commentIndex)
			return String.class;
		else
			return Double.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return (col == selectedIndex || col == commentIndex || col == sortIndex);
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		LineInfo line = (LineInfo) getObjectAt(row, 0);

		if (col == selectedIndex)
			selectLine(line, (boolean)value);

		else if (col == rankIndex)
			line.getLineResults().setRank((int)value);

		else if (col == commentIndex)
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

	public static int getPercHetIndex()
		{ return percHetIndex; }

	public static int getPercAlleleMatchIndex()
		{ return percAlleleMatchIndex; }

	@Override
	public Color getDisplayColor(int row, int col)
	{
		PedVerDecisions decisionMethod = viewSet.getPedVerF1sBatchList().getDecisionMethod();
		if (col == 13)
		{
			LineInfo info = lines.get(row);
			PedVerF1sResult result = info.getLineResults().getPedVerF1sResult();
			if (result.isP1() || result.isP2() || result.isF1())
				return super.getDisplayColor(row, col);

			return decisionMethod.getDecisionColor(result);
		}

		return null;
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
	public FilterColumn[] getFilterColsTrueF1sSelected()
	{
		ArrayList<FilterColumn> cols = new ArrayList<>();

		for (int i = 0; i < getColumnCount(); i++)
		{
			Class c = getObjectColumnClass(i);

			if (i == decisionIndex)
				cols.add(new PedVerF1sFilterColumn(i, c, columnNames[i], PedVerF1sFilterColumn.TRUE_F1));

			else if (c == Double.class || c == Float.class || c == Integer.class || c == Boolean.class)
				cols.add(new FilterColumn(i, c, columnNames[i], FilterColumn.NONE));
		}

		return cols.toArray(new FilterColumn[] {});
	}
}