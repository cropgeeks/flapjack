// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class OptimizeProject extends SimpleJob
{
	@Override
	public int getValue()
		{ return 0; }

	@Override
	public void runJob(int jobNum)
	{
		try
		{
			long s = System.currentTimeMillis();
			ProjectSerializerDB.vacuum();
			long e = System.currentTimeMillis();

			System.out.println("Optimize time: " + (e-s) + "ms");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}