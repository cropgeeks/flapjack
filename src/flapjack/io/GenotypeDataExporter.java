package flapjack.io;

import java.io.*;
import java.text.*;

import flapjack.data.*;

public class GenotypeDataExporter
{
	private File file;
	private GTViewSet viewSet;

	private NumberFormat nf = NumberFormat.getInstance();

	public GenotypeDataExporter(File file, GTViewSet viewSet)
	{
		this.file = file;
		this.viewSet = viewSet;
	}

	public void export(boolean allMarkers, boolean allLines)
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
		for (int line = 0; line < view.getLineCount(); line++)
		{
			System.out.println("line: " + line);

			if (allLines || view.isLineSelected(line))
			{
				out.write(view.getLine(line).getName() + "\t");

				for (int v = 0; v < viewSet.chromosomeCount(); v++)
				{
					GTView cView = viewSet.getView(v);
					cView.cacheLines();

					for (int marker = 0; marker < cView.getMarkerCount(); marker++)
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
}