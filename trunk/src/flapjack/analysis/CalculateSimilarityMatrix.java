// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class CalculateSimilarityMatrix extends SimpleJob
{
	private GTViewSet viewSet;
	private String filename;
	private boolean[] chromosomes;
	private float[][] scores;

	private AtomicInteger linesScored = new AtomicInteger(0);

	public CalculateSimilarityMatrix(GTViewSet viewSet, String filename)
	{
		this.viewSet = viewSet;
		this.filename = filename;

		// We're generating a square matrix, so the total number of comparisons
		// will be the number of lines squared (divided by 2)
		maximum = (int) (Math.pow(viewSet.getLines().size(), 2) / 2);

		chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;
	}

	@Override
	public int getValue()
		{ return linesScored.intValue(); }

	@Override
	public void runJob(int index)
		throws Exception
	{
		long s = System.currentTimeMillis();

		for (GTView view: viewSet.getViews())
			view.cacheLines();

		// Set up the 2D array to hold the resultant matrix
		ArrayList<LineInfo> lines = viewSet.getLines();
		scores = new float[lines.size()][lines.size()];

		// Set up a multithreaded calculation run
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Future[] tasks = new Future[cores];

		for (int i = 0; i < tasks.length; i++)
			tasks[i] = executor.submit(new Calculator(i, cores));
		for (Future task: tasks)
			task.get();


		if (okToRun)
			writeResults(viewSet.getView(0), lines, scores);

		long e = System.currentTimeMillis();
		System.out.println("SimMatrix time: " + (e-s) + "ms");
	}

	private void writeResults(GTView view, ArrayList<LineInfo> lines, float[][] scores)
		throws Exception
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));

		// Header line
		for (int i = 0; i < lines.size(); i++)
		{
			// Ignore the awkward cases
			if (view.isDummyLine(i) || view.isSplitter(i) || view.isDuplicate(i))
				continue;

			LineInfo li = lines.get(i);
			out.write("\t" + li.getLine().getName());
		}
		out.newLine();

		// For each line
		for (int i = 0; i < lines.size(); i++)
		{
			// Ignore the awkward cases
			if (view.isDummyLine(i) || view.isSplitter(i) || view.isDuplicate(i))
				continue;

			// Its name
			LineInfo li = lines.get(i);
			out.write(li.getLine().getName());

			// Its scores
			for (int j = 0; j < lines.size(); j++)
			{
				if (i == j)
					out.write("\t1");
				else
					out.write("\t" + scores[i][j]);
			}

			out.newLine();
		}

		out.close();
	}

	@Override
	public String getMessage()
	{
		return RB.format("gui.MenuData.simMatrix.message", linesScored);
	}

	private class Calculator implements Runnable
	{
		private int i;
		private int cores;

		Calculator(int i, int cores)
		{
			this.i = i;
			this.cores = cores;
		}

		public void run()
		{
			float[][] matrix = viewSet.getDataSet().getStateTable().calculateSimilarityMatrix();
			ArrayList<LineInfo> lines = viewSet.getLines();

			// For every line...
			for (; i < lines.size() && okToRun; i += cores)
			{
				// Compare it against every other line...
				for (int j = i+1; j < lines.size() && okToRun; j++, linesScored.getAndIncrement())
				{
					SimilarityScore ss = new SimilarityScore(viewSet, matrix, i, j, chromosomes);
					SimilarityScore.Score score = ss.getScore(false);

					scores[i][j] = scores[j][i] = score.score;
				}
			}
		}
	}
}