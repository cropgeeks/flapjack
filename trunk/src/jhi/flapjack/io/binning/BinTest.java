// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import junit.framework.*;

public class BinTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.io.binning.BinTest");
	}

	public void testStandardBinning()
		throws Exception
	{
		IBinner binner = new StandardBinner(10);

		// 0 to 0.9 in 0.1 increments...
		for (int bin = 0; bin < 10; bin++)
		{
			float value = bin * 0.1f;

			assertEquals(bin, binner.bin(value));
		}

		// Special case for value of 1 that should still map to bin 9
		assertEquals(9, binner.bin(1.0f));
	}

	public void testSplitBinning()
		throws Exception
	{
		// 0.[n]1 sometimes used to cope with Java inprecision in fp arithmetic


		// (Semi) duplicate of the normal 10 bins, just with 5 either side
		IBinner binner = new SplitBinner(5, 0.5f, 5);

		// Left of the split
		assertEquals(0, binner.bin(0.0f));
		assertEquals(1, binner.bin(0.1f));
		assertEquals(2, binner.bin(0.2f));
		assertEquals(3, binner.bin(0.3f));
		// 0.4 & 0.5 map to bin 4, because the method uses <= on the split point
		assertEquals(4, binner.bin(0.4f));
		assertEquals(4, binner.bin(0.5f));

		// Right of the split
		assertEquals(5, binner.bin(0.51f));
		assertEquals(6, binner.bin(0.6f));
		assertEquals(7, binner.bin(0.71f));
		assertEquals(8, binner.bin(0.8f));
		assertEquals(9, binner.bin(0.91f));
		assertEquals(9, binner.bin(1.0f));


		// Now test something more advanced:
		binner = new SplitBinner(2, 0.7f, 10);

//		float f = 0;
//		for (int i = 0; i <= 10; i++)
//		{
//			System.out.println(f + " - " + binner.bin(f));
//			f+= 0.1f;
//		}

		// Left of the split
		assertEquals(0, binner.bin(0.0f));
		assertEquals(0, binner.bin(0.1f));
		assertEquals(0, binner.bin(0.2f));
		assertEquals(0, binner.bin(0.3f));
		assertEquals(1, binner.bin(0.4f));
		assertEquals(1, binner.bin(0.5f));
		assertEquals(1, binner.bin(0.6f));

		// Right of the split
		assertEquals(2, binner.bin(0.71f));
		assertEquals(5, binner.bin(0.8f));
		assertEquals(8, binner.bin(0.9f));
		assertEquals(11, binner.bin(1.0f));
	}
}