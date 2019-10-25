// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.results.*;

import scri.commons.gui.*;

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

			case 5:  return (double) summary.thresholds().getHetThreshold();
			case 6:  return (double) summary.thresholds().getF1Threshold();

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
		else
			return Double.class;
	}

	@Override
	public int getRowCount()
	{
		return batchList.size();
	}

	static class DoubleNumRenderer extends NumberFormatCellRenderer
	{
		// White
		static Color col1 = new Color(255,255,255);
		// Greenish
		static Color col2 = new Color(188,209,151);
		static int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		static int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

		static Color bgColSel = UIManager.getColor("Table.selectionBackground");
		static Color bgCol = UIManager.getColor("Table.background");

		@Override
		public Component getTableCellRendererComponent(JTable table, Object obj,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, obj, isSelected,
				hasFocus, row, column);

			if (obj instanceof Double)// && column >= 5)
			{
				double value = (double) obj;

				if (value < 0 || value > 100)
					return this;
				value = value / 100d;

				double f1 = 1f - value;
				double f2 = value;

				Color color = new Color(
					(int) (f1 * c1[0] + f2 * c2[0]),
					(int) (f1 * c1[1] + f2 * c2[1]),
					(int) (f1 * c1[2] + f2 * c2[2]));

				setBackground(isSelected ? color.darker() : color);
			}
			else
				setBackground(isSelected ? bgColSel : bgCol);

			return this;
		}
	}
}