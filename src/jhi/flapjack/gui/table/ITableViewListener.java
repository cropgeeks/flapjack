// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

public interface ITableViewListener
{
	public void tablePreSorted();

	public void tableSorted();

	public void tableFiltered();
}