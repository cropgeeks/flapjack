// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
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
			"Family Size", "Proportion Selected",
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

			case 5:  return summary.percentDecisionTrueF1s();
//			case x:  return summary.percentDecisionUndecidedHybrid();
//			case x:  return summary.percentDecisionLikeP1();
//			case x: return summary.percentDecisionLikeP2();
//			case x: return summary.percentDecisionUndecidedInbred();
//			case x: return summary.percentDecisionNoDecision();

			case 6: return summary.percentDataAvg();
			case 7: return summary.percentHetAvg();
			case 8: return summary.similarityToP1Avg();
			case 9: return summary.similarityToP2Avg();
			case 10: return summary.percentAlleleMatchExpectedAvg();
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