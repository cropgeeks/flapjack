/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import org.apache.commons.cli.*;

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

	private List<String> output = new ArrayList<>();

	private DataImportSettings importSettings = new DataImportSettings();

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withCommonOptions()
			.withGenotypeFile(true)
			.withProjectFile(true)
			.withMapFile(false)
			.withTraitFile(false)
			.withQtlFile(false);

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			CreateProjectSettings projectSettings = options.getCreateProjectSettings(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);

			CreateProject cProj = new CreateProject(projectSettings, importSettings);
			cProj.doProjectCreation();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("CreateProject");

			System.exit(1);
		}
	}

	CreateProject(CreateProjectSettings options, DataImportSettings importSettings)
	{
		this.mapFile = options.getMap();
		this.genotypesFile = options.getGenotypes();
		this.traitsFile = options.getTraits();
		this.qtlsFile = options.getQtls();
		this.prjFile = options.getProject();

		this.importSettings = importSettings;
	}

	public List<String> doProjectCreation()
	{
		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();
		FlapjackUtils.initialiseSqlite();

		if (importSettings.isDecimalEnglish())
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
			genotypesFile, dataSet, mapImporter.getMarkersHashMap(), importSettings.getMissingData(), importSettings.isUseHetSep(), importSettings.getHetSep(), false);

		genoImporter.importGenotypeData();

		if (importSettings.isMakeAllChrom())
			dataSet.createSuperChromosome(RB.getString("io.DataImporter.allChromosomes"));

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
}