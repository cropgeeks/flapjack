// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import javax.swing.*;

import jhi.flapjack.data.results.*;
import scri.commons.gui.RB;

/**
 * This class holds information about a single column in a LineDataTable, and is
 * used for filtering the table. The user will build up a list of these objects
 * that ultimately specify which columns to filter on and the parameters to use
 * for that operation.
 */
public class FilterColumn extends AbstractColumn
{
	// Filter types
	public static final int NONE = 0;
	public static final int LESS_THAN = 1;
	public static final int LESS_THAN_EQ = 2;
	public static final int EQUAL = 3;
	public static final int GREATER_THAN_EQ = 4;
	public static final int GREATER_THAN = 5;
	public static final int NOT_EQUAL = 6;
	public static final int FALSE = 7;
	public static final int TRUE = 8;
	public static final int PARENT_1 = 9;
	public static final int PARENT_2 = 10;
	public static final int EXPECTED_F1 = 11;
	public static final int TRUE_F1 = 12;
	public static final int UNDECIDED_HYBRID = 13;
	public static final int UNDECIDED_INBRED = 14;
	public static final int LIKE_P1 = 15;
	public static final int LIKE_P2 = 16;
	public static final int NO_DECISION = 17;

	private int filter = NONE;

	// What class of column (in the main table) is this representing?
	private boolean isBoolFilter;

	private boolean isPedVerF1sFilter;

	// Cutoff value for numerical filters
	private String value;

	public FilterColumn()
	{
	}

	public FilterColumn(int colIndex, Class colClass, String name, int filter)
	{
		super(colIndex, name);
		this.filter = filter;

		isBoolFilter = colClass == Boolean.class;
	}

	public FilterColumn(int colIndex, Class colClass, String name, int filter, boolean isPedVerF1sFilter)
	{
		super(colIndex, name);
		this.filter = filter;

		this.isPedVerF1sFilter = isPedVerF1sFilter;
	}

	// Methods required for XML serialization

	public int getFilter()
		{ return filter; }

	public void setFilter(int filter)
		{ this.filter = filter; }

	public boolean isBoolFilter()
		{ return isBoolFilter; }

	public void setBoolFilter(boolean boolFilter)
		{ isBoolFilter = boolFilter; }

	public String getValue()
		{ return value; }

	public void setValue(String value)
		{ this.value = value; }

	public boolean isPedVerF1sFilter()
		{ return isPedVerF1sFilter; }

	public void setPedVerF1sFilter(boolean isPedVerF1sFilter)
		{ this.isPedVerF1sFilter = isPedVerF1sFilter; }


	// Other methods

	static JComboBox<FilterColumn> numericalFilters()
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		combo.addItem(new FilterColumn(0, Object.class, "", NONE));
		combo.addItem(new FilterColumn(0, Object.class, "", LESS_THAN));
		combo.addItem(new FilterColumn(0, Object.class, "", LESS_THAN_EQ));
		combo.addItem(new FilterColumn(0, Object.class, "", EQUAL));
		combo.addItem(new FilterColumn(0, Object.class, "", GREATER_THAN_EQ));
		combo.addItem(new FilterColumn(0, Object.class, "", GREATER_THAN));
		combo.addItem(new FilterColumn(0, Object.class, "", NOT_EQUAL));

