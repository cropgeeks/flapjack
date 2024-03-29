// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import javax.swing.*;

/**
 * This class holds information about a single column in a LineDataTable, and is
 * used for filtering the table. The user will build up a list of these objects
 * that ultimately specify which columns to filter on and the parameters to use
 * for that operation.
 */
public class PedVerLinesFilterColumn extends FilterColumn
{
	// Filter types
	public static final int PARENT_1 = 9;
	public static final int PARENT_2 = 10;
	public static final int VERIFIED = 11;
	public static final int NO_DECISION = 12;


	public PedVerLinesFilterColumn()
	{
	}

	public PedVerLinesFilterColumn(int colIndex, Class colClass, String name, int filter)
	{
		super(colIndex, colClass, name, filter);
	}


	// Other methods

	static JComboBox<FilterColumn> pedVerLinesFilters()
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		combo.addItem(new FilterColumn(0, Object.class, "", NONE));

		combo.addItem(new PedVerLinesFilterColumn(0, Object.class, "", PARENT_1));
		combo.addItem(new PedVerLinesFilterColumn(0, Object.class, "", PARENT_2));
		combo.addItem(new PedVerLinesFilterColumn(0, Object.class, "", VERIFIED));
		combo.addItem(new PedVerLinesFilterColumn(0, Object.class, "", NO_DECISION));

		return combo;
	}

	// Safety net catch for Integer values being passed into createRowFilter
	// below, that can't be cast to a Double (despite int->double being ok)
	Double convert(Object object)
	{
		Object o = ((CellData)object).getData();

		if (o instanceof Double)
			return (Double)o;
		else if (o instanceof Integer)
			return (double)(Integer)o;
		else if (o instanceof Float)
			return (double)(Float)o;

		return null;
	}

	RowFilter<LineDataTableModel, Object> createRowFilter()
	{
		return RowFilter.regexFilter(toString(), colIndex);
	}

	private boolean noFilter(RowFilter.Entry<? extends LineDataTableModel, ? extends Object> entry)
	{
		return ((CellData)entry.getValue(0)).getLineInfo().getLineResults().isSortToTop();
	}

	// This is used when we're using the FilterColumn objects to auto-select
	// values in the table. It's a simple does the entry in the given column
	// match the value of this 'filter'
	public boolean matches(Object oEntry)
	{
		return toString().equals(oEntry);
	}

	@Override
	public String toString()
	{
		switch (filter)
		{
			case PARENT_1:
				return "Parent 1";
			case PARENT_2:
				return "Parent 2";
			case VERIFIED:
				return "Pedigree verified";
			case NO_DECISION:
				return "No decision";

			default: return super.toString();
		}
	}

	// Returns short form text for each filter type (eg < or > rather than less
	// than or greater than)
	String toShortString()
	{
		return toString();
	}

	public PedVerLinesFilterColumn cloneMe()
	{
		PedVerLinesFilterColumn clone = new PedVerLinesFilterColumn();
		clone.colIndex = colIndex;
		clone.name = name;
		clone.filter = filter;
		clone.isBoolFilter = isBoolFilter;
		clone.value = value;

		return clone;
	}
}