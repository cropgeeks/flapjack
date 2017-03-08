package jhi.flapjack.io.cmd;

import java.io.*;
import java.text.*;
import java.util.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.mabc.*;
import jhi.flapjack.gui.table.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class GenerateMabcStats
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private File mapFile;
	private File genotypesFile;
	private File qtlFile;
	private double maxMarkerCoverage = 10;
	private boolean decimalEnglish = false;
	private String filename;
	private boolean unweighted = false;
	private Integer parent1;
	private Integer parent2;

	public static void main(String[] args)
	{
		GenerateMabcStats mabcStats = new GenerateMabcStats(args);
		mabcStats.doStatGeneration();

		System.exit(0);
	}

	private GenerateMabcStats(String[] args)
	{
		NumberFormat nf = NumberFormat.getInstance();
		String modelType;

		for (String arg : args)
		{
			if (arg.startsWith("-map="))
				mapFile = new File(arg.substring(5));
			if (arg.startsWith("-genotypes="))
				genotypesFile = new File(arg.substring(11));
			if (arg.startsWith("-qtls="))
				qtlFile = new File(arg.substring(6));
			if (arg.startsWith("-parent1="))
				parent1 = parseParent(arg.substring(9));
			if (arg.startsWith("-parent2="))
				parent2 = parseParent(arg.substring(9));
			if (arg.startsWith("-decimalEnglish"))
				decimalEnglish = true;
			if (arg.startsWith("-output="))
				filename = arg.substring(8);
			if (arg.startsWith("-model="))
			{
				modelType = arg.substring(7);
				if (modelType.equals("unweighted"))
					unweighted = true;
			}
			if (arg.startsWith("-coverage="))
			{
				try
				{
					maxMarkerCoverage = nf.parse(arg.substring(10)).doubleValue();
				}
				catch (ParseException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

		if (mapFile == null || genotypesFile == null || qtlFile == null ||
			filename == null || parent1 == null || parent2 == null)
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

		CreateProject createProject = new CreateProject(mapFile, genotypesFile, null, qtlFile, null, false);

		try
		{
			createProject.doProjectCreation();
			dataSet = createProject.dataSet();

			generateMabcStats();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void generateMabcStats()
		throws Exception
	{
		GTViewSet viewSet = dataSet.getViewSets().get(0);

		boolean[] chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;

		MabcAnalysis stats = new MabcAnalysis(viewSet, chromosomes, maxMarkerCoverage, parent1, parent2, unweighted);
		stats.runJob(0);

		MabcTableModel model = new MabcTableModel(viewSet);
		LineDataTable table = new LineDataTable();

		table.setModel(model);
		table.setViewSet(viewSet);

		LineDataTableExporter exporter = new LineDataTableExporter(
			table, new File(filename), 0, false);
		exporter.runJob(0);
	}

	private static void printHelp()
	{
		System.out.println("Usage: mabcstats <options>\n"
			+ " where valid options are:\n"
			+ "   -map=<map_file>                (required input file)\n"
			+ "   -genotypes=<genotypes_file>    (required input file)\n"
			+ "   -qtls=<qtl_file>               (required input file)\n"
			+ "   -parent1=<index_of_line>       (required parameter, index of recurrent parent, first line is index 1)\n"
			+ "   -parent2=<index_of_line>       (required parameter, index of donor parent, first line is index 1)\n"
			+ "   -model=weighted|unweighted     (optional parameter, defaults to weighted)\n"
			+ "   -coverage=<coverage_value>     (optional floating point parameter, defaults to 10)\n"
			+ "   -decimalEnglish                (optional parameter)\n"
			+ "   -output=<output_file>          (required output file)");

		System.exit(1);
	}
}