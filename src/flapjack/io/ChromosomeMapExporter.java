// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.text.*;

import flapjack.data.*;

import scri.commons.gui.*;

public class ChromosomeMapExporter extends SimpleJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private File file;
	private GTViewSet viewSet;
	private boolean useAll;
	private boolean[] chrm;

	public ChromosomeMapExporter(File file, GTViewSet viewSet, boolean useAll, boolean[] chrm, int total)
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
		out.write("# fjFile = MAP");
		out.newLine();

		// For each chromosome...
		for (int c = 0; c < viewSet.chromosomeCount(); c++)
		{
			GTView view = viewSet.getView(c);

			// Skip any chromosomes that weren't selected
			if (chrm[c] == false)
				continue;

			// ...and for each marker within the current chromosome...
			for (int i = 0; i < view.markerCount() && okToRun; i++, progress++)
			{
				if (useAll || view.isMarkerSelected(i))
				{
					if (view.getMarker(i).dummyMarker() == false)
					{
						out.write(view.getMarker(i).getName() + "\t"
							+ view.getChromosomeMap().getName() + "\t"
							+ nf.format(view.getMarker(i).getPosition()));
						out.newLine();
					}
				}
			}
		}

		out.close();
	}
}