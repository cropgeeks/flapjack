// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class CalculateSimilarityMatrix extends SimpleJob
{
	private GTViewSet viewSet;
	private GTView view;

	// A boolean array indicating which chromosomes to use for the calculation
	private boolean[] chromosomes;
	private ArrayList<Integer> indices;

	// A list of selected marker indices (into the original data) across all
	// selected chromosomes
	private ArrayList<int[]> viewMarkers;

	// Mini-matrix of allele-by-allele scores to use as a look up table
	private float[][] stMatrix;
	// Class that does the actual calculations for us
	private SimilarityScore ss;

	// End result...
	private SimMatrix matrix = new SimMatrix();
	private AtomicInteger count = new AtomicInteger(0);

	// True, if the resultant matrix should get serialized to disk
	private boolean useCache;

	public CalculateSimilarityMatrix(GTViewSet viewSet, GTView view, boolean[] chromosomes, boolean useCache)
	{
		this.viewSet = viewSet;
		this.view = view;
		this.chromosomes = chromosomes;
		this.useCache = useCache;

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

		// Build the list of selected marker indices
		viewMarkers = new ArrayList<int[]>();
		for (int i=0; i < viewSet.getViews().size(); i++)
			if (chromosomes[i])
				viewMarkers.add(selectedMarkers(viewSet.getView(i)));

		// Set up the objects that can be reused on each run
		stMatrix = viewSet.getDataSet().getStateTable().calculateSimilarityMatrix();
		ss = new SimilarityScore(viewSet, stMatrix, chromosomes);

		// Set up the 2D array to hold the resultant matrix
		matrix.initialize(indices.size());


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
			viewSet.getMatrices().add(matrix);

			long e = System.currentTimeMillis();
			System.out.println("SimMatrix time: " + (e-s) + "ms");

			// Cache the result to disk
			if (useCache)
				ProjectSerializerDB.cacheToDisk(matrix);
		}
	}

	@Override
	public String getMessage()
	{
		return RB.format("gui.MenuAnalysis.simMatrix.message", count);
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

	// Returns an array where each element is the index in a GenotypeData object
	// that contains the allele score for each selected marker in the view.
	private int[] selectedMarkers(GTView view)
	{
		int[] selected = new int[view.countSelectedMarkers()];
		for (int i=0, j=0; i < view.getMarkers().size(); i++)
			if (view.getMarkerInfo(i).dummyMarker() == false)
				if (view.isMarkerSelected(i))
					selected[j++] = view.getMarkerInfo(i).getIndex();

		return selected;
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
			// For every line...
			for (int indicesSize = indices.size(); i < indicesSize && okToRun; i += cores)
			{
				// Compare it against every other line...
				for (int j = 0; j < i && okToRun; j++, count.getAndIncrement())
				{
					int a = indices.get(i); // Real index of line A
					int b = indices.get(j); // Real index of line B

					float score = ss.getScore(getData(a), getData(b), viewMarkers);

					matrix.setValueAt(i, j, score);
				}
			}
		}

		// Builds a list of GenotypeData objects, one per selected chromosome
		private ArrayList<GenotypeData> getData(int lineIndex)
		{
			Line line = viewSet.getLines().get(lineIndex).getLine();

			ArrayList<GenotypeData> data = new ArrayList<>();

			for (int i = 0; i < chromosomes.length; i++)
				if (chromosomes[i])
					data.add(line.getGenotypes().get(i));

			return data;
		}
	}
}