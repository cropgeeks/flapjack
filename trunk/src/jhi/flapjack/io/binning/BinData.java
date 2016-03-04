// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import java.io.*;

public class BinData
{
	private static IBinner binner;

	private String inFile;
	private String outFile;

	public static void main(String[] args)
		throws Exception
	{
		String method = args[0].toUpperCase();

		String inFile = args[1];
		String outFile = args[2];

		BinData binData = new BinData(inFile, outFile);

		if (method.equals("STANDARD"))
		{
			int numBins = Integer.parseInt(args[3]);
			binData.writeStandardFile(numBins);
		}

		else if (method.equals("SPLIT"))
		{
			int lBinCount = Integer.parseInt(args[3]);
			float split = Float.parseFloat(args[4]);
			int rBinCount = Integer.parseInt(args[5]);

			binData.writeSplitFile(lBinCount, split, rBinCount);
		}

		else if (method.equals("AUTO"))
		{
			int numBins = Integer.parseInt(args[3]);
			String histFile = args[4];

			binData.writeAutoFile(numBins, histFile);
		}
	}

	public BinData(String inFile, String outFile)
	{
		this.inFile = inFile;
		this.outFile = outFile;
	}

	public void writeStandardFile(int numBins)
		throws Exception
	{
		binner = new StandardBinner(numBins);
		writeBinFile();
	}

	public void writeSplitFile(int lBinCount, float split, int rBinCount)
		throws Exception
	{
		binner = new SplitBinner(lBinCount, split, rBinCount);
		writeBinFile();
	}

	public void writeAutoFile(int numBins, String histFile)
		throws Exception
	{
		binner = new AutoBinner(numBins, histFile);
		writeBinFile();
	}

	private void writeBinFile()
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(inFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

		// Write the header lines
		// Line one should be the standard Flapjack header
		out.write(in.readLine());
		out.newLine();

		String line;
		while ((line = in.readLine()) != null)
		{
			if (line.startsWith("#"))
			{
				if (line.startsWith("# bin") == false)
				{
					out.write(line);
					out.newLine();
				}
			}

			else
				break;
		}

		// Add the mapping information
		int binNum = 0;
		for (float[] data: binner.getBinSummary())
		{
			out.write("# bin\t" + (binNum++) + "\t" + data[0] + "\t" + data[1]);
			out.newLine();
		}

		// Now the marker names
		out.write(line);
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