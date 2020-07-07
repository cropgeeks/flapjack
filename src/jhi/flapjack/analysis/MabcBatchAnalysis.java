// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.dialog.analysis.*;

import scri.commons.gui.*;

// Batch run multiple MABC analysis tasks
public class MabcBatchAnalysis extends SimpleJob
{
	private ArrayList<GTViewSet> resultViewSets;

	private List<MABCBatchSettings> batchSettings;
	private MABCThresholds thresholds;

	private double maxMarkerCoverage;
	private boolean simpleStats;
	private String name;

	public MabcBatchAnalysis(List<MABCBatchSettings> batchSettings, MABCThresholds thresholds, double maxMarkerCoverage, boolean simpleStats, String name)
	{
		this.batchSettings = batchSettings;
		this.thresholds = thresholds;

		this.maxMarkerCoverage = maxMarkerCoverage;
		this.simpleStats = simpleStats;
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

		MABCBatchSettings settings = batchSettings.get(i);
		GTViewSet viewSet = settings.getViewSet();
		
		int rpIndex = settings.getRpIndex();
		int dpIndex = settings.getDpIndex();

		boolean excludeParents = true;

		// Use a CSD dialog (without showing it) to get a suitable selected set
		ChromosomeSelectionDialog csd = new ChromosomeSelectionDialog(viewSet, true, false);
		boolean[] selectedChromosomes = csd.getSelectedChromosomes();

		MabcAnalysis stats = new MabcAnalysis(
			viewSet, selectedChromosomes, thresholds, maxMarkerCoverage, rpIndex,
			dpIndex, excludeParents, simpleStats, name);

		stats.runJob(0);
		resultViewSets.add(stats.getViewSet());

		progress++;
	}
}