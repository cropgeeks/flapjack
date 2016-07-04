// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

abstract class AbstractColumn
{
	// The index of this column in the model
	public int colIndex;
	// Its name
	public String name;

	protected AbstractColumn(int colIndex, String name)
	{
		this.colIndex = colIndex;
		this.name = name;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		return toString() != null ? toString().equals(o.toString()) : o.toString() == null;
	}

	@Override
	public int hashCode()
	{
		return toString() != null ? toString().hashCode() : 0;
	}
}