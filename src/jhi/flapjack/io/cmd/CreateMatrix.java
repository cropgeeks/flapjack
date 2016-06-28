/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;

import jhi.flapjack.io.FlapjackFile;
import jhi.flapjack.io.SimMatrixExporter;
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
		File mapFile = null;
		File genotypesFile = null;
		String filename = null;
		boolean decimalEnglish = false;

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

		if (genotypesFile == null || filename == null)
		{
			System.out.println("Usage: creatematrix <options>\n"
				+ " where valid options are:\n"
				+ "   -map=<map_file>                (optional input file)\n"
				+ "   -genotypes=<genotypes_file>    (required input file)\n"
				+ "   -matrix=<matrix_file>          (required output file)\n"
				+ "   -decimalEnglish                (optional parameter)\n");

			return;
		}

		CreateMatrix cMatrix = new CreateMatrix(mapFile, genotypesFile, filename, decimalEnglish);
		cMatrix.doMatrixCreation();

		System.exit(0);
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

		CreateProject createProject = new CreateProject(mapFile, genotypesFile, null, null, new FlapjackFile("temp"), false);

//		CreateProject.mapFile = mapFile;
//		CreateProject.genotypesFile = genotypesFile;
//		CreateProject.prjFile = new FlapjackFile("temp");

		try
		{
			createProject.createProject();
			dataSet = createProject.dataSet();

			CreateSimMatrix();
		}
		catch (Exception e)
		{
			System.out.println(e);
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
}