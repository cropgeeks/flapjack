// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.text.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;

public class GenotypeDataExporter extends SimpleJob
{
	private NumberFormat nf = NumberFormat.getInstance();

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


		// Empty tab before the line containing all the markers
		out.write("\t");

		for (int c = 0; c < viewSet.chromosomeCount(); c++)
		{
			GTView view = viewSet.getView(c);

			// Skip any chromosomes that weren't selected
			if (chrm[c] == false)
				continue;

			for (int i = 0; i < view.getMarkerCount(); i++)
				if (useAll || view.isMarkerSelected(i))
					out.write(view.getMarker(i).getName() + "\t");
		}

		out.newLine();


		StateTable stateTable = viewSet.getDataSet().getStateTable();

		// Now write the genotype data...
		// Use the first chromosome to parse the lines
		GTView view = viewSet.getView(0);
		for (int line = 0; line < view.getLineCount(); line++, progress++)
		{
			// Don't export dummy lines
			if (view.isDummyLine(view.getLine(line)))
				continue;

			if (useAll || view.isLineSelected(line))
			{
				out.write(view.getLine(line).getName() + "\t");

				for (int v = 0; v < viewSet.chromosomeCount(); v++)
				{
					GTView cView = viewSet.getView(v);

					// Skip any chromosomes that weren't selected
					if (chrm[v] == false)
						continue;

					cView.cacheLines();

					for (int marker = 0; marker < cView.getMarkerCount() && okToRun; marker++)
					{
						if (useAll || cView.isMarkerSelected(marker))
						{
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