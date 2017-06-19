/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import java.io.*;
import java.text.*;
import java.util.*;

import jhi.flapjack.io.*;

import scri.commons.io.*;

public class GobiiMabcConverter
{
	private static File wrkDir;
	private static NumberFormat nf = NumberFormat.getInstance();

	// Tracks all the markers we've loaded
	private ArrayList<Marker> markers = new ArrayList<>();

	public static void main(String args[])
		throws Exception
	{
		// Working directory for temp files, etc
		wrkDir = FileUtils.getTempUserDirectory("jhi-flapjack");
		wrkDir = new File(wrkDir, "GobiiMabcConverter");
		wrkDir.mkdirs();

		GobiiMabcConverter converter = new GobiiMabcConverter();

		// Read/create the markers
		converter.createMap(new File(args[0]));
		// Read/create the genotypes
		converter.createGenotypes(new File(args[1]));
		// Read/create the qtls
		converter.createQTL(new File(args[2]));

		CreateProjectSettings projectSettings = new CreateProjectSettings(
			new File(wrkDir, "map"),
			new File(wrkDir, "geno"),
			null,
			new File(wrkDir, "qtl"),
			new FlapjackFile(args[3]),
			null);

		DataImportSettings importSettings = new DataImportSettings();
		importSettings.setDecimalEnglish(true);

		// Make a Flapjack project
		CreateProject cp = new CreateProject(projectSettings, importSettings);

		cp.doProjectCreation();
	}

	private void createMap(File inFile)
		throws Exception
	{
		// Read in the data from the markerpositions.csv file
		BufferedReader in = new BufferedReader(new FileReader(inFile));

		// Chromosomes
		String[] chrms = in.readLine().split(",");
		// Marker positions
		String[] mrkrs = in.readLine().split(",");
		in.close();


		// Now rewrite it in Flapjack map format
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(wrkDir, "map")));
		out.write("# fjFile = MAP");
		out.newLine();

		for (int i = 0; i < chrms.length; i++)
		{
			Marker m = new Marker(i, chrms[i], mrkrs[i]);
			markers.add(m);

			out.write(m.name + "\t" + m.chr + "\t" + m.pos);
			out.newLine();
		}
		out.close();

		markers.stream().forEach(marker -> {

			System.out.println(marker.name);

		});
	}

	private void createGenotypes(File inFile)
		throws Exception
	{
		// Read in the data from the genotypes.csv file
		BufferedReader in = new BufferedReader(new FileReader(inFile));
		// And write back out in Flapjack format
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(wrkDir, "geno")));
		out.write("# fjFile = GENOTYPE");
		out.newLine();

		// Skip the first two lines; we'll just assume marker order...
		in.readLine(); in.readLine();
		// ...and use the data we already have from the map
		for (Marker m: markers)
			out.write("\t" + m.name);
		out.newLine();


		// Now process each line
		int index = 0;
		String str = null;
		while ((str = in.readLine()) != null)
		{
			if (str.isEmpty())
				continue;

			if (index == 0)
				out.write("RP");
			else if (index == 1)
				out.write("DP");
			else
				out.write("RP[1]/DP-" + index);

			for (String allele: str.split(","))
				out.write("\t" + allele);

			out.newLine();
			index++;
		}

		in.close();
		out.close();
	}

	private void createQTL(File inFile)
		throws Exception
	{
		// Read in the data from the qtlregions.csv file
		BufferedReader in = new BufferedReader(new FileReader(inFile));
		// And write back out in Flapjack format
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(wrkDir, "qtl")));

		out.write("# fjFile = QTL");
		out.newLine();
		out.write("Name\tChromosome\tPosition\tPos-Min\tPos-Max\tTrait\tExperiment\tSource");
		out.newLine();

		// Skip the first line
		in.readLine();

		// Every other line should be a QTL
		String str = null;
		while ((str = in.readLine()) != null)
		{
			if (str.isEmpty())
				continue;

			String[] tokens = str.split(",");

			// LeftFlankingMarker
			Marker lfm = markers.get(Integer.parseInt(tokens[1])-1);
			// RightFlankingMarker
			Marker rfm = markers.get(Integer.parseInt(tokens[2])-1);

			// Name
			out.write(tokens[0] + "\t");
			// Chromosome
			out.write(lfm.chr + "\t");
			// Position (mid point of the two QTL we know)
			out.write((lfm.pos + ((rfm.pos-lfm.pos)/2f)) + "\t");
			// LFM position
			out.write(lfm.pos + "\t");
			// RFM position
			out.write(rfm.pos + "\t");
			// Trait/Experiment
			out.write("TRAIT\tEXPERIMENT\t");
			// Source
			out.write(tokens[3]);

			out.newLine();
		}

		in.close();
		out.close();
	}

	private static class Marker
	{
		String name, chr;
		float pos;

		Marker(int index, String chr, String pos)
			throws Exception
		{
			name = "m" + (index+1);
			this.chr = chr;
			this.pos = nf.parse(pos).floatValue();
		}
	}
}