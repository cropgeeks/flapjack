/*
 * // Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
 * // reserved. Use is subject to the accompanying licence terms.
 */

package jhi.flapjack.io.cmd;

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

	// And the settings used to do this (file paths, etc)
	private CreateProjectSettings options;

	private List<String> output = new ArrayList<>();

	private DataImportSettings importSettings = new DataImportSettings();

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.withProjectFile(true)
			.withMapFile(false)
			.withTraitFile(false)
			.withQtlFile(false)
			.withDataSetName(false);

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
		this.options = options;
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

			saveProject();

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
			new ChromosomeMapImporter(options.getMap(), dataSet);
		mapImporter.importMap();

		// Read the data file
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(
			options.getGenotypes(), dataSet, mapImporter.getMarkersHashMap(),
			importSettings.getMissingData(), importSettings.getHetSep(),
			importSettings.isTransposed());

		genoImporter.importGenotypeData();

		if (importSettings.isMakeAllChrom())
			dataSet.createSuperChromosome(RB.getString("io.DataImporter.allChromosomes"));

		PostImportOperations pio = new PostImportOperations(dataSet);
		pio.collapseHomzEncodedAsHet();
		if (importSettings.isCollapseHeteozygotes())
			pio.optimizeStateTable();
		pio.createDefaultView();

		if (options.getDatasetName() != null)
			pio.setName(options.getDatasetName());
		else if (options.getProject() != null)
			pio.setName(options.getProject().getFile());

		project.addDataSet(dataSet);
	}

	private void importTraits()
		throws Exception
	{
		// The traits file may be optional
		if (options.getTraits() == null)
			return;

		logMessage("Importing traits from " + options.getTraits());
		TraitImporter importer = new TraitImporter(options.getTraits(), dataSet);
		importer.runJob(0);

		// There'll only be one view for this created project...
		dataSet.getViewSets().get(0).assignTraits();
	}

	private void importQTLs()
			throws Exception
	{
		if (options.getQtls() == null)
			return;

		logMessage("Importing QTLs from " + options.getQtls());
		QTLImporter importer = new QTLImporter(options.getQtls(), dataSet);
		importer.runJob(0);

//		QTLTrackOptimiser optimiser = new QTLTrackOptimiser(dataSet);
//		optimiser.optimizeTrackUsage();
	}

	private void openProject()
		throws Exception
	{
		FlapjackFile prjFile = options.getProject();

		if (prjFile != null && prjFile.exists() && prjFile.getFile().length() > 0)
			project = ProjectSerializer.open(prjFile);
	}

	void saveProject()
		throws Exception
	{
		if (options.getProject() != null)
		{
			project.fjFile = options.getProject();

			if (ProjectSerializer.save(project))
				logMessage("Project created");
		}
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