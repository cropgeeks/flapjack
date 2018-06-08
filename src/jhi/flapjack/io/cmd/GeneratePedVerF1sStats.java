// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.pedver.*;
import jhi.flapjack.gui.table.*;

import org.apache.commons.cli.*;

import scri.commons.gui.*;

public class GeneratePedVerF1sStats
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private CreateProjectSettings projectSettings;
	private DataImportSettings importSettings;
	private String filename;
	private int parent1;
	private int parent2;
	private int expectedF1;
	private boolean excludeAdditionalParents;

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.withMapFile(true)
			.withQtlFile(false)
			.withTraitFile(false)
			.withOutputPath(true)
			.withProjectFile(false)
			.addRequiredOption("f", "parent1", true, "INTEGER", "Required integer")
			.addRequiredOption("s", "parent2", true, "INTEGER", "Required integer")
			.addOption("e", "expected-f1", true, "INTEGER", "Optional integer")
			.addOption("x", "exclude-additional-parents","Exclude parents which are not the selected recurrent, or donor, parent from the analysis");

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			CreateProjectSettings projectSettings = options.getCreateProjectSettings(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);

			// Required options
			String filename = options.getOutputPath(line);
			int parent1 = parseParent(line.getOptionValue("parent1"));
			int parent2 = parseParent(line.getOptionValue("parent2"));

			int expectedF1 = -1;
			// Optional
			if (line.hasOption("expected-f1"))
				expectedF1 = parseParent(line.getOptionValue("expected-f1"));

			boolean excludeAdditionalParents = line.hasOption("exclude-additional-parents");

			GeneratePedVerF1sStats pedVerF1sStats = new GeneratePedVerF1sStats(projectSettings,
				importSettings, parent1, parent2, expectedF1, excludeAdditionalParents, filename);
			pedVerF1sStats.doStatGeneration();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("GeneratePedVerF1sStats");

			System.exit(1);
		}
	}

	public GeneratePedVerF1sStats(CreateProjectSettings projectSettings, DataImportSettings importSettings, int parent1,
		int parent2, int expectedF1, boolean excludeAdditionalParents, String filename)
	{
		this.projectSettings = projectSettings;
		this.importSettings = importSettings;
		this.parent1 = parent1;
		this.parent2 = parent2;
		this.expectedF1 = expectedF1;
		this.excludeAdditionalParents = excludeAdditionalParents;
		this.filename = filename;
	}

	private static int parseParent(String parent)
	{
		int parentIndex = -1;

		try
		{
			parentIndex = Integer.parseInt(parent) - 1;
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		return parentIndex;
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

		boolean simulateF1 = expectedF1 == -1;

		PedVerF1sAnalysis stats = new PedVerF1sAnalysis(viewSet, chromosomes,
			parent1, parent2, simulateF1, expectedF1, excludeAdditionalParents,
			RB.getString("gui.navpanel.PedVerF1s.node"));

		stats.runJob(0);

		GTViewSet finalViewSet = stats.getViewSet();

		PedVerF1sTableModel model = new PedVerF1sTableModel(finalViewSet);
		LineDataTable table = new LineDataTable();

		table.setModel(model);
		table.setViewSet(finalViewSet);

		LineDataTableExporter exporter = new LineDataTableExporter(
				table, new File(filename), 0, false, false);
		exporter.runJob(0);

		createProject.saveProject();
	}
}