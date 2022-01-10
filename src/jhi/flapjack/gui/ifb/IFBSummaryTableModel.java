// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.ifb;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

public class IFBSummaryTableModel extends SummaryTableModel
{
	// A count of standard (non changing columns)
	private static final int COLCOUNT = 9;

	// Maintains a list of every QTL (across all datasets*) that need to be
	// summarized. *datasets could have different QTL! (urgh)
	private ArrayList<String> qtlNames = new ArrayList<>();
	private IFBBatchList batchList;

	public IFBSummaryTableModel(IFBBatchList batchList)
	{
		this.batchList = batchList;

		LinkedHashMap<String,String> qtlNamesMap = new LinkedHashMap<>();

		// Loop over all summaries
		for (IFBSummary summary: batchList.getSummaries())
		{
			// Extracting the first line's IFBResult object...
			LineInfo firstLine = summary.getLines().get(0);
			IFBResult result = firstLine.getLineResults().getIFBResult();

			// Loop over all QTL objects
			for (IFBQTLScore score: result.getQtlScores())
			{
				String name = score.getQtl().getName();
				qtlNamesMap.put(name, name);
			}
		}

		// Convert to an array list for easier index lookups
		for (String key: qtlNamesMap.keySet())
			qtlNames.add(key);

		columnNames = new String[COLCOUNT + qtlNames.size()];

		// Fixed columns
		columnNames[0] = "Analysis";
		columnNames[1] = "Family Size";
		columnNames[2] = "Proportion Selected";
		columnNames[3] = "Min Weighted MBV Selected";
		columnNames[4] = "Max Weighted MBV Selected";
		columnNames[5] = "Avg Weighted MBV Selected";
		columnNames[6] = "Min Weighted MBV Selected (Non Missing)";
		columnNames[7] = "Max Weighted MBV Selected (Non Missing)";
		columnNames[8] = "Avg Weighted MBV Selected (Non Missing)";

		// Dynamic columns (ie, they change based on the QTL that were analysed)
		int cIndex = COLCOUNT;
		for (String key: qtlNames)
			columnNames[cIndex++] = key + " (FIFA)";
	}

	public IFBBatchList getBatchList()
		{ return batchList; }

	@Override
	public Object getValueAt(int row, int col)
	{
		IFBSummary summary = batchList.getSummaries().get(row);

		switch (col)
		{
			case 0:  return summary.name();

			case 1:  return summary.getFamilySize();
			case 2:  return summary.proportionSelected();

			case 3:  return summary.minWeightedMBVSelected();
			case 4:  return summary.maxWeightedMBVSelected();
			case 5:  return summary.avgWeightedMBVSelected();

			case 6:  return summary.minWeightedMBVSelectedNM();
			case 7:  return summary.maxWeightedMBVSelectedNM();
			case 8:  return summary.avgWeightedMBVSelectedNM();
		}

		if (col >= COLCOUNT && col < (COLCOUNT + qtlNames.size()))
			return summary.getQTLFreq(qtlNames.get(col - COLCOUNT));

		return null;
	}

	@Override
	public final Class getColumnClass(int col)
	{
		if (col < 1)
			return String.class;
		else
			return Double.class;
	}

	@Override
	public int getRowCount()
	{
		return batchList.size();
	}
}