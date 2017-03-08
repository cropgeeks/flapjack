package jhi.flapjack.io.cmd;

import java.io.*;
import java.util.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.pedver.*;
import jhi.flapjack.gui.table.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class GeneratePedVerF1sStats
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private File mapFile;
	private File genotypesFile;
	private boolean decimalEnglish = false;
	private String filename;
	private Integer parent1;
	private Integer parent2;
	private Integer expectedf1;

	public static void main(String[] args)
	{
		GeneratePedVerF1sStats mabcStats = new GeneratePedVerF1sStats(args);
		mabcStats.doStatGeneration();

		System.exit(0);
	}

	private GeneratePedVerF1sStats(String[] args)
	{
		for (String arg : args)
		{
			if (arg.startsWith("-map="))
				mapFile = new File(arg.substring(5));
			if (arg.startsWith("-genotypes="))
				genotypesFile = new File(arg.substring(11));
			if (arg.startsWith("-parent1="))
				parent1 = parseParent(arg.substring(9));
			if (arg.startsWith("-parent2="))
				parent2 = parseParent(arg.substring(9));
			if (arg.startsWith("-expectedf1="))
				expectedf1 = parseParent(arg.substring(12));
			if (arg.startsWith("-decimalEnglish"))
				decimalEnglish = true;
			if (arg.startsWith("-output="))
				filename = arg.substring(8);
		}

		if (mapFile == null || genotypesFile == null || filename == null ||
			parent1 == null || parent2 == null)
		{
			printHelp();
		}
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

		if (decimalEnglish)
			Locale.setDefault(Locale.UK);

		CreateProject createProject = new CreateProject(mapFile, genotypesFile, null, null, null, false);

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
		Integer f1Index = expectedf1;

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

	private static void printHelp()
	{
		System.out.println("Usage: pedverf1sstats <options>\n"
			+ " where valid options are:\n"
			+ "   -map=<map_file>                (required input file)\n"
			+ "   -genotypes=<genotypes_file>    (required input file)\n"
			+ "   -parent1=<index_of_line>       (required parameter, first line is index 1)\n"
			+ "   -parent2=<index_of_line>       (required parameter, first line is index 1)\n"
			+ "   -expectedf1=<index_of_line>    (optional parameter, first line is index 1)\n"
			+ "   -decimalEnglish                (optional parameter)\n"
			+ "   -output=<output_file>          (required output file)\n");

		System.exit(1);
	}
}