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

/**
 * Created by gs40939 on 28/06/2016.
 */
public class GenerateMABCStats
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

	public static void main(String[] args)
	{
		File mapFile = null;
		File genotypesFile = null;
		File qtlFile = null;
		double maxMarkerCoverage = 10;
		String filename = null;
		boolean decimalEnglish = false;

		NumberFormat nf = NumberFormat.getInstance();

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-map="))
				mapFile = new File(args[i].substring(5));
			if (args[i].startsWith("-genotypes="))
				genotypesFile = new File(args[i].substring(11));
			if (args[i].startsWith("-qtls="))
				qtlFile = new File(args[i].substring(6));
			if (args[i].startsWith("-coverage="))
			{
				try
				{
					maxMarkerCoverage = nf.parse(args[i].substring(10)).doubleValue();
				}
				catch (ParseException e) { e.printStackTrace(); }
			}
			if (args[i].startsWith("-stats="))
				filename = args[i].substring(7);
			if (args[i].startsWith("-decimalEnglish"))
				decimalEnglish = true;
		}

		if (mapFile == null || genotypesFile == null || qtlFile == null || filename == null)
		{
			System.out.println("Usage: mabcstats <options>\n"
				+ " where valid options are:\n"
				+ "   -map=<map_file>                (required input file)\n"
				+ "   -genotypes=<genotypes_file>    (required input file)\n"
				+ "   -qtls=<qtl_file>               (required input file)\n"
				+ "   -coverage=<coverage_value>     (optional floating point parameter)\n"
				+ "   -stats=<matrix_file>           (required output file)\n"
				+ "   -decimalEnglish                (optional parameter)\n");

			return;
		}

		GenerateMABCStats mabcStats = new GenerateMABCStats(mapFile, genotypesFile, qtlFile, maxMarkerCoverage, filename, decimalEnglish);
		mabcStats.doStatGeneration();

		System.exit(0);
	}

	public GenerateMABCStats(File mapFile, File genotypesFile, File qtlFile, double maxMarkerCoverage, String filename, boolean decimalEnglish)
	{
		this.mapFile = mapFile;
		this.genotypesFile = genotypesFile;
		this.qtlFile = qtlFile;
		this.maxMarkerCoverage = maxMarkerCoverage;
		this.filename = filename;
		this.decimalEnglish = decimalEnglish;
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

		// TODO: How do we get the rpIndex and dpIndex for passing to MABCStats?
		MABCStats stats = new MABCStats(viewSet, chromosomes, maxMarkerCoverage, 0, 1);
		stats.runJob(0);

		MabcTableModel model = new MabcTableModel(viewSet);
		LineDataTable table = new LineDataTable();

		table.setModel(model);
		table.setViewSet(viewSet);

		LineDataTableExporter exporter = new LineDataTableExporter(table, new File(filename));
		exporter.runJob(0);
	}
}