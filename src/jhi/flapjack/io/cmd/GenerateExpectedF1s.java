// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import java.io.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

import org.apache.commons.cli.*;

import scri.commons.gui.*;

// Takes an input genotype file and the indices of two parents, then generates
// an expected F1 line from them, writing the results to an output file
public class GenerateExpectedF1s
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	private CreateProjectSettings projectSettings;
	private DataImportSettings importSettings;

	private String filename;
	private Integer parent1;
	private Integer parent2;

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.withOutputPath(true)
			.addRequiredOption("1", "parent-1", true, "INTEGER", "Index of parent-1 in file")
			.addRequiredOption("2", "parent-2", true, "INTEGER", "Index of parent-2 in file");

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			CreateProjectSettings projectSettings = options.getCreateProjectSettings(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);

			// Uncommon required vars
			String filename = options.getOutputPath(line);
			Integer parent1 = Integer.parseInt(line.getOptionValue("parent-1"))-1;
			Integer parent2 = Integer.parseInt(line.getOptionValue("parent-2"))-1;

			GenerateExpectedF1s stats = new GenerateExpectedF1s(projectSettings,
				importSettings, parent1, parent2, filename);
			stats.doStatGeneration();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("GenerateExpectedF1s");
			System.exit(1);
		}
	}

	private GenerateExpectedF1s(CreateProjectSettings projectSettings, DataImportSettings importSettings, Integer parent1,
		Integer parent2, String filename)
	{
		this.projectSettings = projectSettings;
		this.importSettings = importSettings;
		this.parent1 = parent1;
		this.parent2 = parent2;
		this.filename = filename;
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

			// Load the data into a view
			GTViewSet viewSet = dataSet.getViewSets().get(0);

			// Activate all chromosomes
			boolean[] chromosomes = new boolean[viewSet.chromosomeCount()];
			for (int i = 0; i < chromosomes.length; i++)
				chromosomes[i] = true;

			// Simulate the F1
			SimulateF1 simulate = new SimulateF1(viewSet, parent1, parent2);
			simulate.runJob(0);

			// Remove all the lines *apart* from the generated F1
			for (int i = simulate.getF1Index()-1; i >= 0; i--)
				viewSet.getLines().remove(i);

			// Write out the result
			GenotypeDataExporter exporter = new GenotypeDataExporter(
				new File(filename), viewSet, true, chromosomes, 0);
			exporter.runJob(0);

			System.out.println("Generated F1 was written to " + filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}