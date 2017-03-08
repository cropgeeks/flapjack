/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

/**
 * Command-line extension to Flapjack for generating a similairty matrix
 */
public class CreateMatrix
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private File mapFile;
	private File genotypesFile;
	private boolean decimalEnglish = false;
	private String filename;

	public static void main(String[] args)
	{
		CreateMatrix cMatrix = new CreateMatrix(args);
		cMatrix.doMatrixCreation();

		System.exit(0);
	}

	private CreateMatrix(String args[])
	{
		for (String arg : args)
		{
			if (arg.startsWith("-map="))
				mapFile = new File(arg.substring(5));
			if (arg.startsWith("-genotypes="))
				genotypesFile = new File(arg.substring(11));
			if (arg.startsWith("-matrix="))
				filename = arg.substring(8);
			if (arg.startsWith("-decimalEnglish"))
				decimalEnglish = true;
		}

		if (genotypesFile == null || filename == null)
			printHelp();
	}

	public CreateMatrix(File mapFile, File genotypesFile, String filename, boolean decimalEnglish)
	{
		this.mapFile = mapFile;
		this.genotypesFile = genotypesFile;
		this.filename = filename;
		this.decimalEnglish = decimalEnglish;
	}

	public void doMatrixCreation()
	{
		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();

		if (decimalEnglish)
			Locale.setDefault(Locale.UK);

		CreateProject createProject = new CreateProject(mapFile, genotypesFile, null, null, null, false);

//		CreateProject.mapFile = mapFile;
//		CreateProject.genotypesFile = genotypesFile;
//		CreateProject.prjFile = new FlapjackFile("temp");

		try
		{
			createProject.doProjectCreation();
			dataSet = createProject.dataSet();

			CreateSimMatrix();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void CreateSimMatrix()
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

	private static void printHelp()
	{
		System.out.println("Usage: creatematrix <options>\n"
			+ " where valid options are:\n"
			+ "   -map=<map_file>                (optional input file)\n"
			+ "   -genotypes=<genotypes_file>    (required input file)\n"
			+ "   -decimalEnglish                (optional input parameter)\n"
			+ "   -matrix=<matrix_file>          (required output file)\n");

		System.exit(1);
	}
}