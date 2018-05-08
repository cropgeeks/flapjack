// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

class PedVerLinesTableModel extends LineDataTableModel
{
	private static final int lineIndex = 0;
	private static final int dataCountIndex = 1;
	private static final int percDataIndex = 2;
	private static final int hetCountIndex = 3;
	private static final int percHetIndex = 4;
	private static final int parentsIndex = 5;
	private int selectedIndex;
	private int rankIndex;
	private int commentsIndex;
	private int sortIndex;
	private int dataTotalMatchIndex;
	private int totalMatchCountIndex;
	private int totalMatchPercentIndex;

	PedVerLinesTableModel(GTViewSet viewSet)
	{
		this.dataSet = viewSet.getDataSet();

		setLines(viewSet.tableHandler().linesForTable());
		initModel();
	}

	void initModel()
	{
		PedVerLinesResult stats = lines.get(0).getResults().getPedVerLinesResult();
		columnNames = new String[12 + (stats.getParentScores().size() * 3)];

		dataTotalMatchIndex = columnNames.length - 7;
		totalMatchCountIndex = columnNames.length - 6;
		totalMatchPercentIndex = columnNames.length - 5;
		selectedIndex = columnNames.length - 4;
		rankIndex = columnNames.length - 3;
		commentsIndex = columnNames.length - 2;
		sortIndex = columnNames.length - 1;

		columnNames[0] = RB.getString("gui.pedver.PedVerLinesTableModel.line");
		columnNames[1] = RB.getString("gui.pedver.PedVerLinesTableModel.dataCount");
		columnNames[2] = RB.getString("gui.pedver.PedVerLinesTableModel.percData");
		columnNames[3] = RB.getString("gui.pedver.PedVerLinesTableModel.hetCount");
		columnNames[4] = RB.getString("gui.pedver.PedVerLinesTableModel.percHet");

		for (int i=0; i < stats.getParentScores().size(); i++)
		{
			columnNames[parentsIndex + (i * 3)] = RB.format("gui.pedver.PedVerLinesTableModel.dataParentMatch", (i+1));
			columnNames[parentsIndex + (i * 3) + 1] = RB.format("gui.pedver.PedVerLinesTableModel.matchParentCount", (i+1));
			columnNames[parentsIndex + (i * 3) + 2] = RB.format("gui.pedver.PedVerLinesTableModel.matchParentPercent", (i+1));
		}
		columnNames[dataTotalMatchIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.dataTotalMatch");
		columnNames[totalMatchCountIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.totalMatchCount");
		columnNames[totalMatchPercentIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.percTotalMatch");
		columnNames[selectedIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.selected");
		columnNames[rankIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.rank");
		columnNames[commentsIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.comments");
		columnNames[sortIndex] = RB.getString("gui.pedver.PedVerLinesTableModel.sortFilter");
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
		if (col == lineIndex)
			return line;
		else if (col == selectedIndex)
			return line.getSelected();
		else if (col == rankIndex)
			return line.getResults().getRank();
		else if (col == sortIndex)
			return line.getResults().isSortToTop();

		if (stats == null)
			return null;

		if (col == dataCountIndex)
			return stats.getDataCount();
		else if (col == percDataIndex)
			return stats.getPercentData();
		else if (col == hetCountIndex)
			return stats.getHetCount();
		else if (col == percHetIndex)
			return stats.getPercentHet();
			// QTL values
		else if (col >= parentsIndex && col < dataTotalMatchIndex)
		{
			col = col-parentsIndex;
			int parent = col / 3;

			PedVerLinesParentScore score = stats.getParentScores().get(parent);

			if (col % 3 == 0)
				return score.getDataParentMatch();
			else if (col % 3 == 1)
				return score.getMatchParentCount();
			else
				return score.getMatchParentPercent();
		}
		else if (col == dataTotalMatchIndex)
			return stats.getDataTotalMatch();
		else if (col == totalMatchCountIndex)
			return stats.getTotalMatch();
		else if (col == totalMatchPercentIndex)
			return stats.getPercentTotalMatch();
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
			selectLine(line, (boolean)value);
		else if (col == rankIndex)
			line.getResults().setRank((int)value);
		else if (col == commentsIndex)
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