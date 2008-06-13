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
	private static File mapFile;
	private static File datFile;
	private static File prjFile;

	private static Project project = new Project();

	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.out.println("Usage: flapjack.io.CreateProject <mapfile> "
				+ "<datfile> <projectfile>");
			return;
		}

		mapFile = new File(args[0]);
		datFile = new File(args[1]);
		prjFile = new File(args[2]);

		RB.initialize();
		TaskDialog.setIsHeadless();

		try
		{
			createProject();
			if (saveProject())
				System.out.println("Project Created");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	private static void createProject()
		throws Exception
	{
		DataSet dataSet = new DataSet();

		// Read the map
		ChromosomeMapImporter mapImporter =
			new ChromosomeMapImporter(mapFile, dataSet);
		mapImporter.importMap();

		// Read the data file
		GenotypeDataImporter genoImporter =
			new GenotypeDataImporter(datFile, dataSet, "-", "/");
		genoImporter.importGenotypeData();

		PostImportOperations pio = new PostImportOperations(dataSet);
		pio.collapseHeterozygotes();
		pio.calculateMarkerFrequencies();
		pio.createDefaultView();

		project.addDataSet(dataSet);
	}

	private static boolean saveProject()
		throws Exception
	{
		project.filename = prjFile;

		return ProjectSerializer.save(project, true);
	}
}