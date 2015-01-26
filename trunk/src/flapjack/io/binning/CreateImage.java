// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binning;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.colors.*;

public class CreateImage
{
	private static IBinner binner;

	public static void main(String[] args)
		throws Exception
	{
		String imageFile = args[0];
		int w = Integer.parseInt(args[1]);
		int h = Integer.parseInt(args[2]);

		String method = args[3].toUpperCase();

		if (method.equals("STANDARD"))
		{
			int numBins = Integer.parseInt(args[4]);
			binner = new StandardBinner(numBins);
		}

		else if (method.equals("SPLIT"))
		{
			int lBinCount = Integer.parseInt(args[4]);
			float split = Float.parseFloat(args[5]);
			int rBinCount = Integer.parseInt(args[6]);

			binner = new SplitBinner(lBinCount, split, rBinCount);
		}

		else if (method.equals("AUTO"))
		{
			int numBins = Integer.parseInt(args[4]);
			String histFile = args[5];

			binner = new AutoBinner(numBins, histFile);
		}

		createImage(imageFile, w, h);
	}

	private static void createImage(String imageFile, int w, int h)
		throws Exception
	{
		// Get the binning information
		ArrayList<float[]> bins = binner.getBinSummary();

		// Fake up a state table that can be used to feed the colour scheme
		StateTable stateTable = new StateTable(0);
		for (int i = 0; i < bins.size(); i++)
			stateTable.getStateCode("" + i, true, "", true, "/");

		Prefs.setColorDefaults();
		BinnedColorScheme colors = new BinnedColorScheme(stateTable, 5, 5);


		// Create an image to draw on
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.createGraphics();


		g.setPaint(new Color(0, 0, 0, 255));

		for (int bin = 0; bin < bins.size(); bin++)
		{
			float f1 = bins.get(bin)[0];
			float f2 = bins.get(bin)[1];

			System.out.println(f1 + " - " + f2);

			// Starting and ending pixels for this bin
			int x1 = Math.round(w*f1);
			int x2 = Math.round(w*f2);

			g.setColor(colors.getColor(bin));
			g.fillRect(x1, 5, x2-x1, h-10);

			g.setColor(Color.black);
			g.drawLine(x1, 5, x1, h-5-1);
		}

		g.setColor(Color.black);
		g.drawRect(0, 5, w-1, h-10-1);

		// Draw the split point if required
		if (binner instanceof SplitBinner)
		{
			float split = ((SplitBinner)binner).getSplit();
			int x = Math.round(w*split);

			g.drawLine(x, 0, x, h-1);
			g.drawLine(x-2, 0, x+2, 0);
			g.drawLine(x-2, h-1, x+2, h-1);
		}

		g.dispose();
		ImageIO.write(image, "png", new File(imageFile));
	}
}