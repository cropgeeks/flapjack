// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.dialog.analysis.*;

import scri.commons.gui.*;

// Batch run multiple IFB analysis tasks
public class IFBBatchAnalysis extends SimpleJob
{
	private ArrayList<GTViewSet> viewSets, resultViewSets;

	private String name;

	public IFBBatchAnalysis(ArrayList<GTViewSet> viewSets, String name)
	{
		this.viewSets = viewSets;

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


		// Use a CSD dialog (without showing it) to get a suitable selected set
		ChromosomeSelectionDialog csd = new ChromosomeSelectionDialog(viewSet, true, false);
		boolean[] selectedChromosomes = csd.getSelectedChromosomes();

		IFBAnalysis stats = new IFBAnalysis(viewSet,
			selectedChromosomes, name);

		stats.runJob(0);
		resultViewSets.add(stats.getViewSet());

		progress++;
	}
}