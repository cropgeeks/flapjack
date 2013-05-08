// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
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
	private GTView view;
	private String filename;

	private boolean[] chromosomes;
	private ArrayList<Integer> indices;
//	private ArrayList<String> lineNames;
	private ArrayList<ArrayList<Float>> lineScores;

	private SimMatrix matrix = new SimMatrix();

	private AtomicInteger count = new AtomicInteger(0);

	public CalculateSimilarityMatrix(GTViewSet viewSet, GTView view, String filename)
	{
		this.viewSet = viewSet;
		this.view = view;
		this.filename = filename;

		// Work out the indices of all the lines being compared
		indices = new ArrayList<Integer>();
		for (int i = 0; i < viewSet.getLines().size(); i++)
			if (skipLine(i) == false)
			{
				indices.add(i);
				matrix.getLineInfos().add(view.getLineInfo(i));
			}

		// We're generating a square matrix, so the total number of comparisons
		// will be the number of lines squared (divided by 2)
		maximum = (int) (Math.pow(indices.size(), 2) / 2);


		// TODO:
		chromosomes = new boolean[viewSet.chromosomeCount()];
		for (int i = 0; i < chromosomes.length; i++)
			chromosomes[i] = true;
	}

	public SimMatrix getMatrix()
		{ return matrix; }

	@Override
	public int getValue()
		{ return count.intValue(); }

	@Override
	public void runJob(int index)
		throws Exception
	{
		long s = System.currentTimeMillis();

		for (GTView view: viewSet.getViews())
			view.cacheLines();

		// Set up the 2D array to hold the resultant matrix
		lineScores = new ArrayList<ArrayList<Float>>();
		for (int i = 0; i < indices.size(); i++)
		{
			lineScores.add(new ArrayList<Float>());
//			for (int j = 0; j < indices.size(); j++)			// uncomment to generate FULL matrix (not half)
			for (int j = 0; j <= i; j++)
				lineScores.get(i).add(1f);
		}

		// Set up a multithreaded calculation run
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Future[] tasks = new Future[cores];

		System.out.println("Calculating similarity matrix...");

		for (int i = 0; i < tasks.length; i++)
			tasks[i] = executor.submit(new Calculator(i, cores));
		for (Future task: tasks)
			task.get();

		if (okToRun)
		{
			matrix.setLineScores(lineScores);
			viewSet.matrices.add(matrix);
		}

//		if (okToRun)
//			writeResults(viewSet.getView(0));

		long e = System.currentTimeMillis();
		System.out.println("SimMatrix time: " + (e-s) + "ms");
	}

	private void writeResults(GTView view)
		throws Exception
	{
		ArrayList<LineInfo> lines = viewSet.getLines();

		System.out.println("Writing results...");

		BufferedWriter out = new BufferedWriter(new FileWriter(filename));

		// Header line
		for (int i = 0; i < indices.size(); i++)
		{
			LineInfo li = lines.get(indices.get(i));
			out.write("\t" + li.getLine().getName());
		}
		out.newLine();

		// For each line
		for (int i = 0; i < indices.size(); i++)
		{
			// Its name
			LineInfo li = lines.get(indices.get(i));
			out.write(li.getLine().getName());

			// Its scores
			for (int j = 0; j < indices.size(); j++)
			{
				if (j <= i)
					out.write("\t" + lineScores.get(i).get(j));
				else
					out.write("\t" + lineScores.get(j).get(i));
			}

			out.newLine();
		}

		out.close();
	}

	@Override
	public String getMessage()
	{
		return RB.format("gui.MenuData.simMatrix.message", count);
	}

	private boolean skipLine(int i)
	{
		// Ignore the awkward cases
		if (view.isDummyLine(i) || view.isSplitter(i) || view.isDuplicate(i))
			return true;
		// Or unselected lines
		if (view.isLineSelected(i) == false)
			return true;

		return false;
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

			// For every line...
			for (; i < indices.size() && okToRun; i += cores)
			{
				// Compare it against every other line...
				for (int j = 0; j <= i && okToRun; j++, count.getAndIncrement())
				{
					if (i != j)
					{
						int a = indices.get(i); // Real index of line A
						int b = indices.get(j); // Real index of line B

						SimilarityScore ss = new SimilarityScore(viewSet, matrix, a, b, chromosomes);
						SimilarityScore.Score score = ss.getScore(false);

						// First diagonal of the matrix
						lineScores.get(i).set(j, score.score);
						// Second diagonal of the matrix
//						lineScores.get(j).set(i, score.score);        // uncomment to generate FULL matrix (not half)
					}
				}
			}
		}
	}
}