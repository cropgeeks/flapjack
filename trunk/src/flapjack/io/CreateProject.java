package flapjack.io;

import java.io.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

/**
 * Command-line extension to Flapjack that can be used to generate and save a
 * Flapjack-compatible project file if passed a valid map and data file.
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
	private static File prjFile;

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
			if (args[i].startsWith("-project="))
				prjFile = new File(args[i].substring(9));
		}

		if (mapFile == null || genotypesFile == null || prjFile == null)
		{
			System.out.println("Usage: flapjack.io.CreateProject <options>\n"
				+ " where valid options are:\n"
				+ "   -map=<map_file>                (required)\n"
				+ "   -genotypes=<genotypes_file>    (required)\n"
				+ "   -traits=<traits_file>          (optional)\n"
				+ "   -project=<project_file>        (required)");
			return;
		}

		RB.initialize();
		TaskDialog.setIsHeadless();

		try
		{
			createProject();
			importTraits();

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
		GenotypeDataImporter genoImporter =
			new GenotypeDataImporter(genotypesFile, dataSet, "-", true, "/");
		genoImporter.importGenotypeData();

		PostImportOperations pio = new PostImportOperations(dataSet);
		pio.setName(genotypesFile);
		pio.collapseHeterozygotes();
		pio.calculateMarkerFrequencies();
		pio.createDefaultView();

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
		importer.runJob();

		// There'll only be one view for this created project...
		dataSet.getViewSets().get(0).assignTraits();
	}

	private static boolean saveProject()
		throws Exception
	{
		project.filename = prjFile;

		return ProjectSerializer.save(project, true);
	}
}