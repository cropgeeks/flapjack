// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.awt.*;
import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.servlet.*;

import scri.commons.gui.*;

public class PCoAGenerator extends SimpleJob
{
	private GTViewSet viewSet;
	private DataSet dataSet;
	private SimMatrix matrix;

	public PCoAGenerator(GTViewSet viewSet, SimMatrix matrix)
	{
		this.viewSet = viewSet;
		this.matrix = matrix;

		dataSet = viewSet.getDataSet();
	}

	public void runJob(int index)
		throws Exception
	{
		PCoAResult result = new PCoAResult(matrix.getLineInfos());

		// Run the servlet (upload, run, download)
		PCoAClient client = new PCoAClient();
		client.doClientStuff(matrix, result);


		// Save the result (and a .curlywhirly file) to temp
		String GID = SystemUtils.createGUID(12);

		File pcoaFile = writePCoAFile(result, GID);
		File cwFile = writeCurlyWhirlyFile(pcoaFile, GID);


		// Now try to open CurlyWhirly to display the result
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();

			try
			{
				desktop.open(cwFile);
			}
			catch (Exception e)
			{
				e.printStackTrace();

				TaskDialog.error("CurlyWhirly is required to view these results, but it couldn't be found on your system.\n"
					+ "Please download it from http://ics.hutton.ac.uk/curlywhirly\n\n"
					+ "For reference, the results file was saved to " + cwFile.getPath(),
					RB.getString("gui.text.close"));
			}
		}
	}

	private File writePCoAFile(PCoAResult result, String GID)
		throws Exception
	{
		File pcoaFile = new File(FlapjackUtils.getCacheDir(), GID + ".txt");
		pcoaFile.deleteOnExit();

		BufferedWriter out = new BufferedWriter(new FileWriter(pcoaFile));

		int[] selectedTraits = viewSet.getTraits();
		ArrayList<Trait> traits = dataSet.getTraits();
		ArrayList<LineInfo> lineInfos = result.getLineInfos();

		// HEADER ROW
// Just send the selected traits
//		if (traits.size() > 0)
// Or send all the traits
		if (selectedTraits.length > 0)
		{
			// Category labels
// Just send the selected traits
//			for (Trait trait: traits)
// Or send all the traits
			for (int i = 0; i < selectedTraits.length; i++)
			{
				Trait trait = traits.get(selectedTraits[i]);

				out.write("categories:" + trait.getName());
				out.write("\t");
			}
		}
		else
			out.write("categories:\t");

		out.write("label");

		// Axis (P1, P2, P3, etc)
		int count = result.getData().get(0).length;
		for (int i = 0; i < count; i++)
			out.write("\tP" + (i+1));

		out.newLine();
		// END HEADER ROW


		// LINE DATA (one row per line)
		for (int i = 0; i < lineInfos.size(); i++)
		{
			LineInfo lineInfo = lineInfos.get(i);
			Line line = lineInfo.getLine();

// Just send the selected traits
			if (selectedTraits.length > 0)
			{
				for (int t = 0; t < selectedTraits.length; t++)
				{
					TraitValue tv = line.getTraitValues().get(selectedTraits[t]);
					out.write(tv.formatForCurlyWhirly());
				}
			}
// Or send all the traits
//			if (traits.size() > 0)
//			{
//				for (TraitValue tv: line.getTraitValues())
//					out.write(tv.formatForCurlyWhirly());
//			}
			else
				out.write("\t");

			// Name
			out.write(lineInfo.name());

			for (float data: result.getData().get(i))
				out.write("\t" + data);

			out.newLine();
		}


		out.close();

		return pcoaFile;
	}

	private File writeCurlyWhirlyFile(File pcoaFile, String GID)
		throws Exception
	{
		File cwFile = new File(FlapjackUtils.getCacheDir(), GID + ".curlywhirly");
		cwFile.deleteOnExit();

		BufferedWriter out = new BufferedWriter(new FileWriter(cwFile));
		out.write("<curlywhirly>");
		out.newLine();
		out.write("\t<datafile>" + pcoaFile.getPath() + "</datafile>");
		out.newLine();
		out.write("</curlywhirly>");
		out.close();

		return cwFile;
	}
}