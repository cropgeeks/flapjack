// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.dialog.analysis.*;

import scri.commons.gui.*;

// Batch run multiple PedVerLines analysis tasks
public class PedVerLinesBatchAnalysis extends SimpleJob
{
	private ArrayList<GTViewSet> viewSets, resultViewSets;
	private PedVerLinesThresholds thresholds;

	private String name;

	public PedVerLinesBatchAnalysis(ArrayList<GTViewSet> viewSets, PedVerLinesThresholds thresholds, String name)
	{
		this.viewSets = viewSets;
		this.thresholds = thresholds;

		this.name = name;

		maximum = viewSets.size();
		resultViewSets = new ArrayList<>(maximum);
	}

	public ArrayList<GTViewSet> getResultViewSets()
		{ return resultViewSets; }

	@Override
	public int getJobCount()
		{ return viewSets.size(); }


	@Override
	public void runJob(int i)
		throws Exception
	{
		System.out.println("Running analysis " + i);

		GTViewSet viewSet = viewSets.get(i);

		// TODO: SET TO WHAT?
		int p1Index = 0;
		int p2Index = 1;

		// Use a CSD dialog (without showing it) to get a suitable selected set
		ChromosomeSelectionDialog csd = new ChromosomeSelectionDialog(viewSet, true, false);
		boolean[] selectedChromosomes = csd.getSelectedChromosomes();

		// Clone the common thresholds into a per-dataset object
		PedVerLinesThresholds t = thresholds.clone();

		PedVerLinesAnalysis stats = new PedVerLinesAnalysis(viewSet,
			selectedChromosomes, t, p1Index, p2Index, name);

		stats.runJob(0);
		resultViewSets.add(stats.getViewSet());

		progress++;
	}
}