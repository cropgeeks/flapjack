// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import javax.swing.*;

/**
 * This class holds information about a single column in a LineDataTable, and is
 * used for sorting the table. The user will build up a list of these objects
 * that ultimately specify which columns to sort on as well as the sort order.
 */
public class SortColumn extends AbstractColumn
{
	// Whether to sort this column's data ascending or decending
	public SortOrder sortOrder = SortOrder.DESCENDING;

	public SortColumn()
	{
	}

	SortColumn(int colIndex, String name)
	{
		super(colIndex, name);
	}

	SortColumn(int colIndex, SortOrder sortOrder)
	{
		this.colIndex = colIndex;
		this.sortOrder = sortOrder;
	}


	// Methods required for XML serialization

	public SortOrder getSortOrder()
		{ return sortOrder; }

	public void setSortOrder(SortOrder sortOrder)
		{ this.sortOrder = sortOrder; }


	// Other methods

	@Override
	public String toString()
	{
		return name;
	}

	SortColumn cloneMe()
	{
		SortColumn clone = new SortColumn(colIndex, name);
		clone.sortOrder = sortOrder;

		return clone;
	}
}