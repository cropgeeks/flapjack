// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.traits;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

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
			columnNames[i] = traits.get(i-1).getName();
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
		// Row 0 contains the experiment data
		if (row == 0)
		{
			// We don't want to dispaly a line name in the experiment "header"
			if (col == 0)
				return null;
			else
				return traits.get(col-1).getExperiment();
		}

		// Column 0 contains the line data
		else if (col == 0)
			return dataSet.getLineByIndex(row-1);

		// Other columns are traits in the vector of values held by a line
		Line line = dataSet.getLineByIndex(row-1);

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
		if (col == 0 || row == 0)
			return false;
		else
			return true;
	}

	JComboBox getCategoryComboBox(int col)
	{
		JComboBox<String> combo = new JComboBox<>();

		for (String category: traits.get(col-1).getCategories())
			combo.addItem(category);
		combo.addItem(null);

		return combo;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		Line line = dataSet.getLineByIndex(row-1);
		float newValue = 0;

		if (value == null)
			line.getTraitValues().get(col-1).setDefined(false);

		else if (value instanceof String)
		{
			try
			{
				// Parse/determine the category
				newValue = traits.get(col-1).computeValue((String)value);
				line.getTraitValues().get(col-1).setDefined(true);
			}
			catch (Exception e) {}
		}
		else
		{
			newValue = (Float) value;
			line.getTraitValues().get(col-1).setDefined(true);
		}

		// Update it in the underlying model
		line.getTraitValues().get(col-1).setValue(newValue);
	    fireTableCellUpdated(row-1, col);

	    Actions.projectModified();
	}
}