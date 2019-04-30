// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import jhi.flapjack.data.*;

public abstract class AbstractColumn extends XMLRoot
{
	// The index of this column in the model
	public int colIndex;
	// Its name
	public String name;

	public AbstractColumn()
	{
	}

	protected AbstractColumn(int colIndex, String name)
	{
		this.colIndex = colIndex;
		this.name = name;
	}


	// Methods required for XML serialization

	public int getColIndex()
		{ return colIndex; }

	public void setColIndex(int colIndex)
		{ this.colIndex = colIndex; }

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }


	// Other methods

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