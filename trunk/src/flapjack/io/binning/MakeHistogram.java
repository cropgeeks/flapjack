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

	// Generates a histogram from allele frequency input data.
	// args[0] should be the allele frequency input data.
	// args[1] should be the desired path to the output file.
	// args[2] should be the desired number of bins for the histogram.
	// args[3] should be the desried path for histogram stats file (averages)...
	public static void main(String[] args)
		throws Exception
	{
		long s = System.currentTimeMillis();

		MakeHistogram mh = new MakeHistogram(Integer.parseInt(args[2]));

		System.out.println("Writing binned data...");
		mh.writeBinFile(args[0], args[1]);

		mh.outputStats();

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
				if (split[i].isEmpty() == false)
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

	private void outputStats()
		throws IOException
	{
		// Get the total number of values stored in all bins
		int total = 0;
		for (int i : histogram)
			total += i;

		// Get our averages
		int modeBin = getModeBin();
		int medianBin = getMedianBin(total);
		int meanBin = getMeanBin(total);

		// Print to stdout for display on Germinate page
		System.out.println("Mean\t" + getBinRangeString(meanBin));
		System.out.println("Median\t" + getBinRangeString(medianBin));
		System.out.println("Mode\t" + getBinRangeString(modeBin));
	}

	private int getMeanBin(int total)
	{
		// Mean
		int meanBin = 0;
		for (int i=0; i < histogram.length; i++)
			meanBin += i * histogram[i];

		return meanBin /= total;
	}

	private int getMedianBin(int total)
	{
		int middle = total / 2;
		int runningTot = 0;
		int medianBin = 0;

		for (int i=0; i < histogram.length && runningTot < middle; i++)
		{
			runningTot += histogram[i];
			medianBin = i;

		}
		return medianBin;
	}

	private int getModeBin()
	{
		int modeBin = 0;

		for (int i=0; i < histogram.length; i++)
			if (histogram[i] > modeBin)
				modeBin = i;

		return modeBin;
	}

	private String getBinRangeString(int bin)
	{
		df.setMaximumFractionDigits(3);

		return df.format(bin*binSize) + "-" + df.format((bin+1)*binSize);
	}
}
