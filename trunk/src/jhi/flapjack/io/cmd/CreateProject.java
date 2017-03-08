/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import java.io.*;
import java.util.*;

import jhi.flapjack.io.*;
import scri.commons.gui.*;

/**
 * Command-line extension to Flapjack that can be used to generate and save a
 * Flapjack-compatible project file if passed a valid map and data file. Also
 * supports phenotype and QTL trait inputs as optional files.
 */
public class CreateProject
{
	// The objects that will (hopefully) get created
	private Project project = new Project();
	private DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private File mapFile;
	private File genotypesFile;
	private File traitsFile;
	private File qtlsFile;
	private FlapjackFile prjFile;
	private boolean decimalEnglish = false;

	private List<String> output = new ArrayList<>();

	public static void main(String[] args)
	{
		CreateProject cProj = new CreateProject(args);
		cProj.doProjectCreation();

		System.exit(0);
	}

	private CreateProject(String[] args)
	{
		for (String arg : args)
		{
			if (arg.startsWith("-map="))
				mapFile = new File(arg.substring(5));
			if (arg.startsWith("-genotypes="))
				genotypesFile = new File(arg.substring(11));
			if (arg.startsWith("-traits="))
				traitsFile = new File(arg.substring(8));
			if (arg.startsWith("-qtls="))
				qtlsFile = new File(arg.substring(6));
			if (arg.startsWith("-project="))
				prjFile = new FlapjackFile(arg.substring(9));
			if (arg.startsWith("-decimalEnglish"))
				decimalEnglish = true;
		}

		if (genotypesFile == null || prjFile == null)
			printHelp();
	}

	/**
	 * Constructor for setting up project creation. Call this, then doProjectCreation() to create a project.
	 *
	 * @param mapFile			The map File object for this project (requried)
	 * @param genotypesFile 	The genotypes File object for this project (required)
	 * @param traitsFile		The traits File object for this project (optional - can be null)
	 * @param qtlsFile			The qtls File object for this project (optional - can be null)
	 * @param decimalEnglish	Whether or not we use English decimal points (required - boolean).
	 */
	public CreateProject(File mapFile, File genotypesFile, File traitsFile, File qtlsFile, FlapjackFile prjFile, boolean decimalEnglish)
	{
		this.mapFile = mapFile;
		this.genotypesFile = genotypesFile;
		this.traitsFile = traitsFile;
		this.qtlsFile = qtlsFile;
		this.decimalEnglish = decimalEnglish;

		this.prjFile = prjFile;
	}

	public List<String> doProjectCreation()
	{
		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();
		FlapjackUtils.initialiseSqlite();

		if (decimalEnglish)
			Locale.setDefault(Locale.UK);

		try
		{
			openProject();
			createProject();
			importTraits();
			importQTLs();

			if (prjFile != null && saveProject())
				logMessage("\nProject Created");

			ProjectSerializerDB.close();
		}
		catch (Exception e)
		{
			logMessage(e.toString());
			System.exit(1);
		}

		return output;
	}

	private void createProject()
		throws Exception
	{
		// Read the map
		ChromosomeMapImporter mapImporter =
			new ChromosomeMapImporter(mapFile, dataSet);
		mapImporter.importMap();

		// Read the data file
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(
			genotypesFile, dataSet, mapImporter.getMarkersHashMap(), "-", true, "/", false);

		genoImporter.importGenotypeData();

		PostImportOperations pio = new PostImportOperations(dataSet);
		pio.collapseHomzEncodedAsHet();
		pio.optimizeStateTable();
		pio.createDefaultView();
		if (prjFile != null)
			pio.setName(prjFile.getFile());

		project.addDataSet(dataSet);
	}

	private void importTraits()
		throws Exception
	{
		// The traits file may be optional
		if (traitsFile == null)
			return;

		logMessage("Importing traits from " + traitsFile);
		TraitImporter importer = new TraitImporter(traitsFile, dataSet);
		importer.runJob(0);

		// There'll only be one view for this created project...
		dataSet.getViewSets().get(0).assignTraits();
	}

	private void importQTLs()
			throws Exception
	{
		if(qtlsFile == null)
			return;

		logMessage("Importing QTLs from " + qtlsFile);
		QTLImporter importer = new QTLImporter(qtlsFile, dataSet);
		importer.runJob(0);

//		QTLTrackOptimiser optimiser = new QTLTrackOptimiser(dataSet);
//		optimiser.optimizeTrackUsage();
	}

	private void openProject()
		throws Exception
	{
		if (prjFile != null && prjFile.exists() && prjFile.getFile().length() > 0)
			project = ProjectSerializer.open(prjFile);
	}

	private boolean saveProject()
		throws Exception
	{
		project.fjFile = prjFile;

		return ProjectSerializer.save(project);
	}

	private void logMessage(String message)
	{
		System.out.println(message);
		output.add(message);
	}

	DataSet dataSet()
	{
		return dataSet;
	}

	private void printHelp()
	{
		System.out.println("Usage: createproject <options>\n"
			+ " where valid options are:\n"
			+ "   -map=<map_file>                (optional input file)\n"
			+ "   -genotypes=<genotypes_file>    (required input file)\n"
			+ "   -traits=<traits_file>          (optional input file)\n"
			+ "   -qtls=<qtl_file>               (optional input file)\n"
			+ "   -decimalEnglish                (optional input parameter)\n"
			+ "   -project=<project_file>        (required output file)\n");

		System.exit(1);
	}
}