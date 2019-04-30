// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.traits;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

class TraitsTableModel extends LineDataTableModel
{
	private ArrayList<Trait> traits;

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
			if (traits.get(i-1).traitIsNumerical())
				columnNames[i] += " - numerical";
			else
				columnNames[i] += " - categorical";
		}
	}

	@Override
	public int getRowCount()
	{
		return dataSet.countLines();
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		return new CellData(null, getObjectAt(row, col));
	}

	@Override
	public Object getObjectAt(int row, int col)
	{
		// Column 0 contains the line data
		if (col == 0)
			return dataSet.getLineByIndex(row);

		// Other columns are traits in the vector of values held by a line
		Line line = dataSet.getLineByIndex(row);

		TraitValue tv = line.getTraitValues().get(col-1);
		return tv.tableValue();
	}

	@Override
	public Class getObjectColumnClass(int col)
	{
		if (col == 0)
			return Line.class;

		if (traits.get(col-1).traitIsNumerical())
			return Double.class;
		else
			return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return getObjectColumnClass(col) == String.class;
	}

	JComboBox getCategoryComboBox(int col)
	{
		JComboBox<String> combo = new JComboBox<>();

		for (String category: traits.get(col-1).getCategories())
			combo.addItem(category);

		return combo;
	}

	@Override
	public Color getDisplayColor(int row, int col)
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