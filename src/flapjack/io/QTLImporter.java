package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.file.*;

public class QTLImporter implements ITrackableJob
{
	private File file;
	private DataSet dataSet;

	private boolean isOK = true;
	private int total;
	private int count = 2;

	// Store a "track" per chromsome - the QTLs will be added to the appropriate
	// track as they are read
	Hashtable<String, Vector<Feature>> chromosomes = new Hashtable<String, Vector<Feature>>();

	public QTLImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		// Add a storage track to each chromosome
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
			chromosomes.put(c.getName(), new Vector<Feature>());

		try { total = FileUtils.countLines(file, 16384); }
		catch (IOException e) {}
	}

	public void runJob()
		throws IOException, DataFormatException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));

		// Read (and ignore) the header line
		String str = in.readLine();

		for (; (str = in.readLine()) != null && isOK; count++)
		{
			if (str.length() == 0)
				continue;

			String[] tokens = str.split("\t", -1);

			// Fail if the data per line doesn't match the expected number
			if (tokens.length != 10)
				throw new DataFormatException(RB.format("io.DataFormatException.traitColumnError", count));

			// Its name and chromosome
			QTL qtl = new QTL(tokens[0]);
			String cName = tokens[1];

			// Data values
			qtl.setPosition(Float.parseFloat(tokens[2]));
			qtl.setMin(Float.parseFloat(tokens[3]));
			qtl.setMax(Float.parseFloat(tokens[4]));
			qtl.setLod(Float.parseFloat(tokens[5]));
			qtl.setR2(Float.parseFloat(tokens[6]));
			qtl.setMag(Float.parseFloat(tokens[7]));

			// Categorical information
			qtl.setTrait(tokens[8]);
			qtl.setExperiment(tokens[9]);

			// Add this QTL to the correct chromosome's track
			Vector<Feature> track = chromosomes.get(cName);
			if (track != null)
				track.add(qtl);
		}

		in.close();

		// Once the data is in memory, assign the tracks back to the chromosomes
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
		{
			Vector<Vector<Feature>> trackSet = new Vector<Vector<Feature>>();
			trackSet.add(chromosomes.get(c.getName()));

			c.setTrackSet(trackSet);
		}
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