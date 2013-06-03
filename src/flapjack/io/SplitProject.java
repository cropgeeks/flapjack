// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

/**
 * Command-line extension to Flapjack that can be used to load in an existing
 * project file and then split it back up into separate data files (map,
 * genotype, traits, etc)
 */
public class SplitProject extends SimpleJob
{
	private static Project project = new Project();

	// Command line parameters
	private static FlapjackFile prjFile;
	private static String outputDir;
	private static boolean decimalEnglish = false;
	private static boolean isCommandLine = false;

	private static HashMap<String,String> datasets = new HashMap<>();


	// Constructor is called by the GUI for a File->Quick Export menu option
	public SplitProject(Project project, String outputDir)
	{
		this.project = project;
		this.outputDir = outputDir;

		maximum = 4;
	}

	public SplitProject()
	{
	}

	// Or the main method (obviously) called by the command line
	public static void main(String[] args)
	{
		isCommandLine = true;

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].toUpperCase().startsWith("-PROJECT="))
				prjFile = new FlapjackFile(args[i].substring(9));
			if (args[i].toUpperCase().startsWith("-DIR="))
				outputDir = args[i].substring(5);
			if (args[i].toUpperCase().startsWith("-DECIMALENGLISH"))
				decimalEnglish = true;

			// The user can name only certain datasets to be exported
			if (args[i].toUpperCase().startsWith("-DATASETIN="))
			{
				String name = args[i].substring(11);
				datasets.put(name, name);

				// And even give them a new name when they are
				if (i+1 < args.length)
				{
					if (args[i+1].toUpperCase().startsWith("-DATASETOUT="))
					{
						String newname = args[i+1].substring(12);
						datasets.put(name, newname);

						i++;
					}
				}
			}
		}


		if (prjFile == null || outputDir == null)
		{
			System.out.println("Flapjack " + Install4j.VERSION + "\n\n"
				+ "Splits a Flapjack project into multiple raw data text files.\n\n"
				+ "SPLITPROJECT -PROJECT=project -DIR=dir\n"
				+ "             [-DATASETIN=datasetin [-DATASETOUT=datasetout]]\n"
				+ "             [-DECIMALENGLISH]\n\n"
				+ "  project           The location of the project to process.\n"
				+ "  dir               The location to write the output files to.\n"
				+ "  datasetin         Specifies the name of a dataset within the project file\n"
				+ "                    to process. If no names are specified, then all datasets\n"
				+ "                    will be extracted.\n"
				+ "  datasetout        Overrides the given datasetin name with a new name to use\n"
				+ "                    when outputting that dataset's files.\n"
				+ "  -DECIMALENGLISH   Specifies that all numbers will be processed using the\n"
				+ "                    English decimal mark separator (dot rather than a comma).\n\n"
				+ "Note that dataset names in Flapjack are case sensitive.\n"
				+ "You can process multiple datasets at once, eg:\n"
				+ "  -DATASETIN dsin1 -DATASETOUT dsout1 -DATASETIN dsin2 -DATASETOUT dsout2\n");


			return;
		}

		RB.initialize("auto", "res.text.flapjack");
		TaskDialog.setIsHeadless();

		if (decimalEnglish)
			Locale.setDefault(Locale.UK);

		try
		{
			SplitProject splitter = new SplitProject();
			splitter.runJob(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Actual method that does the work. Simply takes the data and runs it
	// through each of the export options that we've added.
	public void runJob(int index)
		throws Exception
	{
		if (isCommandLine)
			openProject();

		exportMap();
		progress = 1;

		exportGenotypes();
		progress = 2;

		exportTraits();
		progress = 3;

		exportQTL();
		progress = 4;
	}

	private static void openProject()
		throws Exception
	{
		project = ProjectSerializer.open(prjFile);
	}

	// Returns the name to use for the output of this dataset. It will either be
	// the original name, or the overriden name if the user passed one in
	private static String getName(DataSet dataSet)
	{
		if (datasets.containsKey(dataSet.getName()))
			return datasets.get(dataSet.getName());
		else
			return dataSet.getName();
	}

	// Returns true if the given dataset should be processed. The rationale for
	// this is: if no named datasets, then all will be extracted; otherwise,
	// only named datasets will return true (regardless of whether they have a
	// name override too)
	private static boolean processDataSet(DataSet dataSet)
	{
		if (datasets.size() == 0)
			return true;

		else if (datasets.containsKey(dataSet.getName()))
			return true;
		else
			return false;
	}

	private static void exportMap()
		throws Exception
	{
		for (DataSet dataSet: project.getDataSets())
		{
			if (processDataSet(dataSet) == false)
				continue;

			for (GTViewSet viewSet: dataSet.getViewSets())
			{
				String name = getName(dataSet) + "_" + viewSet.getName() + ".map";

				ChromosomeMapExporter exporter = new ChromosomeMapExporter(
					new File(outputDir, name),
					viewSet, true, null, 0);

				System.out.println("Exporting map:       " + name);
				exporter.runJob(0);
			}
		}
	}

	private static void exportGenotypes()
		throws Exception
	{
		for (DataSet dataSet: project.getDataSets())
		{
			if (processDataSet(dataSet) == false)
				continue;

			for (GTViewSet viewSet: dataSet.getViewSets())
			{
				String name = getName(dataSet) + "_" + viewSet.getName() + ".dat";

				GenotypeDataExporter exporter = new GenotypeDataExporter(
					new File(outputDir, name),
					viewSet, true, null, 0);

				System.out.println("Exporting genotypes: " + name);
				exporter.runJob(0);
			}
		}
	}

	private static void exportTraits()
		throws Exception
	{
		for (DataSet dataSet: project.getDataSets())
		{
			if (processDataSet(dataSet) == false)
				continue;

			String name = getName(dataSet) + ".traits";

			TraitExporter exporter = new TraitExporter(
				dataSet, new File(outputDir, name));

			System.out.println("Exporting traits:    " + name);
			exporter.runJob(0);
		}
	}

	private static void exportQTL()
		throws Exception
	{
		for (DataSet dataSet: project.getDataSets())
		{
			if (processDataSet(dataSet) == false)
				continue;

			String name = getName(dataSet) + ".qtl";

			QTLExporter exporter = new QTLExporter(
				dataSet, new File(outputDir, name));

			System.out.println("Exporting QTL:       " + name);
			exporter.runJob(0);
		}
	}
}