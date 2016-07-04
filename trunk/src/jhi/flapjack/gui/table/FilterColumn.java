// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import javax.swing.*;

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
	private static final int GREATER_THAN = 2;
	private static final int EQUAL = 3;
	private static final int NOT_EQUAL = 4;
	private static final int FALSE = 5;
	private static final int TRUE = 6;

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

	FilterColumn cloneMe()
	{
		return new FilterColumn(colIndex, colClass, name, filter);
	}

	@Override
	public String toString()
	{
		switch (filter)
		{
			case LESS_THAN: return "<";
			case GREATER_THAN: return ">";
			case EQUAL: return "=";
			case NOT_EQUAL: return "<>";
			case FALSE: return Boolean.toString(false);
			case TRUE: return Boolean.toString(true);

			default: return "";
		}
	}

	public static JComboBox<FilterColumn> getNumericalFilters()
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		combo.addItem(new FilterColumn(0, Object.class, "", NONE));
		combo.addItem(new FilterColumn(0, Object.class, "", LESS_THAN));
		combo.addItem(new FilterColumn(0, Object.class, "", GREATER_THAN));
		combo.addItem(new FilterColumn(0, Object.class, "", EQUAL));
		combo.addItem(new FilterColumn(0, Object.class, "", NOT_EQUAL));

		return combo;
	}

	public static JComboBox<FilterColumn> getBooleanFilters()
	{
		JComboBox<FilterColumn> combo = new JComboBox<>();

		combo.addItem(new FilterColumn(0, Object.class, "", NONE));
		combo.addItem(new FilterColumn(0, Object.class, "", FALSE));
		combo.addItem(new FilterColumn(0, Object.class, "", TRUE));

		return combo;
	}

	public boolean disabled()
	{
		// This isn't a usable filter, if:
		//   - it's set to none
		//   - it's numercial, but a value hasn't been set
		return (filter == NONE || (filter < FALSE && value == null));
	}
}