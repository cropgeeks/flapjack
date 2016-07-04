// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import javax.swing.*;
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
	private static final int LESS_THAN = 1;
	private static final int LESS_THAN_EQ = 2;
	private static final int EQUAL = 3;
	private static final int GREATER_THAN_EQ = 4;
	private static final int GREATER_THAN = 5;
	private static final int NOT_EQUAL = 6;
	private static final int FALSE = 7;
	private static final int TRUE = 8;

	public int filter = NONE;

	// What class of column (in the main table) is this representing?
	public Class colClass;

	// Cutoff value for numerical filters
	public String value;

	FilterColumn(int colIndex, Class colClass, String name, int filter)
	{
		super(colIndex, name);
		this.colClass = colClass;
		this.filter = filter;
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

			default: return "";
		}
	}

	static JComboBox<FilterColumn> getNumericalFilters()
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

	static JComboBox<FilterColumn> getBooleanFilters()
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		combo.addItem(new FilterColumn(0, Object.class, "", NONE));
		combo.addItem(new FilterColumn(0, Object.class, "", FALSE));
		combo.addItem(new FilterColumn(0, Object.class, "", TRUE));

		return combo;
	}

	boolean disabled()
	{
		// This isn't a usable filter, if:
		//   - it's set to none
		//   - it's numercial, but a value hasn't been set
		return (filter == NONE || (filter < FALSE && value == null));
	}

	RowFilter<LineDataTableModel, Object> createRowFilter()
	{
		if (colClass != Boolean.class)
		{
			double num = Double.parseDouble(value);

			switch (filter)
			{
				case LESS_THAN:
					return RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, num, colIndex);

				case LESS_THAN_EQ:
					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
							{ return (((Double)entry.getValue(colIndex)) <= num); }
					};

				case EQUAL:
					return RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, num, colIndex);

				case GREATER_THAN_EQ:
					return new RowFilter<LineDataTableModel, Object>() {
						public boolean include(Entry<? extends LineDataTableModel, ? extends Object> entry)
							{ return (((Double)entry.getValue(colIndex)) >= num); }
					};

				case GREATER_THAN:
					return RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, num, colIndex);

				case NOT_EQUAL:
					return RowFilter.numberFilter(RowFilter.ComparisonType.NOT_EQUAL, num, colIndex);
			}
		}

		else
		{
			if (filter == FALSE)
				return RowFilter.regexFilter(Boolean.toString(false), colIndex);
			else
				return RowFilter.regexFilter(Boolean.toString(true), colIndex);
		}

		return null;
	}
}