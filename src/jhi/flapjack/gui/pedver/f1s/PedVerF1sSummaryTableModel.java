// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver.f1s;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

public class PedVerF1sSummaryTableModel extends SummaryTableModel
{
	private PedVerF1sBatchList batchList;

	public PedVerF1sSummaryTableModel(PedVerF1sBatchList batchList)
	{
		this.batchList = batchList;

		columnNames = new String[] { "Analysis", "Parent 1", "Parent 2",
			"Family Size", "Proportion Selected", "% Het", "% Match to F1",
			"% True F1s", "% Data Avg", "% Het Avg",
			"% Allele Match to P1 Avg", "% Allele Match to P2 Avg",
			"% Genotype Match to Expected F1" };
	}

	public PedVerF1sBatchList getBatchList()
		{ return batchList; }

	@Override
	public Object getValueAt(int row, int col)
	{
		PedVerF1sSummary summary = batchList.getSummaries().get(row);

		switch (col)
		{
			case 0:  return summary.name();
			case 1:  return summary.parent1();
			case 2:  return summary.parent2();

			case 3:  return summary.getFamilySize();
			case 4:  return summary.proportionSelected();

			case 5:  return (double) summary.thresholds().getHetThreshold();
			case 6:  return (double) summary.thresholds().getF1Threshold();

			case 7:  return summary.percentDecisionTrueF1s();
//			case 8:  return summary.percentDecisionUndecidedHybrid();
//			case 9:  return summary.percentDecisionLikeP1();
//			case 10: return summary.percentDecisionLikeP2();
//			case 11: return summary.percentDecisionUndecidedInbred();
//			case 12: return summary.percentDecisionNoDecision();

			case 8: return summary.percentDataAvg();
			case 9: return summary.percentHetAvg();
			case 10: return summary.similarityToP1Avg();
			case 11: return summary.similarityToP2Avg();
			case 12: return summary.percentAlleleMatchExpectedAvg();
		}

		return null;
	}

	@Override
	public final Class getColumnClass(int col)
	{
		if (col < 3)
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