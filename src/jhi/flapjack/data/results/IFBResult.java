// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

import java.util.*;

public class IFBResult extends XMLRoot
{
	private int dataCount;

	public IFBResult()
	{
	}

	public int getDataCount()
		{ return dataCount; }

	public void setDataCount(int dataCount)
		{ this.dataCount = dataCount; }
}