package flapjack.io;

import java.io.*;
import java.text.*;

import flapjack.data.*;

public class ChromosomeMapExporter
{
	private File file;
	private GTViewSet viewSet;

	private NumberFormat nf = NumberFormat.getInstance();

	public ChromosomeMapExporter(File file, GTViewSet viewSet)
	{
		this.file = file;
		this.viewSet = viewSet;
	}

	public void export(boolean allMarkers)
		throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for (int c = 0; c < viewSet.chromosomeCount(); c++)
		{
			GTView view = viewSet.getView(c);

			for (int i = 0; i < view.getMarkerCount(); i++)
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
}