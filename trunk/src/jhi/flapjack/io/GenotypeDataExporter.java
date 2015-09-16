// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;


import jhi.flapjack.data.*;
import jhi.flapjack.data.BinnedData.BinData;
import java.io.*;

import scri.commons.gui.*;

public class GenotypeDataExporter extends SimpleJob
{
	private File file;
	private GTViewSet viewSet;
	private boolean useAll;
	private boolean[] chrm;

	public GenotypeDataExporter(File file, GTViewSet viewSet, boolean useAll, boolean[] chrm, int total)
	{
		this.file = file;
		this.viewSet = viewSet;
		this.useAll = useAll;
		this.chrm = chrm;

		maximum = total;
	}

	public void runJob(int index)
		throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		// File header for drag and drop detection
		out.write("# fjFile = GENOTYPE");
		out.newLine();

		// Write any required database header info
		DBAssociation db = viewSet.getDataSet().getDbAssociation();
		if (db.isLineSearchEnabled())
		{
			out.write("# fjDatabaseLineSearch = " + db.getLineSearch());
			out.newLine();
		}
		if (db.isMarkerSearchEnabled())
		{
			out.write("# fjDatabaseMarkerSearch = " + db.getMarkerSearch());
			out.newLine();
		}
		if (db.isLineSearchEnabled() || db.isMarkerSearchEnabled())
			out.newLine();

		BinnedData binnedData = viewSet.getDataSet().getBinnedData();
		if (binnedData.containsBins())
		{
			for (BinData binData : binnedData.getBins())
			{
				out.write("# bin\t" + binData.index + "\t" + binData.min + "\t" + binData.max);
				out.newLine();
			}
		}

		// Empty tab before the line containing all the markers
		out.write("\t");

		for (int c = 0; c < viewSet.chromosomeCount(); c++)
		{
			GTView view = viewSet.getView(c);

			// Skip any chromosomes that weren't selected
			if (chrm != null && chrm[c] == false)
				continue;

			view.cacheLines();

			// Skip any "super" chromosomes
//			if (view.getChromosomeMap().isSpecialChromosome())
//				continue;

			for (int i = 0; i < view.markerCount(); i++)
				if (useAll || view.isMarkerSelected(i))
					if (view.getMarker(i).dummyMarker() == false)
						out.write(view.getMarker(i).getName() + "\t");
		}

		out.newLine();


		StateTable stateTable = viewSet.getDataSet().getStateTable();

		// Now write the genotype data...
		// Use the first chromosome to parse the lines
		GTView view = viewSet.getView(0);
		for (int line = 0; line < view.lineCount(); line++, progress++)
		{
			// Don't export dummy lines, splitter lines, or duplicate lines
			if (view.isDummyLine(line) || view.isSplitter(line) || view.isDuplicate(line))
				continue;

			if (useAll || view.isLineSelected(line))
			{
				out.write(view.getLine(line).getName() + "\t");

				for (int v = 0; v < viewSet.chromosomeCount(); v++)
				{
					GTView cView = viewSet.getView(v);

					// Skip any chromosomes that weren't selected
					if (chrm != null && chrm[v] == false)
						continue;

					// Skip any "super" chromosomes
//					if (view.getChromosomeMap().isSpecialChromosome())
//						continue;

					for (int marker = 0; marker < cView.markerCount() && okToRun; marker++)
					{
						if (useAll || cView.isMarkerSelected(marker))
						{
							if (cView.getMarker(marker).dummyMarker())
								continue;

							int state = cView.getState(line, marker);
							out.write(stateTable.getAlleleState(state) + "\t");
						}
					}
				}

				out.newLine();
			}
		}

		out.close();
	}
}