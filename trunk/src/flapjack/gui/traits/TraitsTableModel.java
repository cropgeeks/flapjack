package flapjack.gui.traits;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

class TraitsTableModel extends AbstractTableModel
{
	private DataSet dataSet;
	private Vector<Trait> traits;

	private JTable table;
	private String[] columnNames;

	TraitsTableModel(DataSet dataSet, JTable table)
	{
		this.dataSet = dataSet;
		this.table = table;

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

	public Class getColumnClass(int col)
	{
		if (col == 0)
			return Line.class;

		if (traits.get(col-1).traitIsNumerical())
			return Float.class;
		else
			return String.class;
	}

	public boolean isCellEditable(int row, int col)
	{
		if (col == 0)
			return false;
		else
			return true;
	}

	JComboBox getCategoryComboBox(int col)
	{
		JComboBox combo = new JComboBox();

		for (String category: traits.get(col-1).getCategories())
			combo.addItem(category);
		combo.addItem(null);

		return combo;
	}

	public void setValueAt(Object value, int row, int col)
	{
		Line line = dataSet.getLineByIndex(row);
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
	    fireTableCellUpdated(row, col);

	    Actions.projectModified();
	}
}

class TraitsTableRenderer extends DefaultTableCellRenderer
{
	private static NumberFormat nf = NumberFormat.getInstance();

	TraitsTableRenderer(int alignment)
	{
		setHorizontalAlignment(alignment);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected,
			hasFocus, row, column);

		if (value instanceof Number)
			setText(nf.format((Number)value));

		return this;
	}
}