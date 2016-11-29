// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.colors.*;

public class CreateImage
{
	private IBinner binner;

	private String imageFile;

	public static void main(String[] args)
		throws Exception
	{
		NumberFormat nf = NumberFormat.getInstance();

		String imageFile = args[0];
		CreateImage imageCreator = new CreateImage(imageFile);

		String method = args[1].toUpperCase();

		if (method.equals("STANDARD"))
		{
			int numBins = Integer.parseInt(args[2]);
			imageCreator.createStandardImage(numBins);
		}

		else if (method.equals("SPLIT"))
		{
			int lBinCount = Integer.parseInt(args[2]);
			float split = nf.parse(args[3]).floatValue();
			int rBinCount = Integer.parseInt(args[4]);

			imageCreator.createSplitImage(lBinCount, split, rBinCount);
		}

		else if (method.equals("AUTO"))
		{
			int numBins = Integer.parseInt(args[2]);
			String histFile = args[3];

			imageCreator.createAutoImage(numBins, histFile);
		}
	}

	public CreateImage(String imageFile)
	{
		this.imageFile = imageFile;
	}

	public void createStandardImage(int numBins)
		throws Exception
	{
		binner = new StandardBinner(numBins);
		createImage();
	}

	public void createAutoImage(int numBins, String histogramFile)
		throws Exception
	{
		binner = new AutoBinner(numBins, histogramFile);
		createImage();
	}

	public void createSplitImage(int lBinCount, float split, int rBinCount)
		throws Exception
	{
		binner = new SplitBinner(lBinCount, split, rBinCount);
		createImage();
	}

	private void createImage()
		throws Exception
	{
		// Get the binning information
		ArrayList<float[]> bins = binner.getBinSummary();

		float totalSize = bins.get(bins.size()-1)[1];

		// Fake up a state table that can be used to feed the colour scheme
		StateTable stateTable = new StateTable(0);
		for (int i = 0; i < bins.size(); i++)
			stateTable.getStateCode("" + i, true, "", true, "/");

		Prefs.setColorDefaults();
		BinnedColorScheme colors = new BinnedColorScheme(stateTable, 5, 5);

		PrintWriter writer = new PrintWriter(new FileWriter(new File(imageFile)));
		NumberFormat nf = NumberFormat.getNumberInstance();

		// For each bin output its size (as a percentage of the overall set of
		// bins) as part of a comma separated list
		for (int bin=0; bin < bins.size(); bin++)
		{
			float[] binArr = bins.get(bin);
			float percent = ((binArr[1]-binArr[0])/totalSize)*100f;

			String out;
			if (bin < bins.size()-1)
				out = nf.format(percent) + ", ";
			else
				out = "" + nf.format(percent);

			writer.print(out);
		}
		// Write end of line character so we can include other data
		writer.println();

		// For each bin output its colour as a hex string.
		for (int bin=0; bin < bins.size(); bin++)
		{
			Color color = colors.getColor(bin);
			String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

			if (bin < bins.size()-1)
				writer.print(hex + ", ");
			else
				writer.println(hex);
		}

		writer.close();
	}
}