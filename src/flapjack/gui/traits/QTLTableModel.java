package flapjack.gui.traits;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

class QTLTableModel extends AbstractTableModel
{
	private DataSet dataSet;
	Vector<QTL> qtls = new Vector<QTL>();

	private JTable table;
	private String[] columnNames;

	QTLTableModel(DataSet dataSet, JTable table)
	{
		this.dataSet = dataSet;
		this.table = table;

		// We need to scan the chromsomes for EVERY QTL...

		// Scan each chromosome
		for (ChromosomeMap cMap: dataSet.getChromosomeMaps())
			// And each track within that chromosome
			for (Vector<Feature> track: cMap.getTrackSet())
				// And along each track
				for (Feature f: track)
					if (f instanceof QTL)
						qtls.add((QTL)f);

		setColumnNames();
	}

	void setColumnNames()
	{
		if (qtls.size() == 0)
			columnNames = new String[0];
		else
		{
			QTL qtl = qtls.get(0);

			columnNames = new String[7 + qtl.getValues().length];

			columnNames[0] = "QTL";
			columnNames[1] = "Chromosome";
			columnNames[2] = "Position";
			columnNames[3] = "Minimum";
			columnNames[4] = "Maximum";
			columnNames[5] = "Trait";
			columnNames[6] = "Experiment";

			for (int i = 0; i < qtl.getValues().length; i++)
				columnNames[7+i] = qtl.getVNames()[i];
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
			case 0: return qtl.getName();
			case 1: return qtl.getChromosomeMap().getName();
			case 2: return qtl.getPosition();
			case 3: return qtl.getMin();
			case 4: return qtl.getMax();
			case 5: return qtl.getTrait();
			case 6: return qtl.getExperiment();
		}

		return qtl.getValues()[col-7];
	}

	public Class getColumnClass(int col)
	{
		if (col < 2)
			return String.class;
		else if (col >= 2 && col <= 4)
			return Float.class;
		else
			return String.class;
	}
}