		return combo;
	}

	static JComboBox<FilterColumn> booleanFilters()
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		combo.addItem(new FilterColumn(0, Object.class, "", NONE));
		combo.addItem(new FilterColumn(0, Object.class, "", FALSE));
		combo.addItem(new FilterColumn(0, Object.class, "", TRUE));

		return combo;
	}

	static JComboBox<FilterColumn> pedVerF1Filters(PedVerDecisions decisionMethod)
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		if (decisionMethod instanceof PedVerDecisionsSimple)
		{
			combo.addItem(new FilterColumn(0, Object.class, "", PARENT_1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", PARENT_2, true));
			combo.addItem(new FilterColumn(0, Object.class, "", EXPECTED_F1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", TRUE_F1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", NO_DECISION, true));
		}
		else if (decisionMethod instanceof PedVerDecisionsIntermediate)
		{
			combo.addItem(new FilterColumn(0, Object.class, "", PARENT_1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", PARENT_2, true));
			combo.addItem(new FilterColumn(0, Object.class, "", EXPECTED_F1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", TRUE_F1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", UNDECIDED_HYBRID, true));
			combo.addItem(new FilterColumn(0, Object.class, "", NO_DECISION, true));
		}
		else if (decisionMethod instanceof PedVerDecisionsDetailed)
		{
			combo.addItem(new FilterColumn(0, Object.class, "", PARENT_1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", PARENT_2, true));
			combo.addItem(new FilterColumn(0, Object.class, "", EXPECTED_F1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", TRUE_F1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", UNDECIDED_HYBRID, true));
			combo.addItem(new FilterColumn(0, Object.class, "", UNDECIDED_INBRED, true));
			combo.addItem(new FilterColumn(0, Object.class, "", LIKE_P1, true));
			combo.addItem(new FilterColumn(0, Object.class, "", LIKE_P2, true));
			combo.addItem(new FilterColumn(0, Object.class, "", NO_DECISION, true));
		}

		return combo;
	}

	public boolean disabled()
	{
		// This isn't a usable filter, if:
		//   - it's set to none
		//   - it's numercial, but a value hasn't been set
		return (filter == NONE || (filter < FALSE && value == null));
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
		if (isBoolFilter)
		{
			// These can stay as default filters because CellData.toString()
			// return "true" / "false" for Boolean columns
			if (filter == FALSE)
				return RowFilter.regexFilter(Boolean.toString(false), colIndex);
			else
				return RowFilter.regexFilter(Boolean.toString(true), colIndex);
		}

		else if (isPedVerF1sFilter)
		{
			return RowFilter.regexFilter(toString(), colIndex);
		}

		else
		{
			double value = Double.parseDouble(this.value);

			switch (filter)
			{
				// Left for reference
				// Each cell in the table holds a CellData rather than a
				// number/boolean/etc so these default filters won't work
//				return RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, value, colIndex);

				case LESS_THAN:

					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
							{ return convert(entry.getValue(colIndex)) < value || noFilter(entry); }
					};

				case LESS_THAN_EQ:
					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
							{ return convert(entry.getValue(colIndex)) <= value || noFilter(entry); }
					};

				case EQUAL:
					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
						{ return convert(entry.getValue(colIndex)) == value || noFilter(entry); }
					};

				case GREATER_THAN_EQ:
					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
						{ return convert(entry.getValue(colIndex)) >= value || noFilter(entry); }
					};

				case GREATER_THAN:
					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
						{ return convert(entry.getValue(colIndex)) > value || noFilter(entry); }
					};

				case NOT_EQUAL:
					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
						{ return convert(entry.getValue(colIndex)) != value || noFilter(entry); }
					};
			}
		}

		return null;
	}

	private boolean noFilter(RowFilter.Entry<? extends LineDataTableModel, ? extends Object> entry)
	{
		return ((CellData)entry.getValue(0)).getLineInfo().getResults().isSortToTop();
	}

	// This is used when we're using the FilterColumn objects to auto-select
	// values in the table. It's a simple does the entry in the given column
	// match the value of this 'filter'
	public boolean matches(Object oEntry)
	{
		if (isBoolFilter)
		{
			if (filter == FALSE)
				return (Boolean)oEntry == false;
			else
				return (Boolean)oEntry == true;
		}

		else if (isPedVerF1sFilter)
		{
			return toString().equals(oEntry);
		}

		else
		{
			double entry = Double.parseDouble(oEntry.toString());
			double value = Double.parseDouble(this.value);

			switch (filter)
			{
				case LESS_THAN:       return entry < value;
				case LESS_THAN_EQ:    return entry <= value;
				case EQUAL:           return entry == value;
				case GREATER_THAN_EQ: return entry >= value;
				case GREATER_THAN:    return entry > value;
				case NOT_EQUAL:       return entry != value;
			}
		}

		return false;
	}

	@Override
	public String toString()
	{
		switch (filter)
		{
			case LESS_THAN:
				return RB.getString("gui.table.FilterColumn.lessThan");
			case LESS_THAN_EQ:
				return RB.getString("gui.table.FilterColumn.lessThanEq");
			case EQUAL:
				return RB.getString("gui.table.FilterColumn.equal");
			case GREATER_THAN_EQ:
				return RB.getString("gui.table.FilterColumn.greaterThanEq");
			case GREATER_THAN:
				return RB.getString("gui.table.FilterColumn.greaterThan");
			case NOT_EQUAL:
				return RB.getString("gui.table.FilterColumn.notEqual");
			case FALSE:
				return RB.getString("gui.table.FilterColumn.false");
			case TRUE:
				return RB.getString("gui.table.FilterColumn.true");
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

			default: return "";
		}
	}

	// Returns short form text for each filter type (eg < or > rather than less
	// than or greater than)
	String toShortString()
	{
		switch (filter)
		{
			case LESS_THAN: return "<";
			case LESS_THAN_EQ: return "<=";
			case EQUAL: return "=";
			case GREATER_THAN_EQ: return ">=";
			case GREATER_THAN: return ">";
			case NOT_EQUAL: return "<>";
			case FALSE: return "FALSE";
			case TRUE: return "TRUE";
			case PARENT_1: return "Parent 1";
			case PARENT_2: return "Parent 2";
			case EXPECTED_F1: return "Expected F1";
			case TRUE_F1: return "True F1";
			case UNDECIDED_HYBRID: return "Undecided hybrid mix";
			case UNDECIDED_INBRED: return "Undecided inbred mix";
			case LIKE_P1: return "Like parent 1";
			case LIKE_P2: return "Like parent 2";
			case NO_DECISION: return "No decision";

			default: return "";
		}
	}

	public FilterColumn cloneMe()
	{
		FilterColumn clone = new FilterColumn();
		clone.colIndex = colIndex;
		clone.name = name;
		clone.filter = filter;
		clone.isBoolFilter = isBoolFilter;
		clone.isPedVerF1sFilter = isPedVerF1sFilter;
		clone.value = value;

		return clone;
	}
}