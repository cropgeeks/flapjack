// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.awt.*;
import java.io.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import jhi.flapjack.servlet.pcoa.*;

import scri.commons.gui.*;

public class PCoAGenerator extends SimpleJob
{
	private GTViewSet viewSet;
	private DataSet dataSet;
	private SimMatrix matrix;

	private PCoAClient client;

	private boolean callingCurlyWhirly = false;

	public PCoAGenerator(GTViewSet viewSet, SimMatrix matrix)
	{
		this.viewSet = viewSet;
		this.matrix = matrix;

		dataSet = viewSet.getDataSet();
	}

	@Override
	public String getMessage()
	{
		if (callingCurlyWhirly)
			return RB.getString("analysis.PCoAGenerator.cwMessage");
		else
			return null;
	}

	public void runJob(int index)
		throws Exception
	{
		// Run the servlet (upload, run, download)
		client = new PCoAClient();
		PCoAResult result = client.generatePco(matrix);

		if (okToRun)
		{
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

					callingCurlyWhirly = true;
					Thread.sleep(5000);
				}
				// Windows 10 (and 8?) pops up its stupid "how do you want to
				// handle this app" dialog, which doesn't cause an exception
				catch (Exception e)
				{
					e.printStackTrace();

					//
					TaskDialog.error(
						RB.format("analysis.PCoAGenerator.cwError",
						cwFile.getPath()), RB.getString("gui.text.close"));
				}
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

		// Write any required database header info
		DBAssociation db = dataSet.getDbAssociation();
		if (db.isLineSearchEnabled())
		{
			out.write("# cwDatabaseLineSearch=" + db.getLineSearch());
			out.newLine();
		}
		if (db.isGroupPreivewEnabled())
		{
			out.write("# cwDatabaseGroupPreview=" + db.getGroupPreview());
			out.newLine();
		}
		if (db.isGroupUploadEnabled())
		{
			out.write("# cwDatabaseGroupUpload=" + db.getGroupUpload());
			out.newLine();
		}
		if (db.isLineSearchEnabled() || db.isGroupPreivewEnabled() || db.isGroupUploadEnabled())
			out.newLine();

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

	public void cancelJob()
	{
		super.cancelJob();
		client.cancelJob();
	}
}