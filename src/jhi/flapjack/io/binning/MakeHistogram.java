// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import java.text.*;
import java.io.*;
import java.util.*;

public class MakeHistogram
{
	private float min = 0f;
	private float max = 1f;

	private float binSize;

	private int numBins = 1000;
	private int[] histogram;

	private StandardBinner binner;

	private DecimalFormat df = new DecimalFormat("0.000000000000000");

	private String inputFile;
	private String outputFile;

	NumberFormat nf = NumberFormat.getInstance();

	private static List<String> output;

	// Generates a histogram from allele frequency input data.
	// args[0] should be the allele frequency input data.
	// args[1] should be the desired path to the output file.
	// args[2] should be the desired number of bins for the histogram.
	public static void main(String[] args)
		throws Exception
	{
		long s = System.currentTimeMillis();

		MakeHistogram mh = new MakeHistogram(Integer.parseInt(args[2]), args[0], args[1]);
		mh.createHistogram();

		long e = System.currentTimeMillis();
		logMessage("Time: " + (e-s) + "ms");
	}

	/**
	 * Takes a number of bins which will be the number of bins used to create a histogram, as well as a String which
	 * should represent the path to a file of allele frequency data which is the data which will be histogrammed and
	 * finally a string which represents the path where the output file will be written to.
	 *
	 * @param numBins		The number of bins with which to create the histogram.
	 * @param inputFile		The path to the input file containing allele frequency data.
	 * @param outputFile	The desired path to the output file.
	 */
	public MakeHistogram(int numBins, String inputFile, String outputFile)
	{
		this.numBins = numBins;
		this.inputFile = inputFile;
		this.outputFile = outputFile;

		output = new ArrayList<String>();
	}

	private void setupBinner(int numBins)
	{
		binner = new StandardBinner(numBins);
		histogram = new int[numBins];

		binSize = (max - min) / numBins;
		logMessage(" binsize: " + df.format(binSize));
	}

	public List<String> createHistogram()
		throws Exception
	{
		setupBinner(numBins);

		logMessage("Writing binned data...");
		writeBinFile(inputFile, outputFile);

		outputStats();

		return output;
	}

	private int[] calculateHistogram(String inFile)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(inFile));

		// Skip header lines
		String str;
		while ((str = in.readLine()) != null)
			if (str.startsWith("#") == false)
				break;

		// Note we're deliberately skipping the markers header line implicitly
		// here
		// Now write all the data, binning it as we go
		while ((str = in.readLine()) != null && str.length() > 0)
		{
			String[] split = str.split("\t");

			// Ignore 1st column
			for (int i = 1; i < split.length; i++)
			{
				// Ignore empty strings
				if (split[i].isEmpty() == false)
				{
					float value = nf.parse(split[i]).floatValue();

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
		logMessage("Mean\t" + getBinRangeString(meanBin));
		logMessage("Median\t" + getBinRangeString(medianBin));
		logMessage("Mode\t" + getBinRangeString(modeBin));
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

	private static void logMessage(String message)
	{
		System.out.println(message);
		output.add(message);
	}
}