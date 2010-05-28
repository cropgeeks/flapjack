// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.traits;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class QTLTableModel extends AbstractTableModel
{
	ArrayList<QTL> qtls = new ArrayList<QTL>();

	private String[] columnNames;

	boolean qtlOffMap = false;

	QTLTableModel(DataSet dataSet)
	{
		// We need to scan the chromsomes for EVERY QTL...

		// Scan each chromosome
		for (ChromosomeMap cMap: dataSet.getChromosomeMaps())
			for (Feature feature: cMap.getFeatures())
				if (feature instanceof QTL)
				{
					QTL qtl = (QTL) feature;
					qtls.add(qtl);
					if (qtl.isAllowed() == false)
						qtlOffMap = true;
				}

		setColumnNames();
	}

	void setColumnNames()
	{
		if (qtls.size() == 0)
			columnNames = new String[0];
		else
		{
			QTL qtl = qtls.get(0);

			columnNames = new String[8 + qtl.getValues().length];

			columnNames[0] = RB.getString("gui.traits.QTLTableModel.qtl");
			columnNames[1] = RB.getString("gui.traits.QTLTableModel.chromosome");
			columnNames[2] = RB.getString("gui.traits.QTLTableModel.position");
			columnNames[3] = RB.getString("gui.traits.QTLTableModel.min");
			columnNames[4] = RB.getString("gui.traits.QTLTableModel.max");
			columnNames[5] = RB.getString("gui.traits.QTLTableModel.trait");
			columnNames[6] = RB.getString("gui.traits.QTLTableModel.experiment");
			columnNames[7] = RB.getString("gui.traits.QTLTableModel.visible");

			for (int i = 0; i < qtl.getValues().length; i++)
				columnNames[8+i] = qtl.getVNames()[i];
		}
	}

	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getRowCount()
	{
		return qtls.size();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		QTL qtl = qtls.get(row);

		switch (col)
		{
			case 0: return qtl;
			case 1: return qtl.getChromosomeMap().getName();
			case 2: return qtl.getPosition();
			case 3: return qtl.getMin();
			case 4: return qtl.getMax();
			case 5: return qtl.getTrait();
			case 6: return qtl.getExperiment();
			case 7: return qtl.isVisible();
		}

		return qtl.getValues()[col-8];
	}

	public Class getColumnClass(int col)
	{
		if (col == 0)
			return QTL.class;
		else if (col ==5)
			return String.class;
		else if (col == 6)
			return Float.class; // it's a string, but right justified
		else if (col == 7)
			return Boolean.class;
		else
			return Float.class; // might be a string or a float
	}

	public boolean isCellEditable(int row, int col)
	{
		if (col == 7 && qtls.get(row).isAllowed())
			return true;

		return false;
	}

	// This should only be called on col 7 (isVisible)
	public void setValueAt(Object value, int row, int col)
	{
		qtls.get(row).setVisible((Boolean) value);

		fireTableCellUpdated(row, col);
	    Actions.projectModified();
	}
}