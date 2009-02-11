package flapjack.io;

import java.io.*;
import java.text.*;

import flapjack.data.*;
import flapjack.gui.*;

public class GenotypeDataExporter implements ITrackableJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private File file;
	private GTViewSet viewSet;
	private boolean allMarkers, allLines;

	// Is it still ok for the export to go ahead?
	private boolean isOK = true;
	// How many lines are going to be processed?
	private int total;
	// How many lines HAVE been processed?
	private int count;

	public GenotypeDataExporter(File file, GTViewSet viewSet, boolean allMarkers, boolean allLines)
	{
		this.file = file;
		this.viewSet = viewSet;
		this.allMarkers = allMarkers;
		this.allLines = allLines;

		total = viewSet.getView(0).getLineCount();
	}

	public void runJob()
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

			for (int i = 0; i < view.getMarkerCount(); i++)
				if (allMarkers || view.isMarkerSelected(i))
					out.write(view.getMarker(i).getName() + "\t");
		}

		out.newLine();


		StateTable stateTable = viewSet.getDataSet().getStateTable();

		// Now write the genotype data...
		// Use the first chromosome to parse the lines
		GTView view = viewSet.getView(0);
		for (int line = 0; line < view.getLineCount(); line++, count++)
		{
			// Don't export dummy lines
			if (view.isDummyLine(view.getLine(line)))
				continue;

			if (allLines || view.isLineSelected(line))
			{
				out.write(view.getLine(line).getName() + "\t");

				for (int v = 0; v < viewSet.chromosomeCount(); v++)
				{
					GTView cView = viewSet.getView(v);
					cView.cacheLines();

					for (int marker = 0; marker < cView.getMarkerCount() && isOK; marker++)
					{
						if (allMarkers || cView.isMarkerSelected(marker))
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

	public boolean isIndeterminate()
		{ return false; }

	public int getMaximum()
		{ return total; }

	public int getValue()
		{ return count; }

	public void cancelJob()
		{ isOK = false; }
}