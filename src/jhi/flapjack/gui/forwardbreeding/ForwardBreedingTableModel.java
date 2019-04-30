// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.forwardbreeding;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class ForwardBreedingTableModel extends LineDataTableModel
{
	private int selectedIndex, rankIndex, commentIndex, sortIndex;
	private int partialMatchIndex;
	private int hapAlleleCountIndex;
//	private int hapMatchIndex;
//	private int averageHapMatchIndex;
	private int hapWeightIndex;
	private int averageWeightedHapMatchIndex;

	private int dataCountIndex;
	private int percData;
	private int hetCountIndex;
	private int hetPercIndex;

	public ForwardBreedingTableModel(GTViewSet viewSet)
	{
		this.dataSet = viewSet.getDataSet();

		setLines(viewSet.tableHandler().linesForTable());
		initModel();
	}

	void initModel()
	{
		LineInfo line = lines.get(0);
		ForwardBreedingResult results = line.getResults().getForwardBreedingResult();
		int partialMatchSize = results.getHaplotypePartialMatch().size();
//		int hapAlleleCountSize = results.getHaplotypeAlleleCounts().size();
		int hapMatchSize = results.getHaplotypeMatch().size();
		int hapWeightSize = results.getHaplotypeWeight().size();

		dataCountIndex = 1;
		percData = dataCountIndex + 1;
		hetCountIndex = percData + 1;
		hetPercIndex = hetCountIndex + 1;
		partialMatchIndex = hetPercIndex + 1;
//		hapAlleleCountIndex = partialMatchIndex + partialMatchSize;
//		hapMatchIndex = partialMatchIndex + partialMatchSize;
//		averageHapMatchIndex = hapMatchIndex + hapMatchSize;
		hapWeightIndex = partialMatchIndex + partialMatchSize;
		averageWeightedHapMatchIndex = hapWeightIndex + hapWeightSize;
		selectedIndex = averageWeightedHapMatchIndex + 1;
		rankIndex = selectedIndex + 1;
		commentIndex = rankIndex + 1;
		sortIndex = commentIndex +1;

		int colCount = sortIndex + 1;
		columnNames = new String[colCount];
		ttNames = new String[colCount];

		// LineInfo column
		columnNames[0] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.line");
		columnNames[dataCountIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.dataCount");
		columnNames[percData] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.percData");
		columnNames[hetCountIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.hetCount");
		columnNames[hetPercIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.percHet");
		for (int i=0; i < partialMatchSize; i++)
		{
			columnNames[i + partialMatchIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.partialMatch", results.getHaplotypeNames().get(i));
			ttNames[i + partialMatchIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.partialMatch.tt", results.getHaplotypeNames().get(i));
		}
//		for (int i=0; i < hapAlleleCountSize; i++)
//		{
//			columnNames[i + hapAlleleCountIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.alleleCount", results.getHaplotypeNames().get(i));
//			ttNames[i + hapAlleleCountIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.alleleCount.tt", results.getHaplotypeNames().get(i));
//		}
//		for (int i=0; i < hapMatchSize; i++)
//		{
//			columnNames[i + hapMatchIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.match", results.getHaplotypeNames().get(i));
//			ttNames[i + hapMatchIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.match.tt", results.getHaplotypeNames().get(i));
//		}
//		columnNames[averageHapMatchIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.average");
//		ttNames[averageHapMatchIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.average.tt");
		for (int i=0; i < hapWeightSize; i++)
		{
			columnNames[i + hapWeightIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.weighted", results.getHaplotypeNames().get(i));
			ttNames[i + hapWeightIndex] = RB.format("gui.forwardbreeding.ForwardBreedingTableModel.weighted.tt", results.getHaplotypeNames().get(i));
		}
		columnNames[averageWeightedHapMatchIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.weightedAverage");
		ttNames[averageWeightedHapMatchIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.weightedAverage.tt");
		columnNames[selectedIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.selected");
		columnNames[rankIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.rank");
		columnNames[commentIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.comments");
		columnNames[sortIndex] = RB.getString("gui.forwardbreeding.ForwardBreedingTableModel.sortFilter");

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
		ForwardBreedingResult stats = line.getResults().getForwardBreedingResult();

		// Name, Selected and Sort can work without results
		if (col == 0)
			return line;
		else if (col == dataCountIndex)
			return stats.getDataCount();
		else if (col == percData)
			return stats.getPercentData();
		else if (col == hetCountIndex)
			return stats.getHeterozygousCount();
		else if (col == hetPercIndex)
			return stats.getPercentHeterozygous();
//		else if (col >= partialMatchIndex && col < hapAlleleCountIndex)
		else if (col >= partialMatchIndex && col < hapWeightIndex)
			return stats.getHaplotypePartialMatch().get(col - partialMatchIndex);
//		else if (col >= hapAlleleCountIndex && col < hapMatchIndex)
//			return stats.getHaplotypeAlleleCounts().get(col - hapAlleleCountIndex);
//		else if (col >= hapMatchIndex && col < averageHapMatchIndex)
//			return stats.getHaplotypeMatch().get(col - hapMatchIndex);
//		else if (col >= averageHapMatchIndex && col < hapWeightIndex)
//			return stats.getAverageHapMatch();
		else if (col >= hapWeightIndex && col < averageWeightedHapMatchIndex)
			return stats.getHaplotypeWeight().get(col - hapWeightIndex);
		else if (col == averageWeightedHapMatchIndex)
			return stats.getAverageWeightedHapMatch();
		else if (col == selectedIndex)
			return line.getSelected();
		else if (col == rankIndex)
			return line.getResults().getRank();
		else if (col == commentIndex)
			return line.getResults().getComments();
		else if (col == sortIndex)
			return line.getResults().isSortToTop();

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