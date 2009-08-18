package flapjack.io;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class QTLImporter implements ITrackableJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private File file;
	private DataSet dataSet;

	private boolean isOK = true;
	private int total;
	private int count = 2;

	// Number of expected KNOWN headers in imported file
	private static final int HEADERCOUNT = 7;

	// Store a "track" per chromsome - the QTLs will be added to the appropriate
	// track as they are read
	Hashtable<String, Vector<Feature>> chromosomes = new Hashtable<String, Vector<Feature>>();
	// And they'll also be added to this for easy reference
	LinkedList<QTL> qtls = new LinkedList<QTL>();

	// Store references to each trait, so colors can be assigned post-import
	Hashtable<String, Color> traits = new Hashtable<String, Color>();

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
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(file));

		// Read and process the header line
		String str = in.readLine();
		String[] tokens = str.split("\t", -1);

		// Work out how many addition "data score" headers there are
		String[] scoreHeaders = new String[tokens.length-HEADERCOUNT];
		for (int i = HEADERCOUNT; i < tokens.length; i++)
			scoreHeaders[i-HEADERCOUNT] = new String(tokens[i]);


		// Now process the main batch of lines
		for (; (str = in.readLine()) != null && isOK; count++)
		{
			if (str.length() == 0)
				continue;

			tokens = str.split("\t", -1);

			// Fail if the data per line doesn't match the expected number
			if (tokens.length != HEADERCOUNT + scoreHeaders.length)
				throw new DataFormatException(RB.format("io.DataFormatException.traitColumnError", count));

			// Its name and chromosome
			QTL qtl = new QTL(new String(tokens[0]));
			String cName = tokens[1];

			// Position values
			qtl.setPosition(nf.parse(tokens[2]).floatValue());
			qtl.setMin(nf.parse(tokens[3]).floatValue());
			qtl.setMax(nf.parse(tokens[4]).floatValue());

			// Categorical information
			qtl.setTrait(tokens[5]);
			qtl.setExperiment(tokens[6]);
			traits.put(tokens[5], Color.white);

			// Zero or more score "values"
			String[] vNames = new String[scoreHeaders.length];
			String[] values = new String[scoreHeaders.length];

			for (int i = 0; i < values.length; i++)
			{
				vNames[i] = scoreHeaders[i];
				values[i] = new String(tokens[i+HEADERCOUNT]);
			}

			qtl.setVNames(vNames);
			qtl.setValues(values);

			// Add this QTL to the correct chromosome's track
			Vector<Feature> track = chromosomes.get(cName);
			if (track != null)
			{
				track.add(qtl);
				checkChromosome(qtl, cName);
			}

			qtls.add(qtl);
		}

		in.close();

		// Quit before applying to the dataset if the user cancelled...
		if (isOK == false)
			return;

		// Work out a colors for the traits and QTLs
		calculateTraitColors();

		// Finally, assign the QTL tracks to the chromosomes
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
		{
			// Sort the QTLs into map order
			Vector<Feature> track = chromosomes.get(c.getName());
			Collections.sort(track);

			// Then add the track to a new trackset and set to the chromosome
			Vector<Vector<Feature>> trackSet = new Vector<Vector<Feature>>();
			trackSet.add(track);
			c.setTrackSet(trackSet);
		}
	}

	private void calculateTraitColors()
	{
		// Work out colors for each trait type
		float colorCount = traits.size();
		float hue = 0;

		Enumeration<String> keys = traits.keys();
		for (int i = 0; keys.hasMoreElements(); i++)
		{
			Color color = Color.getHSBColor(hue, 0.4f, 1);
			hue += 1/colorCount;

			traits.put(keys.nextElement(), color);
		}

		// Assign every QTL a colour
		for (QTL qtl: qtls)
		{
			String traitName = qtl.getTrait();
			Color color = traits.get(traitName);

			qtl.setDisplayColor(color);
		}
	}

	// Adds a QTL to its chromosome, but also checks if it is on the map itself
	private void checkChromosome(QTL qtl, String cName)
	{
		ChromosomeMap cMap = dataSet.getMapByName(cName, false);

		if (qtl.getMin() < 0 || qtl.getMax() > cMap.getLength() ||
			qtl.getMax() < 0 || qtl.getMin() > cMap.getLength() ||
			qtl.getPosition() < 0 || qtl.getPosition() > cMap.getLength())
		{
			qtl.setAllowed(false);
			qtl.setVisible(false);
		}

		qtl.setChromosomeMap(cMap);
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