// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import javax.swing.SortOrder;

public class SortableColumn
{
	// The index of this column in the model
	public int colIndex;
	// Its name
	public String name;
	// Whether to sort this column's data ascending or decending
	public SortOrder sortOrder;

	SortableColumn(int colIndex, String name, SortOrder sortOrder)
	{
		this.colIndex = colIndex;
		this.name = name;
		this.sortOrder = sortOrder;
	}

	SortableColumn cloneMe()
	{
		return new SortableColumn(colIndex, name, sortOrder);
	}

	public String toString()
	{
		return name;
	}
}