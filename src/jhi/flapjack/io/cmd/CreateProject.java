// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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

	// And the settings used to do this (file paths, etc)
	private CreateProjectSettings options;

	private List<String> output = new ArrayList<>();

	private DataImportSettings importSettings = new DataImportSettings();

	public static void main(String[] args)
	{
		if (useLegacyParser(args) == false)
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
	}

	public CreateProject(CreateProjectSettings options, DataImportSettings importSettings)
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

	private GenotypeDataImporter createGenoImporter(ChromosomeMapImporter mapImporter)
	{
		return new GenotypeDataImporter(
			options.getGenotypes(), dataSet, mapImporter.getMarkersHashMap(),
			importSettings.getMissingData(), importSettings.getHetSep(),
			importSettings.isTransposed(), importSettings.isAllowDuplicates());
	}

	private void createProject()
		throws Exception
	{
		// Read the map
		ChromosomeMapImporter mapImporter =
			new ChromosomeMapImporter(options.getMap(), dataSet);
		mapImporter.importMap();

		// Read the data file (byte storage)
		GenotypeDataImporter genoImporter = createGenoImporter(mapImporter);
		if (genoImporter.importGenotypeDataAsBytes() == false)
		{
			// Or (re)read it using int storage if the first attempt failed
			genoImporter = createGenoImporter(mapImporter);
			genoImporter.importGenotypeDataAsInts();
		}

		if (importSettings.isMakeAllChrom())
			dataSet.createSuperChromosome(RB.getString("io.DataImporter.allChromosomes"));

		PostImportOperations pio = new PostImportOperations(dataSet);
		pio.collapseHomzEncodedAsHet();
		if (importSettings.isCollapseHeteozygotes())
			pio.optimizeStateTable();
		pio.createDefaultView(importSettings.isForceNucScheme());

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

	// This method replicates our old command line parsing code for
	// CreateProject to maintain compatability with older software which made
	// use of this command line tool
	private static boolean useLegacyParser(String[] args)
	{
		File mapFile = null;
		File genotypesFile = null;
		File traitsFile = null;
		File qtlsFile = null;
		FlapjackFile prjFile = null;
		boolean decimalEnglish = false;

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

		if (genotypesFile != null && prjFile != null)
		{
			CreateProjectSettings projectSettings = new CreateProjectSettings(genotypesFile, mapFile, traitsFile, qtlsFile, prjFile, null);
			DataImportSettings importSettings = new DataImportSettings();
			importSettings.setDecimalEnglish(decimalEnglish);

			CreateProject cProj = new CreateProject(projectSettings, importSettings);
			cProj.doProjectCreation();

			System.exit(0);
		}

		return false;
	}
}