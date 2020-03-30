// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.traits;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class QTLTableModel extends AbstractTableModel
{
	ArrayList<QTL> qtls = new ArrayList<>();

	private String[] columnNames;

	boolean qtlOffMap = false;

	QTLTableModel(DataSet dataSet)
	{
		// We need to scan the chromsomes for EVERY QTL...

		// Scan each chromosome
		for (ChromosomeMap cMap: dataSet.getChromosomeMaps())
			for (QTL qtl: cMap.getQtls())
			{
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
		else if (col >= 2 && col <= 4)
			return Double.class;
		else if (col == 7)
			return Boolean.class;

		return String.class;
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

	// Special "fudge-o-matic" renderer class that deals with the optional
	// columns of QTL data that can either be numerical or textual, but is
	// always stored within the class as a string (String.class to the renderer)
	static class NumStrRenderer extends DefaultTableCellRenderer
	{
		private NumberFormat nf = NumberFormat.getInstance();

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			try
			{
				// We want numbers right aligned
				setText(nf.format(nf.parse(value.toString())));
				setHorizontalAlignment(JLabel.RIGHT);
			}
			catch (ParseException e)
			{
				// And "real" strings, left aligned
				setHorizontalAlignment(JLabel.LEFT);
				setText(value.toString());
			}

			return this;
		}
	}
}