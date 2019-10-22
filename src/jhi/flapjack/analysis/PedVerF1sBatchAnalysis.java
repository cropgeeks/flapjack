// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.dialog.analysis.*;

import scri.commons.gui.*;

// Batch run multiple PedVerF1 analysis tasks
public class PedVerF1sBatchAnalysis extends SimpleJob
{
	private ArrayList<GTViewSet> resultViewSets;

	private List<PedVerF1sBatchSettings> batchSettings;
	private PedVerF1sThresholds thresholds;

	private String name;

	public PedVerF1sBatchAnalysis(List<PedVerF1sBatchSettings> batchSettings, PedVerF1sThresholds thresholds, String name)
	{
		this.batchSettings = batchSettings;
		this.thresholds = thresholds;

		this.name = name;

		maximum = batchSettings.size();
		resultViewSets = new ArrayList<>(maximum);
	}

	public ArrayList<GTViewSet> getResultViewSets()
		{ return resultViewSets; }

	@Override
	public int getJobCount()
		{ return batchSettings.size(); }


	@Override
	public void runJob(int i)
		throws Exception
	{
		System.out.println("Running analysis " + i);

		PedVerF1sBatchSettings settings = batchSettings.get(i);
		GTViewSet viewSet = settings.getViewSet();

		// TODO: SET TO WHAT?
		int p1Index = settings.getParent1Index();
		int p2Index = settings.getParent2Index();
		int f1Index = -1;
		boolean simulateF1 = true;
		boolean excludeParents = true;

		// Use a CSD dialog (without showing it) to get a suitable selected set
		ChromosomeSelectionDialog csd = new ChromosomeSelectionDialog(viewSet, true, false);
		boolean[] selectedChromosomes = csd.getSelectedChromosomes();

		PedVerF1sAnalysis stats = new PedVerF1sAnalysis(viewSet,
			selectedChromosomes, p1Index, p2Index, simulateF1, f1Index,
			excludeParents, name, thresholds);

		stats.runJob(0);
		resultViewSets.add(stats.getViewSet());

		progress++;
	}
}