// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.forwardbreeding.*;
import jhi.flapjack.gui.table.*;
import org.apache.commons.cli.*;
import scri.commons.gui.*;

import java.io.*;
import java.util.*;

public class GenerateForwardBreedingStats
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private CreateProjectSettings projectSettings;
	private DataImportSettings importSettings;
	private String filename;

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.withMapFile(true)
			.withQtlFile(true)
			.withOutputPath(true)
			.withProjectFile(false);

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			CreateProjectSettings projectSettings = options.getCreateProjectSettings(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);

			// Required options
			String filename = options.getOutputPath(line);

			GenerateForwardBreedingStats pedVerF1sStats = new GenerateForwardBreedingStats(projectSettings,
				importSettings, filename);
			pedVerF1sStats.doStatGeneration();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("GenerateForwardBreedingStats");

			System.exit(1);
		}
	}

	public GenerateForwardBreedingStats(CreateProjectSettings projectSettings, DataImportSettings importSettings, String filename)
	{
		this.projectSettings = projectSettings;
		this.importSettings = importSettings;
		this.filename = filename;
	}

	public void doStatGeneration()
	{
		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();

		if (importSettings.isDecimalEnglish())
			Locale.setDefault(Locale.UK);

		CreateProject createProject = new CreateProject(projectSettings, importSettings);

		try
		{
			createProject.doProjectCreation();
			dataSet = createProject.dataSet();

			generateStats(createProject);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void generateStats(CreateProject createProject)
		throws Exception
	{
		GTViewSet viewSet = dataSet.getViewSets().get(0);

		boolean[] chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;

		FBAnalysis stats = new FBAnalysis(viewSet, chromosomes, RB.getString("gui.navpanel.ForwardBreeding.node"));

		stats.runJob(0);

		GTViewSet finalViewSet = stats.getViewSet();

		FBTableModel model = new FBTableModel(finalViewSet);
		LineDataTable table = new LineDataTable();

		table.setModel(model);
		table.setViewSet(finalViewSet);

		LineDataTableExporter exporter = new LineDataTableExporter(
				table, new File(filename), 0, false, false);
		exporter.runJob(0);

		createProject.saveProject();
	}
}