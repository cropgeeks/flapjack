// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.text.*;

import flapjack.data.*;
import flapjack.gui.*;

public class ChromosomeMapExporter implements ITrackableJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private File file;
	private GTViewSet viewSet;
	private boolean allMarkers;

	// Is it still ok for the export to go ahead?
	private boolean isOK = true;
	// How many markers are going to be processed?
	private int total;
	// How many markers HAVE been processed?
	private int count;


	public ChromosomeMapExporter(File file, GTViewSet viewSet, boolean allMarkers)
	{
		this.file = file;
		this.viewSet = viewSet;
		this.allMarkers = allMarkers;

		total = viewSet.countAllMarkers();
	}

	public void runJob()
		throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		// For each chromosome...
		for (int c = 0; c < viewSet.chromosomeCount(); c++)
		{
			GTView view = viewSet.getView(c);

			// ...and for each marker within the current chromosome...
			for (int i = 0; i < view.getMarkerCount() && isOK; i++, count++)
			{
				if (allMarkers || view.isMarkerSelected(i))
				{
					out.write(view.getMarker(i).getName() + "\t"
						+ view.getChromosomeMap().getName() + "\t"
						+ nf.format(view.getMarker(i).getPosition()));
					out.newLine();
				}
			}
		}

		out.close();
	}

	public boolean isIndeterminate()
		{ return false; }

	public int getMaximum()
		{ return total; }

	public int getValue()
		{ return count; }

	public void cancelJob()
		{ isOK = false; }
}