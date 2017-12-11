// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import java.util.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

import org.apache.commons.cli.*;

import scri.commons.gui.*;

/**
 * Command-line extension to Flapjack for generating a similairty matrix
 */
public class CreateMatrix
{
	// The objects that will (hopefully) get created
	private DataSet dataSet = new DataSet();

	// And the files required to read and write to
	private CreateProjectSettings projectSettings;
	private DataImportSettings importSettings;
	private String filename;

	public static void main(String[] args)
	{
		CmdOptions options = new CmdOptions()
			.withAdvancedOptions()
			.withGenotypeFile(true)
			.withMapFile(false)
			.withOutputPath(true)
			.withProjectFile(false);

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			CreateProjectSettings projectSettings = options.getCreateProjectSettings(line);
			DataImportSettings importSettings = options.getDataImportSettings(line);
			String filename = options.getOutputPath(line);

			CreateMatrix cMatrix = new CreateMatrix(projectSettings, importSettings, filename);
			cMatrix.doMatrixCreation();

			System.exit(0);
		}
		catch (Exception e)
		{
			options.printHelp("CreateMatrix");

			System.exit(1);
		}
	}

	public CreateMatrix(CreateProjectSettings projectSettings, DataImportSettings importSettings, String filename)
	{
		this.projectSettings = projectSettings;
		this.importSettings = importSettings;
		this.filename = filename;
	}

	public void doMatrixCreation()
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

			CreateSimMatrix();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void CreateSimMatrix()
		throws Exception
	{
		GTViewSet viewSet = dataSet.getViewSets().get(0);
		GTView view = viewSet.getView(0);

		boolean[] chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;

		CalculateSimilarityMatrix calculator = new CalculateSimilarityMatrix(
			viewSet, view, chromosomes, false);

		calculator.runJob(0);
		SimMatrixExporter exporter = new SimMatrixExporter(calculator.getMatrix(), filename);
		exporter.runJob(0);
	}
}