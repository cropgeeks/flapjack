// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.traits;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class TraitsTableModel extends AbstractTableModel
{
	private DataSet dataSet;
	private ArrayList<Trait> traits;

	private String[] columnNames;

	TraitsTableModel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		traits = dataSet.getTraits();

		setColumnNames();
	}

	void setColumnNames()
	{
		columnNames = new String[traits.size()+1];

		columnNames[0] = RB.getString("gui.traits.TraitsTableModel.line");
		for (int i = 1; i < columnNames.length; i++)
		{
			columnNames[i] = traits.get(i-1).getName();
			if (traits.get(i-1).experimentDefined())
				columnNames[i] += " (" + traits.get(i-1).getExperiment() + ")";
		}
	}

	@Override
	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getRowCount()
	{
		return dataSet.countLines();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		// Column 0 contains the line data
		if (col == 0)
			return dataSet.getLineByIndex(row);

		// Other columns are traits in the vector of values held by a line
		Line line = dataSet.getLineByIndex(row);

		Trait trait = traits.get(col-1);
		TraitValue tv = line.getTraitValues().get(col-1);

		if (tv.isDefined() == false)
			return null;

		else if (trait.traitIsNumerical())
			return tv.getValue();
		else
			return trait.format(tv);

	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return Line.class;

		if (traits.get(col-1).traitIsNumerical())
			return Float.class;
		else
			return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return getColumnClass(col) == String.class;
	}

	JComboBox getCategoryComboBox(int col)
	{
		JComboBox<String> combo = new JComboBox<>();

		for (String category: traits.get(col-1).getCategories())
			combo.addItem(category);

		return combo;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
	}

	Color displayColor(int row, int col)
	{
		if (col > 0)
		{
			Line line = dataSet.getLineByIndex(row);
			Trait trait = traits.get(col-1);
			TraitValue tv = line.getTraitValues().get(col-1);

			if (tv.isDefined())
				return tv.displayColor();
		}

		return null;
	}
}