// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import jhi.flapjack.data.results.*;
import scri.commons.gui.*;

import javax.swing.*;

/**
 * This class holds information about a single column in a LineDataTable, and is
 * used for filtering the table. The user will build up a list of these objects
 * that ultimately specify which columns to filter on and the parameters to use
 * for that operation.
 */
public class PedVerF1sFilterColumn extends FilterColumn
{
	// Filter types
	public static final int PARENT_1 = 9;
	public static final int PARENT_2 = 10;
	public static final int EXPECTED_F1 = 11;
	public static final int TRUE_F1 = 12;
	public static final int UNDECIDED_HYBRID = 13;
	public static final int UNDECIDED_INBRED = 14;
	public static final int LIKE_P1 = 15;
	public static final int LIKE_P2 = 16;
	public static final int NO_DECISION = 17;


	public PedVerF1sFilterColumn()
	{
	}

	public PedVerF1sFilterColumn(int colIndex, Class colClass, String name, int filter)
	{
		super(colIndex, colClass, name, filter);
	}


	// Other methods

	static JComboBox<FilterColumn> pedVerF1Filters(PedVerDecisions decisionMethod)
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		combo.addItem(new FilterColumn(0, Object.class, "", NONE));

		if (decisionMethod instanceof PedVerDecisionsSimple)
		{
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", PARENT_1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", PARENT_2));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", EXPECTED_F1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", TRUE_F1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", NO_DECISION));
		}
		else if (decisionMethod instanceof PedVerDecisionsIntermediate)
		{
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", PARENT_1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", PARENT_2));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", EXPECTED_F1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", TRUE_F1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", UNDECIDED_HYBRID));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", NO_DECISION));
		}
		else if (decisionMethod instanceof PedVerDecisionsDetailed)
		{
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", PARENT_1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", PARENT_2));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", EXPECTED_F1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", TRUE_F1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", UNDECIDED_HYBRID));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", UNDECIDED_INBRED));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", LIKE_P1));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", LIKE_P2));
			combo.addItem(new PedVerF1sFilterColumn(0, Object.class, "", NO_DECISION));
		}

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
			case EXPECTED_F1:
				return "Expected F1";
			case TRUE_F1:
				return "True F1";
			case UNDECIDED_HYBRID:
				return "Undecided hybrid mix";
			case UNDECIDED_INBRED:
				return "Undecided inbred mix";
			case LIKE_P1:
				return "Like parent 1";
			case LIKE_P2:
				return "Like parent 2";
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

	public PedVerF1sFilterColumn cloneMe()
	{
		PedVerF1sFilterColumn clone = new PedVerF1sFilterColumn();
		clone.colIndex = colIndex;
		clone.name = name;
		clone.filter = filter;
		clone.isBoolFilter = isBoolFilter;
		clone.value = value;

		return clone;
	}
}