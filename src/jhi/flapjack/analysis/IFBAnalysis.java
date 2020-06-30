// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;
import java.util.stream.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class IFBAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;

	private AnalysisSet as;
	private StateTable stateTable;

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

		IntStream.range(0, as.lineCount()).forEach((lineIndex) ->
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
					ArrayList<MarkerProperties> list = mi.getMarker().getProperties();

					if (list == null)
					{
						processNonQTLMarker(result, viewIndex, lineIndex, mrkIndex);
						continue;
					}

					// then, for each QTL this marker is under
					for (MarkerProperties props: list)
					{
						QTL qtl = props.getQtl();
						if (qtl == null || props.getFavAlleles().size() == 0)
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

						// Special case for missing data
						if (state == 0)
						{
							score.setRefAlleleMatchCount(-1);
							continue;
						}

						int count = 0;
						// Check both alleles
						for (int i = 0; i < 2; i++)
							if (lookupTable[state][i] == favAllele)
								count++;

						score.setRefAlleleMatchCount(count);
					}
				}
			}

			// Sum up the MBV and wMBV as we go along
			double mbvTotal = 0, wmbvTotal = 0;

//			System.out.println("\nLine: " + line.name());
			for (IFBQTLScore qtlScore: result.getQtlScores())
			{
//				System.out.println(" " + qtlScore.getQtl().getName());
//				for (IFBMarkerScore mScore: qtlScore.getMarkerScores())
//				{
//					System.out.println("  " + mScore.getProperties().getMarker().getName());
//					System.out.println("  " + mScore.getRefAlleleMatchCount());
//				}

				determineMarkerProperties(qtlScore);
				calculateMode(qtlScore);

//				System.out.println("  " + qtlScore.qtlGenotype());

				// If we managed to calculate an MBV, add the score object to the
				// separate list tracking just those QTLs with scores
				if (calculateMolecularBreedingValue(qtlScore))
					result.getMbvScores().add(qtlScore);

				mbvTotal += qtlScore.getMolecularBreedingValue();
				wmbvTotal += qtlScore.getWeightedMolecularBreedingValue();
			}

			boolean mbvValid = true;
			for (IFBQTLScore qtlScore: result.getMbvScores())
				if (qtlScore.getRefAlleleMatchCount() == -1)
					mbvValid = false;

			result.setMbvValid(mbvValid);
			result.setMbvTotal(mbvTotal);
			result.setWmbvTotal(wmbvTotal);

//			System.out.println("");
//			System.out.println("Sum_MBV:  " + result.getMbvTotal());
//			System.out.println("Sum_wMBV: " + result.getWmbvTotal());

//			if (true)
//				System.exit(1);
		});
	}

	private void prepareForVisualization()
	{
		changeColourScheme();
		addViewSetToDataSet();
	}

	private void changeColourScheme()
	{
		viewSet.setColorScheme(ColorScheme.FAV_ALLELE);
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

	// UMESH Step 3b (see /docs/ifb)
	// Calculate mode per QTL
	// Looks across the various IFBMarkerScore objects held by this qtl object
	// and determines the mode, ie, the most common value (where the value per
	// marker was its count of allele matches to the reference)
	private void calculateMode(IFBQTLScore qtlScore)
	{
		// Build a map counting the number of times each value appears
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (IFBMarkerScore markerScore: qtlScore.getMarkerScores())
		{
			int value = markerScore.getRefAlleleMatchCount();

			// Missing data? Don't use it to calculate mode
			if (value == -1)
				continue;

			Integer count = map.get(value);
			map.put(value, count != null ? count+1 : 1);
		}

		// If the map is size=0, then it must all have been missing data. We'll
		// just use the value from the priority marker
		if (map.size() == 0)
			qtlScore.setRefAlleleMatchCount(-1);

		else
		{
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
			// Or no clear winner
			else
			{
				// The first marker is treated as the priority marker
				int refAlleleMatchCount = qtlScore.getMarkerScores().get(0).getRefAlleleMatchCount();
				qtlScore.setRefAlleleMatchCount(refAlleleMatchCount);
			}
		}

//		System.out.println("  refMatch: " + qtlScore.getRefAlleleMatchCount());
	}

	// Finds and sets an appropriate MarkerProperties object for each QTL. A QTL
	// may have several markers underlying it, but we want to identify the
	// (first) priority marker and use its Properties for all calculations
	private void determineMarkerProperties(IFBQTLScore qtlScore)
	{
		// Set equal to the first one...
		if (qtlScore.getMarkerScores().size() > 0)
			qtlScore.setProperties(qtlScore.getMarkerScores().get(0).getProperties());

		// ...in case we can't find a priority marker, whose value we'd really
		// rather use
//		for (IFBMarkerScore markerScore: qtlScore.getMarkerScores())
//		{
//			if (markerScore.getProperties().isPriorityMarker())
//				qtlScore.setProperties(markerScore.getProperties());
//		}
	}

	// UMESH Step 5 (see /docs/ifb/)
	private boolean calculateMolecularBreedingValue(IFBQTLScore qtlScore)
	{
		MarkerProperties props = qtlScore.getProperties();

		// We don't need to calculate this QTL marked with "breeding value NO"
		if (props.isBreedingValue() == false)
			return false;

		double mbv;

//		System.out.println("  props.getSubEffect()=" + props.getSubEffect() + ", props.getRelWeight()=" + props.getRelWeight());

		// If the model is ADDITIVE, multiple doseage by substitution effect
		if (props.getModel() == MarkerProperties.ADDITIVE)
			mbv = (double) qtlScore.getRefAlleleMatchCount() * props.getSubEffect();

		// If not, recode dosage >0 as 2 before multiplying
		else
		{
			if (qtlScore.getRefAlleleMatchCount() > 0)
				mbv = 2d * props.getSubEffect();
			else
				mbv = 0;
		}

		qtlScore.setMolecularBreedingValue(mbv);

		// Step 6 for weighted MBV:
		double wMBV = mbv * props.getRelWeight();
		qtlScore.setWeightedMolecularBreedingValue(wMBV);

//		System.out.println("  mbv: " + mbv + ", " + wMBV);

		return true;
	}

	// Works out details (for IFBResult storage) for a marker that isn't under
	// a QTL. We still need to track all markers not under a QTL because they
	// need to be displayed in the table :(
	private void processNonQTLMarker(IFBResult result, int viewIndex, int lineIndex, int mrkIndex)
	{
		if (Prefs.guiIFBIncludeNonQTLMarkers == false)
			return;

		MarkerInfo mi = as.getMarker(viewIndex, mrkIndex);

		String mkrName = mi.getMarker().getName();
		int state = as.getState(viewIndex, lineIndex, mrkIndex);

		IFBQTLScore score = new IFBQTLScore(mkrName, state);

		result.getMkrScores().add(score);
	}
}