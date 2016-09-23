package jhi.flapjack.io.cmd;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.mabc.*;
import jhi.flapjack.gui.table.*;
import jhi.flapjack.io.*;

import java.io.*;
import java.text.*;
import java.util.*;

import scri.commons.gui.*;

public class GenerateMabcStats
{
	public static final String WEIGHTED = "weighted";
	public static final String UNWEIGHTED = "unweighted";

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
	private int parent1;
	private int parent2;

	public static void main(String[] args)
	{
		File mapFile = null;
		File genotypesFile = null;
		File qtlFile = null;
		double maxMarkerCoverage = 10;
		String filename = null;
		boolean decimalEnglish = false;
		String modelType = "";
		Integer parent1 = null;
		Integer parent2 = null;

		NumberFormat nf = NumberFormat.getInstance();

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-map="))
				mapFile = new File(args[i].substring(5));
			if (args[i].startsWith("-genotypes="))
				genotypesFile = new File(args[i].substring(11));
			if (args[i].startsWith("-qtls="))
				qtlFile = new File(args[i].substring(6));
			if (args[i].startsWith("-parent1="))
				parent1 = parseParent(args[i].substring(9));
			if (args[i].startsWith("-parent2="))
				parent2 = parseParent(args[i].substring(9));
			if (args[i].startsWith("-model="))
				modelType = args[i].substring(7);
			if (args[i].startsWith("-coverage="))
			{
				try
				{
					maxMarkerCoverage = nf.parse(args[i].substring(10)).doubleValue();
				}
				catch (ParseException e) { e.printStackTrace(); }
			}
			if (args[i].startsWith("-decimalEnglish"))
				decimalEnglish = true;
			if (args[i].startsWith("-output="))
				filename = args[i].substring(8);
		}

		if (mapFile == null || genotypesFile == null || qtlFile == null ||
			filename == null || parent1 == null || parent2 == null ||
			(modelType.equals(WEIGHTED) == false && modelType.equals(UNWEIGHTED) == false))
		{
			System.out.println("Usage: mabcstats <options>\n"
				+ " where valid options are:\n"
				+ "   -map=<map_file>                (required input file)\n"
				+ "   -genotypes=<genotypes_file>    (required input file)\n"
				+ "   -qtls=<qtl_file>               (required input file)\n"
				+ "   -parent1=<index_of_line>       (required parameter, first line is index 1)\n"
				+ "   -parent2=<index_of_line>       (required parameter, first line is index 1)\n"
				+ "   -model=weighted|unweighted     (required parameter)\n"
				+ "   -coverage=<coverage_value>     (optional floating point parameter)\n"
				+ "   -decimalEnglish                (optional parameter)\n"
				+ "   -output=<output_file>          (required output file)");

			return;
		}

		GenerateMabcStats mabcStats = new GenerateMabcStats(mapFile, genotypesFile, qtlFile, parent1, parent2, maxMarkerCoverage, filename, decimalEnglish, modelType);
		mabcStats.doStatGeneration();

		System.exit(0);
	}

	private static Integer parseParent(String parent)
	{
		Integer parentIndex = null;

		try
		{
			parentIndex = Integer.parseInt(parent) - 1;
		}
		catch (NumberFormatException e) {}

		return parentIndex;
	}

	public GenerateMabcStats(File mapFile, File genotypesFile, File qtlFile, int parent1, int parent2, double maxMarkerCoverage, String filename, boolean decimalEnglish, String modelType)
	{
		this.mapFile = mapFile;
		this.genotypesFile = genotypesFile;
		this.qtlFile = qtlFile;
		this.parent1 = parent1;
		this.parent2 = parent2;
		this.maxMarkerCoverage = maxMarkerCoverage;
		this.filename = filename;
		this.decimalEnglish = decimalEnglish;

		if (modelType.equals(UNWEIGHTED))
			unweighted = true;
	}

	public void doStatGeneration()
	{
		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();

		if (decimalEnglish)
			Locale.setDefault(Locale.UK);

		CreateProject createProject = new CreateProject(mapFile, genotypesFile, null, qtlFile, new FlapjackFile("temp"), false);

		try
		{
			createProject.createProject();
			dataSet = createProject.dataSet();

			generateMabcStats();
		}
		catch (Exception e)
		{
			System.out.println(e);
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
}