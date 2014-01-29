// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
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
	private DataSet dataSet;
	private SimMatrix matrix;

	public PCoAGenerator(DataSet dataSet, SimMatrix matrix)
	{
		this.dataSet = dataSet;
		this.matrix = matrix;
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
				// TODO: Display error
				e.printStackTrace();
			}
		}
	}

	private File writePCoAFile(PCoAResult result, String GID)
		throws Exception
	{
		File pcoaFile = new File(FlapjackUtils.getCacheDir(), GID + ".txt");
		pcoaFile.deleteOnExit();

		BufferedWriter out = new BufferedWriter(new FileWriter(pcoaFile));

		ArrayList<Trait> traits = dataSet.getTraits();
		ArrayList<LineInfo> lineInfos = result.getLineInfos();

		// HEADER ROW
		if (traits.size() > 0)
		{
			// Category labels
			for (Trait trait: traits)
			{
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

			if (traits.size() > 0)
			{
				for (TraitValue tv: line.getTraitValues())
					out.write(tv.formatForCurlyWhirly());
			}
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