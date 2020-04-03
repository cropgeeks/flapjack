// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;
import scri.commons.gui.*;

import java.util.*;
import java.util.stream.*;

public class IFBAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;

	private AnalysisSet as;
	private StateTable stateTable;
	private FavAlleleManager favAlleleManager;

	private String name;

	public IFBAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, String name)
	{
		this(viewSet, selectedChromosomes);
		this.name = name;
	}

	public IFBAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes)
	{
		this.viewSet = viewSet.createClone("", true);
		this.selectedChromosomes = selectedChromosomes;
		this.stateTable = viewSet.getDataSet().getStateTable();
		this.favAlleleManager = viewSet.getDataSet().getFavAlleleManager();
	}

	public void runJob(int index)
		throws Exception
	{
		long s = System.currentTimeMillis();

		as = new AnalysisSet(this.viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();

		runAnalysis();
		prepareForVisualization();

		long e = System.currentTimeMillis();
		System.out.println("TIME: " + (e-s) + "ms");
	}

	private void runAnalysis()
	{
		IntStream.range(0, as.lineCount()).parallel().forEach((lineIndex) ->
		{
			// Get the line from the analysis set and set up an FB results object for it
			LineInfo line = as.getLine(lineIndex);
			IFBResult result = new IFBResult();
			line.getLineResults().setIFBResult(result);
			line.getLineResults().setName(name);
		});
	}

	private void prepareForVisualization()
	{
		changeColourScheme();
		addViewSetToDataSet();
	}

	private void changeColourScheme()
	{
//		viewSet.setColorScheme(ColorScheme.FAV_ALLELE);
	}

	private void addViewSetToDataSet()
	{
		DataSet dataSet = viewSet.getDataSet();

		int id = dataSet.getNavPanelCounts().getOrDefault("ifbCount", 0) + 1;
		dataSet.getNavPanelCounts().put("ifbCount", id);
		viewSet.setName(RB.format("gui.MenuAnalysis.ifb.view", id));

		// Add the results viewset to the dataset
		dataSet.getViewSets().add(viewSet);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }
}