// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

/**
 * Command-line extension to Flapjack that can be used to generate and save a
 * Flapjack-compatible project file if passed a valid map and data file. Also
 * supports phenotype and QTL trait inputs as optional files.
 */
public class CreateProject
{
	// The objects that will (hopefully) get created
	private static Project project = new Project();
	private static DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private static File mapFile;
	private static File genotypesFile;
	private static File traitsFile;
	private static File qtlsFile;
	private static FlapjackFile prjFile;
	private static String name;

	public static void main(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-map="))
				mapFile = new File(args[i].substring(5));
			if (args[i].startsWith("-genotypes="))
				genotypesFile = new File(args[i].substring(11));
			if (args[i].startsWith("-traits="))
				traitsFile = new File(args[i].substring(8));
			if (args[i].startsWith("-qtls="))
				qtlsFile = new File(args[i].substring(6));
			if (args[i].startsWith("-project="))
				prjFile = new FlapjackFile(args[i].substring(9));
			if (args[i].startsWith("-datasetname="))
				name = args[i].substring(13);
		}

		if (mapFile == null || genotypesFile == null || prjFile == null)
		{
			System.out.println("Usage: flapjack.io.CreateProject <options>\n"
				+ " where valid options are:\n"
				+ "   -map=<map_file>                (required)\n"
				+ "   -genotypes=<genotypes_file>    (required)\n"
				+ "   -traits=<traits_file>          (optional)\n"
				+ "   -qtls=<qtl_file>               (optional)\n"
				+ "   -project=<project_file>        (required)\n"
				+ "   -datasetname=<datasetname>     (optional)\n");

			return;
		}

		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();

		try
		{
			openProject();
			createProject();
			importTraits();
			importQTLs();

			if (saveProject())
				System.out.println("\nProject Created");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	private static void createProject()
		throws Exception
	{
		// Read the map
		ChromosomeMapImporter mapImporter =
			new ChromosomeMapImporter(mapFile, dataSet);
		mapImporter.importMap();

		// Read the data file
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(
			genotypesFile, dataSet, mapImporter.getMarkersHashMap(), "-", true, "/");

		genoImporter.importGenotypeData(false);

		PostImportOperations pio = new PostImportOperations(dataSet);
		pio.collapseHeterozygotes();
		pio.createDefaultView();

		// A custom name...
		if (name != null)
			pio.setName(name);
		// Or just the name of the project file being created
		else
			pio.setName(prjFile.getFile());

		project.addDataSet(dataSet);
	}

	private static void importTraits()
		throws Exception
	{
		// The traits file may be optional
		if (traitsFile == null)
			return;

		System.out.println("Importing traits from " + traitsFile);
		TraitImporter importer = new TraitImporter(traitsFile, dataSet);
		importer.runJob(0);

		// There'll only be one view for this created project...
		dataSet.getViewSets().get(0).assignTraits();
	}

	private static void importQTLs()
			throws Exception
	{
		if(qtlsFile == null)
			return;

		System.out.println("Importing QTLs from " + qtlsFile);
		QTLImporter importer = new QTLImporter(qtlsFile, dataSet);
		importer.runJob(0);

//		QTLTrackOptimiser optimiser = new QTLTrackOptimiser(dataSet);
//		optimiser.optimizeTrackUsage();
	}

	private static void openProject()
		throws Exception
	{
		if (prjFile.exists())
			project = ProjectSerializer.open(prjFile);
	}

	private static boolean saveProject()
		throws Exception
	{
		project.fjFile = prjFile;
		project.format = ProjectSerializer.XMLZ;

		return ProjectSerializer.save(project);
	}
}