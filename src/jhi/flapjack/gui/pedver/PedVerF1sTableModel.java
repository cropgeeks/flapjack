// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

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
	private static final int countAlleleMatchIndex = 8;
	private static final int percAlleleMatchIndex = 9;
	private static final int selectedIndex = 10;
	private static final int rankIndex = 11;
	private static final int commentIndex = 12;
	private static final int sortIndex = 13;

	public PedVerF1sTableModel(GTViewSet viewSet)
	{
		this.dataSet = viewSet.getDataSet();

		setLines(viewSet.tableHandler().linesForTable());
		initModel();
	}

	private void initModel()
	{
		columnNames = new String[16];
		columnNames[lineIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.line");
		columnNames[dataCountIndex]	= RB.getString("gui.pedver.PedVerF1sTableModel.dataCount");
		columnNames[percDataIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percData");
		columnNames[hetCountIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.hetCount");
		columnNames[percHetIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percHet");
		columnNames[percDevExpectedIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percDevExpected");
		columnNames[similarityToP1Index] = RB.getString("gui.pedver.PedVerF1sTableModel.similarityToP1");
		columnNames[similarityToP2Index] = RB.getString("gui.pedver.PedVerF1sTableModel.similarityToP2");
		columnNames[countAlleleMatchIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.countAlleleMatch");
		columnNames[percAlleleMatchIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.percAlleleMatch");
		columnNames[selectedIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.selected");
		columnNames[rankIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.rank");
		columnNames[commentIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.comments");
		columnNames[sortIndex] = RB.getString("gui.pedver.PedVerF1sTableModel.sortFilter");

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
		PedVerF1sResult stats = line.getResults().getPedVerF1sResult();

		// Name, Selected and Sort can work without results
		if (col == lineIndex)
			return line;
		else if (col == selectedIndex)
			return line.getSelected();
		else if (col == sortIndex)
			return line.getResults().isSortToTop();
		else if (col == rankIndex)
			return line.getResults().getRank();

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
			case countAlleleMatchIndex: return stats.getCountAlleleMatchExpected();
			case percAlleleMatchIndex: return stats.getPercentAlleleMatchExpected();

			case commentIndex:
				String comment = line.getResults().getComments();
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
			line.getResults().setRank((int)value);

		else if (col == commentIndex)
			line.getResults().setComments((String)value);

		else if (col == sortIndex)
			line.getResults().setSortToTop((boolean)value);

		fireTableRowsUpdated(row, row);

		Actions.projectModified();
	}

	int getRankIndex()
	{
		return rankIndex;
	}
}