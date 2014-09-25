// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import junit.framework.*;

public class SimMatrixTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.data.SimMatrixTest");
	}

	public void testMatrix()
		throws Exception
	{
		SimMatrix matrix = new SimMatrix();
		matrix.initialize(1);

		// Explicit 0 and 1 tests just to be sure:
		matrix.setValueAt(0, 0, 0f);
		assertEquals(0f, matrix.valueAt(0, 0), 0f);
		matrix.setValueAt(0, 0, 1f);
		assertEquals(1f, matrix.valueAt(0, 0), 0f);

		// Then 1000 values between 0 and 1
		for (float v = 0f; v <= 1f; v += (1f/1000f))
		{
			matrix.setValueAt(0, 0, v);
			assertEquals(v, matrix.valueAt(0, 0), 0f);
		}
	}
}