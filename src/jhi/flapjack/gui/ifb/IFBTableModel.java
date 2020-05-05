// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.ifb;

import java.util.*;
import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class IFBTableModel extends LineDataTableModel
{
	private int qtlCount;
	private int selectedIndex, rankIndex, commentIndex, sortIndex;
	private int qtlIndex, mbvIndex, wmbvIndex;

	public IFBTableModel(GTViewSet viewSet)
	{
		this.dataSet = viewSet.getDataSet();

		setLines(viewSet.tableHandler().linesForTable());
		initModel();
	}

	void initModel()
	{
		LineInfo line = lines.get(0);
		IFBResult results = line.getLineResults().getIFBResult();
		ArrayList<IFBQTLScore> qtlScores = results.getQtlScores();

		qtlCount = qtlScores.size();

		qtlIndex = 1;
		mbvIndex = qtlIndex + qtlCount;
		wmbvIndex = mbvIndex + 1;

		selectedIndex = wmbvIndex + 1;
		rankIndex = selectedIndex + 1;
		commentIndex = rankIndex + 1;
		sortIndex = commentIndex + 1;

		int colCount = sortIndex + 1;
		columnNames = new String[colCount];
		ttNames = new String[colCount];

		// LineInfo column
		columnNames[0] = RB.getString("gui.ifb.IFBTableModel.line");

		// QTL (display columns)
		for (int i = qtlIndex; i < qtlIndex+qtlCount; i++)
			columnNames[i] = qtlScores.get(i-qtlIndex).getQtl().getName();

		// MBV/wMBV
		columnNames[mbvIndex] = "Molecular Breeding Value";
		columnNames[wmbvIndex] = "Weighted Molecular Breeding Value";

		columnNames[selectedIndex] = RB.getString("gui.ifb.IFBTableModel.selected");
		columnNames[rankIndex] = RB.getString("gui.ifb.IFBTableModel.rank");
		columnNames[commentIndex] = RB.getString("gui.ifb.IFBTableModel.comments");
		columnNames[sortIndex] = RB.getString("gui.ifb.IFBTableModel.sortFilter");

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
		IFBResult stats = line.getLineResults().getIFBResult();

		// Name, Selected and Sort can work without results
		if (col == 0)
			return line;
		else if (col == selectedIndex)
			return line.getSelected();
		else if (col == rankIndex)
			return line.getLineResults().getRank();
		else if (col == commentIndex)
			return line.getLineResults().getComments();
		else if (col == sortIndex)
			return line.getLineResults().isSortToTop();

		else if (col >= qtlIndex && col < qtlIndex+qtlCount)
			return stats.getQtlScores().get(col-qtlIndex).qtlGenotype();

		else if (col == mbvIndex)
			return stats.getMbvTotal();
		else if (col == wmbvIndex)
			return stats.getWmbvTotal();

		// For everything else, don't show entries if stats object null
		if (stats == null)
			return null;

		return null;
	}

	@Override
	public Class getObjectColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;
		else if (col == commentIndex)
			return String.class;
		else if (col == selectedIndex || col == sortIndex)
			return Boolean.class;
		else if (col == rankIndex)
			return Integer.class;

		else if (col >= qtlIndex && col < qtlIndex+qtlCount)
			return String.class;

		else
			return Double.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return (col == selectedIndex ||
				col == rankIndex ||
				col == commentIndex ||
				col == sortIndex);
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
}