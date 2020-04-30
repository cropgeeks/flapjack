// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;
import scri.commons.gui.*;

import java.util.*;
import java.util.Map.Entry;
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
		// Create a lookup table for easily matching single alleles
		int[][] lookupTable = stateTable.createAlleleLookupTable();

		IntStream.range(0, as.lineCount())/*.parallel()*/.forEach((lineIndex) ->
		{
			// Get the line from the analysis set and set up an IFB results object for it
			LineInfo line = as.getLine(lineIndex);
			IFBResult result = new IFBResult();
			line.getLineResults().setIFBResult(result);
			line.getLineResults().setName(name);

			// A lookup hash of IFBQTLScores (searching by QTL)
			HashMap<QTL,IFBQTLScore> qtlScores = new HashMap<>();

			// ...loop over each chromosome
			for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			{
				// ...and each marker
				for (int mrkIndex = 0; mrkIndex < as.markerCount(viewIndex); mrkIndex++)
				{
					// If this Marker has MarkerProperties, then we can continue
					MarkerInfo mi = as.getMarker(viewIndex, mrkIndex);
					MarkerProperties props = mi.getMarker().getProperties();
					QTL qtl = props.getQtl();

					if (props == null || qtl == null || props.getFavAlleles().size() == 0)
						continue;

					// Get (or create) an IFBQTLScore that covers this marker
					IFBQTLScore qtlScore = new IFBQTLScore(qtl);
					if (qtlScores.get(qtl) == null)
					{
						qtlScores.put(qtl, qtlScore);
						result.getQtlScores().add(qtlScore);
					}
					else
						qtlScore = qtlScores.get(qtl);


					// UMESH Step 3a (see /docs/ifb/)
					// Calculate dosage for each favorable allele
					//   We're looping over the markers of this line, checking
					//   each genotype against the favorable allele for each
					//   marker of interest (via MarkerProperties). For each
					//   allele match, increment the score, eg if favAllele=A
					//   and genotype A/A=2; A/T=1; T/T=0 etc

					// Make a new MarkerScore object to hold the count
					IFBMarkerScore score = new IFBMarkerScore(props);
					qtlScore.getMarkerScores().add(score);

					int favAllele = props.getFavAlleles().get(0);
					int state = as.getState(viewIndex, lineIndex, mrkIndex);
					int count = 0;

					// Check both alleles
					for (int i = 0; i < 2; i++)
						if (lookupTable[state][i] == favAllele)
						count++;

					score.setRefAlleleMatchCount(count);
				}
			}

			// UMESH Step 3b (see /docs/ifb)
			// Calculate mode per QTL

			System.out.println("\nLine: " + line.name());
			for (IFBQTLScore qtlScore: result.getQtlScores())
			{
				System.out.println(" " + qtlScore.getQtl().getName());
				for (IFBMarkerScore mScore: qtlScore.getMarkerScores())
				{
	//				System.out.println("  " + mScore.getProperties().getMarker().getName());
	//				System.out.println("  " + mScore.getRefAlleleMatchCount());
				}

				calculateMode(qtlScore);
				calculateRefAlleleDisplay(qtlScore);
			}
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

	// Looks across the various IFBMarkerScore objects held by this qtl object
	// and determines the mode, ie, the most common value (where the value per
	// marker was its count of allele matches to the reference)
	private void calculateMode(IFBQTLScore qtlScore)
	{
		System.out.println("Calculating mode for " + qtlScore.getQtl().getName());

		// Build a map counting the number of times each value appears
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (IFBMarkerScore markerScore: qtlScore.getMarkerScores())
		{
			int value = markerScore.getRefAlleleMatchCount();

			Integer count = map.get(value);
			map.put(value, count != null ? count+1 : 1);
		}

		// We're tracking allele states, so only have counts of 0, 1, or 2
		Integer zero = map.get(0) != null ? map.get(0) : -1;
		Integer one = map.get(1) != null ? map.get(1) : -1;
		Integer two = map.get(2) != null ? map.get(2) : -1;

		// Did we find more 0s?
		if (zero > one && zero > two)
			qtlScore.setRefAlleleMatchCount(0);
		// Or 1s?
		else if (one > zero && one > two)
			qtlScore.setRefAlleleMatchCount(1);
		// Or 2s?
		else if (two > zero && two > one)
			qtlScore.setRefAlleleMatchCount(2);
		// Or not clear winner
		else
		{
			// Use the value from PriorityMarker instead
			for (IFBMarkerScore markerScore: qtlScore.getMarkerScores())
			{
				if (markerScore.getProperties().isPriorityMarker())
				{
					int refAlleleMatchCount = markerScore.getRefAlleleMatchCount();
					qtlScore.setRefAlleleMatchCount(refAlleleMatchCount);
				}
			}
		}

		System.out.println(" refMatch: " + qtlScore.getRefAlleleMatchCount());
	}

	// Looks across the markers associated with a QTL and decides what String to
	// display when showing Umesh:Step 4 display matrix
	private void calculateRefAlleleDisplay(IFBQTLScore qtlScore)
	{
		String str = "-";

		// Set the string to the first marker's value - just so we have something
		// to use...
		if (qtlScore.getMarkerScores().size() > 0)
			str = qtlScore.getMarkerScores().get(0).getProperties().getAlleleName();

		// ...in case we can't find a priority marker, whose value we'd really
		// rather use
		for (IFBMarkerScore markerScore: qtlScore.getMarkerScores())
		{
			MarkerProperties properties = markerScore.getProperties();

			if (properties.isPriorityMarker())
				str = properties.getAlleleName();
		}

		switch (qtlScore.getRefAlleleMatchCount())
		{
			case 0: System.out.println("-/-"); break;
			case 1: System.out.println(str + "/-"); break;
			case 2: System.out.println(str + "/" + str); break;
		}

		qtlScore.setAlleleName(str);
	}
}