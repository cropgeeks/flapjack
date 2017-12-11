// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
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
	private int rankIndex;
	private int commentsIndex;
	private int sortIndex;
	private int parentsIndex;
	private int dataTotalMatchIndex;
	private int totalMatchCountIndex;
	private int totalMatchPercentIndex;

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
		columnNames = new String[14 + (stats.getParentScores().size() * 3)];

		parentsIndex = 7;

		dataTotalMatchIndex = columnNames.length - 7;
		totalMatchCountIndex = columnNames.length - 6;
		totalMatchPercentIndex = columnNames.length - 5;
		selectedIndex = columnNames.length - 4;
		rankIndex = columnNames.length - 3;
		commentsIndex = columnNames.length - 2;
		sortIndex = columnNames.length - 1;

		columnNames[0] = "Line";
		columnNames[1] = "Data Count";
		columnNames[2] = "Missing Count";
		columnNames[3] = "Marker count";
		columnNames[4] = "% Missing";
		columnNames[5] = "Het Count";
		columnNames[6] = "% Het";

		for (int i=0; i < stats.getParentScores().size(); i++)
		{
			columnNames[parentsIndex + (i * 3)] = "Data Parent " + (i+1) + " Match";
			columnNames[parentsIndex + (i * 3) + 1] = "Match Parent " + (i+1) + " Count";
			columnNames[parentsIndex + (i * 3) + 2] = "Match Parent " + (i+1) + " Percent";
		}
		columnNames[dataTotalMatchIndex] = "Data Total Match";
		columnNames[totalMatchCountIndex] = "Total Match";
		columnNames[totalMatchPercentIndex] = "Percent Total Match";
		columnNames[selectedIndex] = "Selected";
		columnNames[rankIndex] = "Rank";
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
		else if (col == rankIndex)
			return line.getResults().getRank();
		else if (col == sortIndex)
			return line.getResults().isSortToTop();

		if (stats == null)
			return null;

		if (col == 1)
			return stats.getDataCount();
		else if (col == 2)
			return stats.getMissingCount();
		else if (col == 3)
			return stats.getMarkerCount();
		else if (col == 4)
			return stats.getPercentMissing();
		else if (col == 5)
			return stats.getHetCount();
		else if (col == 6)
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