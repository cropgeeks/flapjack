package jhi.flapjack.io.cmd;

import java.io.*;
import java.text.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.mabc.*;
import jhi.flapjack.gui.table.*;

import org.apache.commons.cli.*;

import scri.commons.gui.*;

public class GenerateMabcStats
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	private CreateProjectSettings projectSettings;
	private DataImportSettings importSettings;

	private double maxMarkerCoverage = 10;
	private String filename;
	private boolean excludeAdditionalParents = false;
	private boolean unweighted = false;
	private Integer parent1;
	private Integer parent2;

	public static void main(String[] args)
	{
		NumberFormat nf = NumberFormat.getInstance();
		String modelType;

		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.withMapFile(true)
			.withQtlFile(true)
			.withOutputPath(true)
			.withProjectFile(false)
			.withTraitFile(false)
			.addRequiredOption("r", "recurrent-parent", true, "INTEGER", "Index of parent in file")
			.addRequiredOption("d", "donor-parent", true, "INTEGER", "Index of parent in file")
			.addOption(null, "model", true, "ARG", "weighted|unweighted")
			.addOption("c", "max-marker-coverage", true, "FLOATING POINT NUMBER", "Maximum coverage a marker can " +
				"have in the weighted model")
			.addOption("e", "exclude-additional-parents","Exclude parents which are not the selected recurrent, or donor, parent from the analysis");

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			CreateProjectSettings projectSettings = options.getCreateProjectSettings(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);
			// Uncommon required vars
			String filename = options.getOutputPath(line);
			Integer parent1 = parseParent(line.getOptionValue("recurrent-parent"));
			Integer parent2 = parseParent(line.getOptionValue("donor-parent"));
			// Optional vars
			modelType = line.getOptionValue("model");
			boolean unweighted = modelType != null && modelType.equals("unweighted");

			double maxMarkerCoverage = 10;
			if (line.hasOption("max-marker-coverage"))
				maxMarkerCoverage = nf.parse(line.getOptionValue("max-marker-coverage")).doubleValue();

			boolean excludeAdditionalParents = line.hasOption("exclude-additional-parents");

			GenerateMabcStats mabcStats = new GenerateMabcStats(projectSettings,
				importSettings, parent1, parent2, excludeAdditionalParents,
				unweighted, maxMarkerCoverage, filename);
			mabcStats.doStatGeneration();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("GenerateMabcStats");

			System.exit(1);
		}
	}

	private GenerateMabcStats(CreateProjectSettings projectSettings, DataImportSettings importSettings, Integer parent1,
		Integer parent2, boolean excludeAdditionalParents, boolean unweighted, double maxMarkerCoverage, String filename)
	{
		this.projectSettings = projectSettings;
		this.importSettings = importSettings;
		this.parent1 = parent1;
		this.parent2 = parent2;
		this.excludeAdditionalParents = excludeAdditionalParents;
		this.unweighted = unweighted;
		this.maxMarkerCoverage = maxMarkerCoverage;
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

		CreateProject createProject = new CreateProject(projectSettings, importSettings);

		try
		{
			createProject.doProjectCreation();
			dataSet = createProject.dataSet();

			generateMabcStats(createProject);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void generateMabcStats(CreateProject createProject)
		throws Exception
	{
		GTViewSet viewSet = dataSet.getViewSets().get(0);

		boolean[] chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;

		MabcAnalysis stats = new MabcAnalysis(viewSet, chromosomes,
			maxMarkerCoverage, parent1, parent2, excludeAdditionalParents,
			unweighted, RB.getString("gui.navpanel.MabcNode.node"));
		stats.runJob(0);

		// TODO: Can we tidy up the code below by getting at the table in the
		// viewset generated as part of the analysis
		GTViewSet finalViewSet = stats.getViewSet();

		MabcTableModel model = new MabcTableModel(finalViewSet);
		LineDataTable table = new LineDataTable();

		table.setModel(model);
		table.setViewSet(finalViewSet);

		LineDataTableExporter exporter = new LineDataTableExporter(
			table, new File(filename), 0, false);
		exporter.runJob(0);

		createProject.saveProject();
	}
}