// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;
import scri.commons.gui.*;

import java.util.*;
import java.util.stream.*;

public class FBAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;

	private AnalysisSet as;
	private StateTable stateTable;
	private FavAlleleManager favAlleleManager;

	private String name;

	public FBAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, String name)
	{
		this(viewSet, selectedChromosomes);
		this.name = name;
	}

	public FBAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes)
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

	private int countTotalMarkers()
	{
		int totalMarkers = 0;
		for (int c = 0; c < as.viewCount(); c++)
			for (int m = 0; m < as.markerCount(c); m++)
				totalMarkers++;

		return totalMarkers;
	}

	private void runAnalysis()
	{
		final int totalMarkers = countTotalMarkers();

		// Determine the marker indices under each qtl and make that queryable in a HashMap
		HashMap<String, ArrayList<Integer>> markerIndicesByQtlName = getMarkersByQtlMap();

		// Generate the stats for each line
//		for (int lineIndex = 0; lineIndex < as.lineCount(); lineIndex++)
		IntStream.range(0, as.lineCount()).parallel().forEach((lineIndex) ->
		{
			// Get the line from the analysis set and set up an FB results object for it
			LineInfo line = as.getLine(lineIndex);
			FBResult result = new FBResult();
			line.getLineResults().setForwardBreedingResult(result);
			line.getLineResults().setName(name);

			int foundMarkers = totalMarkers - as.missingMarkerCount(lineIndex);
			int hetMarkers = as.hetCount(lineIndex);

			result.setDataCount(foundMarkers);
			result.setPercentData((foundMarkers/ (double) totalMarkers) * 100);
			result.setHeterozygousCount(hetMarkers);
			result.setPercentHeterozygous((hetMarkers / (double) foundMarkers) * 100);

			// Store the qtlNames in the results object so the results table can easily use them in column headers
			ArrayList<String> qtlNames = getQtlNames(as);
			result.setHaplotypeNames(qtlNames);

			// Calculate the "partial match" list...how many alleles (as in AA, AT) in the line match the haplotype
			ArrayList<Double> hapPartialMatchList = calcHapPartialMatchList(as, markerIndicesByQtlName, lineIndex);
			result.setHaplotypePartialMatch(hapPartialMatchList);

			// A check if both phases of each allele matches the haplotype string
			ArrayList<Integer> hapAlleleCountList = calcHapAlleleCountList(as, markerIndicesByQtlName, lineIndex);
			result.setHaplotypeAlleleCounts(hapAlleleCountList);

			// Below are additional statistics which can be calculated from those derived above...

			ArrayList<Double> hapMatch = hapAlleleCountList.stream()
				.mapToDouble(alleleCount -> alleleCount / 2f)
				.boxed()
				.collect(Collectors.toCollection(ArrayList::new));
			result.setHaplotypeMatch(hapMatch);

			ArrayList<Double> hapWeight = hapMatch.stream()
//				.mapToDouble(score -> Double.compare(score, 0.5d) == 0 ? 0.6d : score)
//				.boxed()
				.collect(Collectors.toCollection(ArrayList::new));
			result.setHaplotypeWeight(hapWeight);

			double averageWeightedHapMatch = hapWeight.stream()
				.mapToDouble(Double::doubleValue)
				.average()
				.orElse(Double.NaN);
			result.setAverageWeightedHapMatch(averageWeightedHapMatch);

			double averageHapMatch = hapMatch.stream()
				.mapToDouble(Double::doubleValue)
				.average()
				.orElse(Double.NaN);
			result.setAverageHapMatch(averageHapMatch);
		});
	}

	// Generate a map of qtl names to lists of markerIndices of markers under that qtl. This allows us to then use
	// the FavAlleleManager to pull out the favourable alleles for a qtl so that we can calculate the haplotype
	// matches for the forward breeding analysis
	private HashMap<String, ArrayList<Integer>> getMarkersByQtlMap()
	{
		HashMap<String, ArrayList<Integer>> markersByQtlName = new HashMap<>();

		for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
		{
			if (as.getGTView(viewIndex).getChromosomeMap().isSpecialChromosome())
				continue;

			for (QTLInfo qtlInfo : as.qtls(viewIndex))
			{
				QTL qtl = qtlInfo.getQTL();

				ArrayList<Integer> markerIndices = new ArrayList<>();

				// The code below is adapted from the code in the map canvas...only we can remove its additional check
				// as we won't be running the analysis on the special chromosome
				for (int markerIndex = 0; markerIndex < as.markerCount(viewIndex); markerIndex++)
				{
					Marker marker = as.getMarker(viewIndex, markerIndex).getMarker();

					if (marker.getRealPosition() >= qtl.getMin() && marker.getRealPosition() <= qtl.getMax())
						markerIndices.add(markerIndex);
				}

				markersByQtlName.put(qtl.getName(), markerIndices);
			}
		}
		return markersByQtlName;
	}

	// Loops over each view's QTLs to generate a list of QTL names
	private ArrayList<String> getQtlNames(AnalysisSet as)
	{
		ArrayList<String> qtlNames = new ArrayList<>();

		for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
		{
			for (QTLInfo info : as.qtls(viewIndex))
			{
				String qtlName = info.getQTL().getName();
				qtlNames.add(qtlName);
			}
		}

		return qtlNames;
	}

	// Calculate the "partial match" list...how many alleles (as in AA, AT) in the line match the haplotype
	private ArrayList<Double> calcHapPartialMatchList(AnalysisSet as, HashMap<String, ArrayList<Integer>> markersByQtlName, int lineIndex)
	{
		ArrayList<Double> hapPartialMatchList = new ArrayList<>();

		for (int view = 0; view < as.viewCount(); view++)
		{
			for (QTLInfo info : as.qtls(view))
			{
				String qtlName = info.getQTL().getName();
				ArrayList<Integer> qtlMarkerIndices = markersByQtlName.get(qtlName);

				int alleleMatchScore = 0;
				for (int marker : qtlMarkerIndices)
				{
					// Get the allele state of the allele at this line and marker
					AlleleState lineState = stateTable.getAlleleState(as.getState(view, lineIndex, marker));

					// This gets a list of statetable indices for this haplotype / qtl
					String markerName = as.getMarker(view, marker).getMarker().getName();
					ArrayList<Integer> markerFavAlleles = favAlleleManager.haplotypeAlleles(qtlName, markerName);

					if (markerFavAlleles != null && !markerFavAlleles.isEmpty())
					{
						AlleleState hapState = stateTable.getAlleleState(markerFavAlleles.get(0));

						// If the line's allele completely matches the haplotype allele, that means we have two matching
						// alleles at this location
						if (lineState.matches(hapState))
							alleleMatchScore += 2;
						// If we have a partial match, we only have one matching allele at this location
						else if (lineState.matchesAnyAllele(hapState))
							alleleMatchScore += 1;
					}
				}

				// The calculation works on the basis that we have diploid alleles, then divides by 2
				double hapPartialMatch = alleleMatchScore / (double) (qtlMarkerIndices.size() * 2);
				hapPartialMatchList.add(hapPartialMatch);
			}
		}

		return hapPartialMatchList;
	}

	// Calculate a measure of how many phases of each allele, matches the haplotype allele. We do this by generating
	// ths full string for each phase of the lines allele and doing string equality checks against the haplotype string
	private ArrayList<Integer> calcHapAlleleCountList(AnalysisSet as, HashMap<String, ArrayList<Integer>> markersByQtlName, int lineIndex)
	{
		ArrayList<Integer> hapAlleleCountList = new ArrayList<>();
		for (int view = 0; view < as.viewCount(); view++)
		{
			for (QTLInfo info : as.qtls(view))
			{
				String qtlName = info.getQTL().getName();
				ArrayList<Integer> qtlMarkerIndices = markersByQtlName.get(qtlName);

				// String builders to store each full length phased genotype
				StringBuilder linePhase1 = new StringBuilder();
				StringBuilder linePhase2 = new StringBuilder();
				StringBuilder hapPhase1 = new StringBuilder();

				// Loop over each marker, generating the phased genotype strings for the line and the haplotype
				for (int marker : qtlMarkerIndices)
				{
					int lineStateIndex = as.getState(view, lineIndex, marker);
					AlleleState lineState = stateTable.getAlleleState(lineStateIndex);
					linePhase1.append(lineState.getState(0));
					linePhase2.append(lineState.isHomozygous() ? lineState.getState(0) : lineState.getState(1));

					String markerName = as.getMarker(view, marker).getMarker().getName();
					ArrayList<Integer> markerFavAlleles = favAlleleManager.haplotypeAlleles(qtlName, markerName);
					if (markerFavAlleles != null && !markerFavAlleles.isEmpty())
					{
						AlleleState hapState = stateTable.getAlleleState(markerFavAlleles.get(0));
						hapPhase1.append(hapState.getState(0));
					}
				}

				// For each phased genotype of the line that matches the haplotype, increment the count
				int hapAlleleCount = 0;
				if (linePhase1.toString().equals(hapPhase1.toString()))
					hapAlleleCount++;
				if (linePhase2.toString().equals(hapPhase1.toString()))
					hapAlleleCount++;

				hapAlleleCountList.add(hapAlleleCount);
			}
		}

		return hapAlleleCountList;
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

		int id = dataSet.getNavPanelCounts().getOrDefault("forwardBreedingCount", 0) + 1;
		dataSet.getNavPanelCounts().put("forwardBreedingCount", id);
		viewSet.setName(RB.format("gui.MenuAnalysis.forwardbreeding.view", id));

		// Add the results viewset to the dataset
		dataSet.getViewSets().add(viewSet);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }
}