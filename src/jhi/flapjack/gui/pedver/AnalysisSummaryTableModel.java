// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import javax.swing.table.*;

import jhi.flapjack.data.results.*;

public class AnalysisSummaryTableModel extends AbstractTableModel
{
	private PedVerF1sBatchList batchList;

	protected String[] columnNames, ttNames;

	AnalysisSummaryTableModel(PedVerF1sBatchList batchList)
	{
		this.batchList = batchList;

		columnNames = new String[] { "Analysis", "Parent 1", "Parent 2",
			"Family Size", "Proportion Selected", "% Het", "% Match to F1",
			"% True F1s", "% Undecided Hybrid", "% Like P1", "% Like P2",
			"% Undecided Inbred", "No Decision",
			"% Data Avg", "% Het Avg", "% Allele Match to P1 Avg", "% Allele Match to P2 Avg",
			"% Genotype Match to Expected F1" };
	}

	PedVerF1sBatchList getBatchList()
		{ return batchList; }

	@Override
	public String getColumnName(int col)
		{ return columnNames[col]; }

	public String getToolTip(int col)
	{
		// Return the tooltip text (if it was defined) and if not, just use the
		// standard column name instead
		return ttNames != null && ttNames[col] != null ? ttNames[col] : columnNames[col];
	}

	@Override
	public int getColumnCount()
		{ return columnNames.length; }

	@Override
	public Object getValueAt(int row, int col)
	{
		PedVerF1sSummary summary = batchList.getSummary(row);

		switch (col)
		{
			case 0:  return summary.name();
			case 1:  return summary.parent1();
			case 2:  return summary.parent2();
			case 3:  return summary.getFamilySize();
			case 4:  return summary.proportionSelected();

			case 5:  return summary.thresholds().getHetThreshold();
			case 6:  return summary.thresholds().getF1Threshold();

			case 7:  return summary.percentDecisionTrueF1s();
			case 8:  return summary.percentDecisionUndecidedHybrid();
			case 9:  return summary.percentDecisionLikeP1();
			case 10: return summary.percentDecisionLikeP2();
			case 11: return summary.percentDecisionUndecidedInbred();
			case 12: return summary.percentDecisionNoDecision();

			case 13: return summary.percentDataAvg();
			case 14: return summary.percentHetAvg();
			case 15: return summary.similarityToP1Avg();
			case 16: return summary.similarityToP2Avg();
			case 17: return summary.percentAlleleMatchExpectedAvg();
		}

		return null;
	}

	@Override
	public final Class getColumnClass(int col)
	{
		if (col < 3)
			return String.class;
		else if (col == 3)
			return Integer.class;
		else if (col == 4)
			return Double.class;
		else
			return Integer.class;
	}

	@Override
	public int getRowCount()
	{
		return batchList.size();
	}
}