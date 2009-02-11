package flapjack.io;

import java.io.*;

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

		// Remove duplicate allele states
		CollapseHeterozygotes c = new CollapseHeterozygotes(dataSet);
		c.collapse();

		// Read the data file
		GenotypeDataImporter genoImporter =
			new GenotypeDataImporter(datFile, dataSet, "-", "/");
		genoImporter.importGenotypeData();


		// Set a default view; name the dataset; and add to the project
		GTViewSet viewSet = new GTViewSet(dataSet, "Default View");
		dataSet.getViewSets().add(viewSet);
		dataSet.setName(getDataSetName(dataSet, datFile.getName()));

		project.addDataSet(dataSet);
	}

	private static String getDataSetName(DataSet dataSet, String name)
	{
		if (name.lastIndexOf(".") != -1)
			name = name.substring(0, name.lastIndexOf("."));
		name += " " + dataSet.countLines() + "x" + dataSet.countMarkers();

		return name;
	}

	private static boolean saveProject()
		throws Exception
	{
		project.filename = prjFile;

		return ProjectSerializer.save(project, true);
	}
}