// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

class MabcTableModel extends LineDataTableModel
{
	// This results table is *linked* with the given view
	private GTViewSet viewSet;

	private int chrCount, qtlCount;

	// Indices to track what goes where
	private int rppIndex, rppTotalIndex, rppCoverageIndex;
	private int qtlIndex;

	MabcTableModel(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		this.dataSet = viewSet.getDataSet();

		initModel();
	}

	void initModel()
	{
		// Use information from the first result to determine the UI
		LineInfo line = viewSet.getLines().get(0);
		MABCLineStats s = line.results().getMABCLineStats();
		chrCount = s.getChrScores().size();
		qtlCount = s.getQTLScores().size();

		// Column indices
		rppIndex = 1;
		rppTotalIndex = rppIndex + chrCount;
		rppCoverageIndex = rppTotalIndex + 1;
		qtlIndex = rppCoverageIndex + 1;

		// TODO: UPDATE!
		int colCount = qtlIndex + (qtlCount*2);
		columnNames = new String[colCount];

		// LineInfo column
		columnNames[0] = "Line";

		// For each chromosome's RPP result:
		for (int i = 0; i < s.getChrScores().size(); i++)
		{
			MABCLineStats.ChrScore cs = s.getChrScores().get(i);
			ChromosomeMap map = dataSet.getChromosomeMaps().get(cs.chrMapIndex);
			columnNames[rppIndex+i] = map.getName();
		}

		columnNames[rppTotalIndex] = "RPP Total";
		columnNames[rppCoverageIndex] = "RPP Coverage";

		// QTL section of the table
		int qtl = 0;
		for (MABCLineStats.QTLScore score: s.getQTLScores())
		{
			columnNames[qtlIndex+(qtl*2)] = score.qtl.getQTL().getName() + " LD";
			columnNames[qtlIndex+(qtl*2)+1] = score.qtl.getQTL().getName() + " Status";
			qtl++;
		}
	}

	public int getRowCount()
	{
		return viewSet.getLines().size();
	}

	public Object getValueAt(int row, int col)
	{
		LineInfo line = viewSet.getLines().get(row);
		MABCLineStats stats = line.results().getMABCLineStats();

		// Line name
		if (col == 0)
			return line.name();

		if (stats == null)
			return null;

		// RPP values
		if (col >= rppIndex && col < rppTotalIndex)
		{
			return stats.getChrScores().get(col-rppIndex).sumRP;
		}

		// RPP Total
		else if (col == rppTotalIndex)
			return stats.getRPPTotal();

		// RPP Coverage
		else if (col == rppCoverageIndex)
			return stats.getGenomeCoverage();

		// QTL values
		else if (col >= qtlIndex)
		{
			col = col-qtlIndex;
			int qtl = col / 2;

			MABCLineStats.QTLScore score = stats.getQTLScores().get(qtl);

			if (col % 2 == 0)
				return score.drag;
			else
				return score.status ? 1 : 0;
		}

		return null;
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return Line.class;
		else
			return Double.class;
	}
}