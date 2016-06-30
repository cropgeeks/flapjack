// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.util.*;
import javax.swing.*;

/**
 * This class holds information about a single column in a LineDataTable, and is
 * used for both sorting or filtering the table. The user will build up a list
 * of these objects that ultimately specify which columns to sort or filter on
 * and the parameters to use for that operation.
 */
public class SortFilterColumn
{
	// The index of this column in the model
	public int colIndex;
	// Its name
	public String name;

	// Sort options:
	// Whether to sort this column's data ascending or decending
	public SortOrder sortOrder = SortOrder.DESCENDING;

	// Filter options:
	public Filter filter;
	// Cutoff value for the filter
	public String value;

	private SortFilterColumn()
	{
	}

	SortFilterColumn(int colIndex, String name)
	{
		this.colIndex = colIndex;
		this.name = name;
	}

	SortFilterColumn cloneMe()
	{
		SortFilterColumn clone = new SortFilterColumn();
		clone.colIndex = colIndex;
		clone.name = name;

		clone.sortOrder = sortOrder;

		clone.filter = filter;
		clone.value = value;

		return clone;
	}

	public String toString()
	{
		return name;
	}

	public static Filter[] getFilters()
	{
		return new Filter[] {
			new Filter(null),
			new Filter(RowFilter.ComparisonType.BEFORE),
			new Filter(RowFilter.ComparisonType.EQUAL),
			new Filter(RowFilter.ComparisonType.AFTER),
			new Filter(RowFilter.ComparisonType.NOT_EQUAL)
		};
	}

	public static class Filter
	{
		public RowFilter.ComparisonType type;

		Filter(RowFilter.ComparisonType type)
			{ this.type = type;	}

		public String toString()
		{
			if (type != null)
			{
				if (type == RowFilter.ComparisonType.BEFORE) return "<";
				if (type == RowFilter.ComparisonType.EQUAL) return "=";
				if (type == RowFilter.ComparisonType.AFTER) return ">";
				if (type == RowFilter.ComparisonType.NOT_EQUAL) return "<>";
			}

			return "";
		}
	}
}