// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;

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
			if (args[i].startsWith("-project="))
				prjFile = new FlapjackFile(args[i].substring(9));
			if (args[i].startsWith("-dir="))
				outputDir = args[i].substring(5);
			if (args[i].startsWith("-decimalEnglish"))
				decimalEnglish = true;
		}

		if (prjFile == null || outputDir == null)
		{
			System.out.println("Usage: splitproject <options>\n"
				+ " where valid options are:\n"
				+ "   -project=<project_file>        (REQUIRED)\n"
				+ "   -dir=<output_directory>        (REQUIRED)\n"
				+ "   -decimalEnglish                (optional)\n");

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

	private static void exportMap()
		throws Exception
	{
		for (DataSet dataSet: project.getDataSets())
		{
			for (GTViewSet viewSet: dataSet.getViewSets())
			{
				String name = dataSet.getName() + "_" + viewSet.getName() + ".map";

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
			for (GTViewSet viewSet: dataSet.getViewSets())
			{
				String name = dataSet.getName() + "_" + viewSet.getName() + ".dat";

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
			String name = dataSet.getName() + ".traits";

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
			String name = dataSet.getName() + ".qtl";

			QTLExporter exporter = new QTLExporter(
				dataSet, new File(outputDir, name));

			System.out.println("Exporting QTL:       " + name);
			exporter.runJob(0);
		}
	}
}