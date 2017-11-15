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
	private Integer parent1;
	private Integer parent2;
	private Integer expectedF1;

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
			.addOption("e", "expected-f1", true, "INTEGER", "Optional integer");

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			CreateProjectSettings projectSettings = options.getCreateProjectSettings(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);

			// Required options
			String filename = options.getOutputPath(line);
			Integer parent1 = parseParent(line.getOptionValue("parent1"));
			Integer parent2 = parseParent(line.getOptionValue("parent2"));

			Integer expectedF1 = null;
			// Optional
			if (line.hasOption("expected-f1"))
				expectedF1 = parseParent(line.getOptionValue("expected-f1"));

			GeneratePedVerF1sStats pedVerF1sStats = new GeneratePedVerF1sStats(projectSettings, importSettings, parent1,
				parent2, expectedF1, filename);
			pedVerF1sStats.doStatGeneration();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("GeneratePedVerF1sStats");

			System.exit(1);
		}
	}

	public GeneratePedVerF1sStats(CreateProjectSettings projectSettings, DataImportSettings importSettings, Integer parent1,
		Integer parent2, Integer expectedF1, String filename)
	{
		this.projectSettings = projectSettings;
		this.importSettings = importSettings;
		this.parent1 = parent1;
		this.parent2 = parent2;
		this.expectedF1 = expectedF1;
		this.filename = filename;
	}

	private static Integer parseParent(String parent)
	{
		Integer parentIndex = null;

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

			generateStats();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void generateStats()
		throws Exception
	{
		Integer f1Index = expectedF1;

		GTViewSet viewSet = dataSet.getViewSets().get(0);

		boolean[] chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;

		if (f1Index == null)
		{
			SimulateF1 simF1 = new SimulateF1(viewSet, parent1, parent2);
			simF1.runJob(0);
			f1Index = simF1.getF1Index();
		}

		PedVerF1sAnalysis stats = new PedVerF1sAnalysis(viewSet, chromosomes, parent1, parent2, f1Index);
		stats.runJob(0);

		PedVerF1sTableModel model = new PedVerF1sTableModel(viewSet);
		LineDataTable table = new LineDataTable();

		table.setModel(model);
		table.setViewSet(viewSet);

		LineDataTableExporter exporter = new LineDataTableExporter(
				table, new File(filename), 0, false);
		exporter.runJob(0);
	}
}