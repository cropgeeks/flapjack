// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binning;

import java.io.*;

public class BinData
{
	private static IBinner binner;

	public static void main(String[] args)
		throws Exception
	{
		String method = args[0].toUpperCase();

		if (method.equals("STANDARD"))
		{
			int numBins = Integer.parseInt(args[3]);
			binner = new StandardBinner(numBins);
		}

		else if (method.equals("SPLIT"))
		{
			int lBinCount = Integer.parseInt(args[3]);
			float split = Float.parseFloat(args[4]);
			int rBinCount = Integer.parseInt(args[5]);

			binner = new SplitBinner(lBinCount, split, rBinCount);
		}

		else if (method.equals("AUTO"))
		{
			int numBins = Integer.parseInt(args[3]);
			String histFile = args[4];

			binner = new AutoBinner(numBins, histFile);
		}

		String inFile = args[1];
		String outFile = args[2];

		writeBinFile(inFile, outFile);
	}

	private static void writeBinFile(String inFile, String outFile)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(inFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

		// Write the header lines
		// Line one should be the standard Flapjack header
		out.write(in.readLine());
		out.newLine();

		// Add the mapping information
		int binNum = 0;
		for (float[] data: binner.getBinSummary())
		{
			out.write("# bin\t" + (binNum++) + "\t" + data[0] + "\t" + data[1]);
			out.newLine();
		}


		// Now the marker names (line 2 of the original input)
		out.write(in.readLine());
		out.newLine();

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
					int bin = binner.bin(value);

					out.write("\t" + bin);
				}
			}

			out.newLine();
		}

		in.close();
		out.close();
	}
}