// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binning;

import java.io.*;
import java.util.*;
import java.text.*;

public class EqualBins
{
	private Float min = null;
	private Float max = null;

	private int numBins = 10;

	private DecimalFormat df = new DecimalFormat("0.000000000000000");

	public static void main(String[] args)
		throws Exception
	{
		long s = System.currentTimeMillis();

		EqualBins bd = new EqualBins(args);

		System.out.println("Scanning for min/max values:");
		bd.scan(args[0]);

		System.out.println("Writing binned data...");
		bd.writeBinFile(args[0], args[1]);

		long e = System.currentTimeMillis();
		System.out.println("Time: " + (e-s) + "ms");
	}

	EqualBins(String[] args)
		throws Exception
	{
		numBins = Integer.parseInt(args[2]);
	}

	private void scan(String filename)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(filename));

		// Ignore the header lines
		// This should be the # fjFile header
		in.readLine();
		// This should be the marker names
		in.readLine();

		// Now scan the data for the min/max values
		String str = null;
		while ((str = in.readLine()) != null && str.length() > 0)
		{
			String[] split = str.split("\t", -1);

			// Ignore the first column as that contains the line name
			for (int i = 1; i < split.length; i++)
			{
				// Ignore empty strings
				if (split[i].isEmpty())
					continue;

				float value = Float.parseFloat(split[i]);

				if (min == null || value < min)
					min = value;
				if (max == null || value > max)
					max = value;
			}
		}

		in.close();

		System.out.println(" min: " + df.format(min));
		System.out.println(" max: " + df.format(max));
		System.out.println();

//		min = 0;
//		max = 1;
	}

	private void writeBinFile(String inFile, String outFile)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(inFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

		// Write the header lines
		for (int i = 0; i < 2; i++)
		{
			out.write(in.readLine());
			out.newLine();
		}

		float binSize = (max - min) / numBins;
		System.out.println(" binsize: " + df.format(binSize));

		// Now write all the data, binning it as we go
		String str = null;
		while ((str = in.readLine()) != null && str.length() > 0)
		{
			String[] split = str.split("\t");

			// Line name
			out.write(split[0]);

			// Ignore 1st column
			for (int i = 1; i < split.length; i++)
			{
				// Ignore empty strings
				if (split[i].isEmpty())
					out.write("\t");
				else
				{

					float value = Float.parseFloat(split[i]);
//					float normalized = (value - min) / (max - min);


					int bin = (int) ((value - min) / binSize);
					if (bin == numBins)
						bin = numBins-1;

//					int bin = (int) (normalized % bins);

//					if (bin > 0)
//						System.out.println("bin=" + bin);

					out.write("\t" + bin);
				}
			}

			out.newLine();
		}

		in.close();
		out.close();
	}
}