// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
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
 * Batch version of CreateProject, using an tab-delimited input file to create
 * a project containing multiple data sets at once.
 */
public class CreateProjectBatch
{
	// Primary input files being read
	private File inputFile;
	private FlapjackFile projectFile;

	// The objects that will (hopefully) get created
	private Project project = new Project();

	private List<String> output = new ArrayList<>();

	private DataImportSettings importSettings = new DataImportSettings();

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withBatchOption()
			.withProjectFile(true);

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			File inputFile = options.getBatchInputFile(line);
			FlapjackFile projectFile = options.getProjectFlapjackFile(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);

			CreateProjectBatch cProj = new CreateProjectBatch(inputFile, projectFile, importSettings);
			cProj.doProjectCreation();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("CreateProjectBatch");

			System.exit(1);
		}
	}

	public CreateProjectBatch(File inputFile, FlapjackFile projectFile, DataImportSettings importSettings)
	{
		this.inputFile = inputFile;
		this.projectFile = projectFile;
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
			openProject(projectFile);

			// Read/loop over the entries in the batch input file
			File baseDir = new File(inputFile.getParent());
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			// Read the header
			String str = in.readLine();

			while ((str = in.readLine()) != null && !str.isEmpty())
			{
				File mapFile = null, genoFile = null, traitsFile = null, qtlFile = null;
				String dataSetName = null;

				String[] tokens = str.split("\t", -1);

				if (!tokens[0].isEmpty())
					mapFile = new File(baseDir, tokens[0]);
				if (!tokens[1].isEmpty())
					genoFile = new File(baseDir, tokens[1]);
				if (!tokens[2].isEmpty())
					traitsFile = new File(baseDir, tokens[2]);
				if (!tokens[3].isEmpty())
					qtlFile = new File(baseDir, tokens[3]);
				if (!tokens[4].isEmpty())
					dataSetName = tokens[4];


				DataSet dataSet = new DataSet();
				createProject(dataSet, mapFile, genoFile, dataSetName);
				importTraits(dataSet, traitsFile);
				importQTLs(dataSet, qtlFile);
			}

			in.close();

			saveProject(projectFile);

			ProjectSerializerDB.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();

			logMessage(e.toString());
			System.exit(1);
		}

		return output;
	}

	private GenotypeDataImporter createGenoImporter(DataSet dataSet, ChromosomeMapImporter mapImporter, File genoFile)
	{
		return new GenotypeDataImporter(
			genoFile, dataSet, mapImporter.getMarkersHashMap(),
			importSettings.getMissingData(), importSettings.getHetSep(),
			importSettings.isTransposed(), importSettings.isAllowDuplicates());
	}

	private void createProject(DataSet dataSet, File mapFile, File genoFile, String dataSetName)
		throws Exception
	{
		// Read the map
		ChromosomeMapImporter mapImporter =
			new ChromosomeMapImporter(mapFile, dataSet);
		mapImporter.importMap();

		// Read the data file (byte storage)
		GenotypeDataImporter genoImporter = createGenoImporter(dataSet, mapImporter, genoFile);
		if (genoImporter.importGenotypeDataAsBytes() == false)
		{
			// Or (re)read it using int storage if the first attempt failed
			genoImporter = createGenoImporter(dataSet, mapImporter, genoFile);
			genoImporter.importGenotypeDataAsInts();
		}

		if (importSettings.isMakeAllChrom())
			dataSet.createSuperChromosome(RB.getString("io.DataImporter.allChromosomes"));

		PostImportOperations pio = new PostImportOperations(dataSet);
		pio.collapseHomzEncodedAsHet();
		if (importSettings.isCollapseHeteozygotes())
			pio.optimizeStateTable();
		pio.createDefaultView(importSettings.isForceNucScheme());

		if (dataSetName != null)
			pio.setName(dataSetName);
		else
			pio.setName(projectFile.getFile());

		project.addDataSet(dataSet);
	}

	private void importTraits(DataSet dataSet, File traitsFile)
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

	private void importQTLs(DataSet dataSet, File qtlFile)
			throws Exception
	{
		if (qtlFile == null)
			return;

		logMessage("Importing QTLs from " + qtlFile);
		QTLImporter importer = new QTLImporter(qtlFile, dataSet);
		importer.runJob(0);

//		QTLTrackOptimiser optimiser = new QTLTrackOptimiser(dataSet);
//		optimiser.optimizeTrackUsage();
	}

	private void openProject(FlapjackFile prjFile)
		throws Exception
	{
		if (prjFile != null && prjFile.exists() && prjFile.getFile().length() > 0)
			project = ProjectSerializer.open(prjFile);
	}

	void saveProject(FlapjackFile prjFile)
		throws Exception
	{
		project.fjFile = prjFile;

		if (ProjectSerializer.save(project))
			logMessage("Project created");
	}

	private void logMessage(String message)
	{
		System.out.println(message);
		output.add(message);
	}
}