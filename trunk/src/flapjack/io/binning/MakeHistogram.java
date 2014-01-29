// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binning;

import java.text.*;
import java.io.*;

public class MakeHistogram
{
	private float min = 0f;
	private float max = 1f;

	private float binSize;

	private int numBins = 1000;
	private int[] histogram;

	private StandardBinner binner;

	private DecimalFormat df = new DecimalFormat("0.000000000000000");

	public static void main(String[] args)
		throws Exception
	{
		long s = System.currentTimeMillis();

		MakeHistogram mh = new MakeHistogram(Integer.parseInt(args[2]));

		System.out.println("Writing binned data...");
		mh.writeBinFile(args[0], args[1]);

		long e = System.currentTimeMillis();
		System.out.println("Time: " + (e-s) + "ms");

	}

	MakeHistogram(int numBins)
		throws Exception
	{
		this.numBins = numBins;
		binner = new StandardBinner(numBins);
		histogram = new int[numBins];

		binSize = (max - min) / numBins;
		System.out.println(" binsize: " + df.format(binSize));
	}

	private void writeBinFile(String inFile, String outFile)
		throws Exception
	{
		calculateHistogram(inFile);

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));

		out.println("position\tcount");
		for (int i=0; i < histogram.length; i++)
			out.println(df.format(i * binSize) + "\t" + histogram[i]);

		out.close();
	}

	int[] calculateHistogram(String inFile)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(inFile));

		// Skip the header line
		for (int i = 0; i < 2; i++)
			in.readLine();

		// Now write all the data, binning it as we go
		String str = null;
		while ((str = in.readLine()) != null && str.length() > 0)
		{
			String[] split = str.split("\t");

			// Ignore 1st column
			for (int i = 1; i < split.length; i++)
			{
				// Ignore empty strings
				if (split[i].isEmpty())
					continue;

				else
				{
					float value = Float.parseFloat(split[i]);

					int bin = binner.bin(value);
					histogram[bin]++;
				}
			}
		}
		in.close();

		return histogram;
	}
}