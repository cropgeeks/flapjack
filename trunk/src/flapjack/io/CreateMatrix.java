// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.analysis.*;
import flapjack.data.*;

import scri.commons.gui.*;

/**
 * Command-line extension to Flapjack for generating a similairty matrix
 */
public class CreateMatrix
{
	// The objects that will (hopefully) get created
	private static DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private static File mapFile;
	private static File genotypesFile;
	private static boolean decimalEnglish = false;
	private static String filename;

	public static void main(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-map="))
				mapFile = new File(args[i].substring(5));
			if (args[i].startsWith("-genotypes="))
				genotypesFile = new File(args[i].substring(11));
			if (args[i].startsWith("-matrix="))
				filename = args[i].substring(8);
			if (args[i].startsWith("-decimalEnglish"))
				decimalEnglish = true;
		}

		if (mapFile == null || genotypesFile == null || filename == null)
		{
			System.out.println("Usage: creatematrix <options>\n"
				+ " where valid options are:\n"
				+ "   -map=<map_file>                (required input file)\n"
				+ "   -genotypes=<genotypes_file>    (required input file)\n"
				+ "   -matrix=<matrix_file>          (required output file)\n"
				+ "   -decimalEnglish                (optional parameter)\n");

			return;
		}

		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();

		if (decimalEnglish)
			Locale.setDefault(Locale.UK);

		CreateProject.mapFile = mapFile;
		CreateProject.genotypesFile = genotypesFile;
		CreateProject.prjFile = new FlapjackFile("temp");

		try
		{
			CreateProject.createProject();
			dataSet = CreateProject.dataSet;

			CreateSimMatrix();
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(1);
		}

		System.exit(0);
	}

	private static void CreateSimMatrix()
		throws Exception
	{
		GTViewSet viewSet = dataSet.getViewSets().get(0);
		GTView view = viewSet.getView(0);

		boolean[] chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;

		CalculateSimilarityMatrix calculator = new CalculateSimilarityMatrix(
			viewSet, view, chromosomes, false);

		calculator.runJob(0);
		SimMatrixExporter exporter = new SimMatrixExporter(calculator.getMatrix(), filename);
		exporter.runJob(0);
	}
}