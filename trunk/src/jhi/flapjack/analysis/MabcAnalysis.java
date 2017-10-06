// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.pedigree.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

/**
 * Marker assisted backcrossing.
 * We calculate 'gaps' between markers, including chrStart to first marker and
 * last marker to chrEnd as well. No gap is allowed to be bigger than the
 * maxMarkerCoverage variable.
 * For each line, we look at its alleles across the markers, and assign RPP
 * scores based on whether the allele is A or H, using the gap/coverage before
 * and after each allele's marker.
 */
public class MabcAnalysis extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;

	private HashMap<QTLInfo, QTLParams> qtlHash = new HashMap<>();

	private double maxMarkerCoverage;

	private int rpIndex = 0;
	private int dpIndex = 1;
	private boolean excludeAdditionalParents;

	private boolean simpleStats;

	private String name;

	public MabcAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, double maxMarkerCoverage, int rpIndex, int dpIndex, boolean excludeAdditionalParents, boolean simpleStats, String name)
	{
		this(viewSet, selectedChromosomes, maxMarkerCoverage, rpIndex, dpIndex, excludeAdditionalParents, simpleStats);
		this.name = name;
	}

	public MabcAnalysis(GTViewSet viewSet, boolean[] selectedChromosomes, double maxMarkerCoverage, int rpIndex, int dpIndex,  boolean excludeAdditionalParents, boolean simpleStats)
	{
		this.viewSet = viewSet.createClone("", true);;
		this.selectedChromosomes = selectedChromosomes;
		this.maxMarkerCoverage = maxMarkerCoverage;
		this.rpIndex = rpIndex;
		this.dpIndex = dpIndex;
		this.excludeAdditionalParents = excludeAdditionalParents;
		this.simpleStats = simpleStats;

		setupAnalysis();
	}

	private void setupAnalysis()
	{
		// If the user has specified that only the parents used for the analysis
		// should be included in the results and the view
		if (excludeAdditionalParents)
		{
			PedManager pedMan = viewSet.getDataSet().getPedManager();

			// Iterate backward over the viewSet so we can remove any parents
			// that we need to
			for (int i = viewSet.getLines().size() - 1; i >= 0; i--)
			{
				// Don't remove the selected rp and dp
				if (i == rpIndex || i == dpIndex)
					continue;

				if (pedMan.isParent(viewSet.getLines().get(i)))
				{
					viewSet.getLines().remove(i);

					// If the removed parent is before rp, or dp in the viewSet
					// we need to adjust the rpIndex and dpIndex
					if (i < rpIndex)
						rpIndex--;

					if (i < dpIndex)
						dpIndex--;
				}
			}
		}
	}

	public void runJob(int index)
		throws Exception
	{
		// This analysis will run on selected lines/markers only
		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(selectedChromosomes)
			.withSelectedLines()
			.withSelectedMarkers();

		calculateRPP(as);
		calculateLinkageDrag(as);
		prepareForVisualization();
	}

	// Searchs backwards through a line's worth of allele data (for a single
	// chromosome, returning the index of the marker closest to the end of the
	// chromosome that is usable (no missing data etc)
	private int findLastUsableMarker(AnalysisSet as, int viewIndex, int lineIndex)
		throws Exception
	{
		for (int mrkIndex = as.markerCount(viewIndex)-1; mrkIndex >= 0; mrkIndex--)
			if (as.getState(viewIndex, lineIndex, mrkIndex) != 0)
				return mrkIndex;

		return -1;
	}

	private void calculateRPP(AnalysisSet as)
		throws Exception
	{
		StateTable st = viewSet.getDataSet().getStateTable();

		// For each line that we need to calculate stats for...
		for (int lineIndex = 0; lineIndex < as.lineCount(); lineIndex++)
		{
			LineInfo line = as.getLine(lineIndex);
			MabcResult stats = new MabcResult(line);
			line.getResults().setMabcResult(stats);
			line.getResults().setName(name);

			// ...loop over each chromosome and work out RPP for it
			for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			{
				MabcChrScore chrScore = new MabcChrScore();
				chrScore.view = as.getGTView(viewIndex);
				stats.getChrScores().add(chrScore);

				// The index of the previous marker used (eg wasn't an allele
				// with missing data at that index)
				int prevMrkIndex = -1;
				// The index of the last (ie, closest to chromosome-end) marker
				// that is usable (no missing data etc)
				int lastMrkIndex = findLastUsableMarker(as, viewIndex, lineIndex);


				for (int mrkIndex = 0; mrkIndex < as.markerCount(viewIndex); mrkIndex++)
				{
					MarkerInfo marker = as.getMarker(viewIndex, mrkIndex);

					// Can we use this marker? We'll skip any that are missing
					int state = as.getState(viewIndex, lineIndex, mrkIndex);
					if (state == 0)
						continue;

					// What states do the parents have at this marker?
					int rp = as.getState(viewIndex, rpIndex, mrkIndex);
					int dp = as.getState(viewIndex, dpIndex, mrkIndex);
					// If they're missing, monomorphic, or het, skip this position
					if (rp == 0 || dp == 0 || rp == dp || st.isHet(rp) || st.isHet(dp))
						continue;


					double gap = 0;
					double gapEnd = 0;

					// Add gap from 0 to first (usable) marker's position
					if (prevMrkIndex == -1)
					{
						double pos = marker.position();
						gap = Math.min(pos, maxMarkerCoverage);
					}

					// For every other marker, add gap between marker and previous
					else if (mrkIndex < as.markerCount(viewIndex))
					{
						double pos1 = marker.position();
						double prevPos = as.getMarker(viewIndex, prevMrkIndex).position();

						gap = Math.min(pos1 - prevPos, maxMarkerCoverage);
					}

					// Add gap from last (usable) marker to chromosome's end/length
					if (mrkIndex == lastMrkIndex)
					{
						double chrLength = as.mapLength(viewIndex);
						double dist = chrLength - marker.position();

						gapEnd = Math.min(dist, maxMarkerCoverage);
					}

					if (simpleStats)
					{
						gap = 1;
						gapEnd = 0;
					}


					chrScore.coverage += (gap + gapEnd);


					if (prevMrkIndex == -1)
					{
						if (st.isHet(state))
						{
							chrScore.sumRP += gap/2.0;
							chrScore.sumDO += gap/2.0;
						}
						else if (state == rp)
							chrScore.sumRP += gap;
						else
							chrScore.sumDO += gap;
					}

					else if (mrkIndex < as.markerCount(viewIndex))
					{
						if (st.isHet(state))
						{
							chrScore.sumRP += gap/4.0;
							chrScore.sumDO += gap/4.0;
						}
						else if (state == rp)
							chrScore.sumRP += gap/2.0;
						else
							chrScore.sumDO += gap/2.0;


						int statePrev = as.getState(viewIndex, lineIndex, prevMrkIndex);
						int pStatePrev = as.getState(viewIndex, rpIndex, prevMrkIndex);

						if (st.isHet(statePrev))
						{
							chrScore.sumRP += gap/4.0;
							chrScore.sumDO += gap/4.0;
						}
						else if (statePrev == pStatePrev)		// Do we worry about pStatePrev potentially being missing/het?
							chrScore.sumRP += gap/2.0;			// Perhaps not, as this 'else if' catchs hom-prev vs anything-prev-parent, and if the parent was missing/het then it won't match anyway
						else
							chrScore.sumDO += gap/2.0;
					}

					if (mrkIndex == lastMrkIndex)
					{
						if (st.isHet(state))
						{
							chrScore.sumRP += gapEnd/2.0;
							chrScore.sumDO += gapEnd/2.0;
						}
						else if (state == rp)
							chrScore.sumRP += gapEnd;
						else
							chrScore.sumDO += gapEnd;
					}

					prevMrkIndex = mrkIndex;
				}

				// Update the overal genome coverage
				stats.updateAndAddGenomeCoverage(chrScore.coverage);
			}

			double genomeLength = 0;
			for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
				genomeLength += as.mapLength(viewIndex);

			double rppTotal = 0;
			for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			{
				MabcChrScore chrScore = stats.getChrScores().get(viewIndex);

				// Calculate RPP Total for this line
				rppTotal += chrScore.sumRP;
				// Update the stored RP values to be ??? percentages?
				chrScore.sumRP *= (1.0/chrScore.coverage);
			}

			// Update rppTotal to be a percentage of genome coverage (?)
			rppTotal *= (1.0/stats.getGenomeCoverage());
			stats.setRppTotal(rppTotal);

			if (simpleStats)
				stats.setGenomeCoverage(1);
			else
				stats.setGenomeCoverage(stats.getGenomeCoverage()/genomeLength);
		}
	}

	private void calculateLinkageDrag(AnalysisSet as)
	{
		StateTable st = viewSet.getDataSet().getStateTable();

		for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			indexQTLs(as, viewIndex);


		// For each line in the dataset
		for (int lineIndex = 0; lineIndex < as.lineCount(); lineIndex++)
		{
			// Get its MABC stats collector thing
			LineInfo line = as.getLine(lineIndex);
			MabcResult stats = line.getResults().getMabcResult();

			// For each QTL (across each of the chromosomes)
			for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			{
				ArrayList<MarkerInfo> markers = as.getMarkers(viewIndex);

				for (QTLInfo qtlInfo: as.qtls(viewIndex))
				{
					QTLParams qtl = qtlHash.get(qtlInfo);
					if (qtl == null)
						continue;

					MabcQtlScore score = new MabcQtlScore(qtlInfo);
					stats.getQtlScores().add(score);

					// Calculate drag to left
					// Increase drag by the distance between this marker and its
					// left-neighbour (or chrStart), until neighbour is from DP (eg "A")
					for (int m = qtl.LM; m >= 0; m--)
					{
						if (m > 0 && as.getState(viewIndex, lineIndex, m-1) == as.getState(viewIndex, rpIndex, m-1))
							break;
						if (m == 0)
							score.drag += markers.get(m).position();
						else
							score.drag += markers.get(m).position()-markers.get(m-1).position();
					}

					// Calculate drag to the right
					for (int m = qtl.RM; m < markers.size(); m++)
					{
						if (m <= markers.size()-2 && as.getState(viewIndex, lineIndex, m+1) == as.getState(viewIndex, rpIndex, m+1))
							break;
						if (m == markers.size()-1)
							score.drag += as.mapLength(viewIndex)-markers.get(m).position();
						else
							score.drag += markers.get(m+1).position()-markers.get(m).position();
					}


					// Finally, confirm status
					boolean isHomozygous = true;
					boolean isQTLPresent = true;
					for (int m = qtl.LM; m <= qtl.RM; m++)
					{
						int allele = as.getState(viewIndex, lineIndex, m);
						int rp = as.getState(viewIndex, rpIndex, m);
						int dp = as.getState(viewIndex, dpIndex, m);

						// If QTL is from DP and DP is missing or het, skip this marker
						if (qtl.isDP && dp == 0 || st.isHet(dp))
							continue;
						// Or if QTL is from RP and RP is missing or het, skip this marker
						else if (qtl.isRP && rp == 0 || st.isHet(rp))
							continue;

						// If DP and RP are monomorphic, skip this marker
						if (dp == rp)
						{
							// If the allele doesn't match, then the QTL isn't present
							if (allele != dp && allele != rp)
							{
								isQTLPresent = false;
								break;
							}
							continue;
						}

						// If the line is hom, but doesn't match the QTL source then the QTL isn't present
						if (allele != 0 && st.isHom(allele) && ((qtl.isDP && allele == rp) || (qtl.isRP && allele == dp)))
						{
							isQTLPresent = false;
							break;
						}

						// If the allele is het, then isHomozygous is false
						if (st.isHet(allele))
							isHomozygous = false;

						// The QTL is also not present if:
						//   allele is missing, or
						//   qtl is from DP and allele is RP, or
						//   qtl is from RP and allele is DP
						else if (allele == 0 || qtl.isDP && allele == rp || qtl.isRP && allele == dp)
						{
							isQTLPresent = false;
							break;
						}
					}

					// If the line is fully hom for this QTL, score = 2
					if (isHomozygous && isQTLPresent)
						score.status = 2;
					// If the line has any hets for this QTL, score = 1
					else if (!isHomozygous && isQTLPresent)
						score.status = 1;
					else
						score.status = 0;

					stats.setQtlStatusCount(stats.getQtlStatusCount()+score.status);
				}
			}
		}
	}

	// Build a lookup table for each QTL, that tracks the left-most and
	// right-most markers under its region
	// THIS NEEDS SERIOUS OPTIMIZATION
	// Too many iterations over the markers array - want to loop once ideally
	private void indexQTLs(AnalysisSet as, int viewIndex)
	{
		ArrayList<MarkerInfo> markers = as.getMarkers(viewIndex);

		for (QTLInfo qtl: as.qtls(viewIndex))
		{
			QTLParams params = new QTLParams();

			// Work out the QTL's source value
			String source = qtl.getQTL().valueOf("Source");
			// If it's not there, we can't use it
			if (source == null || !source.equalsIgnoreCase("DP") || !source.equalsIgnoreCase("DP"))
				continue;
			// If it's not visible on the display we shouldn't use it
			if (qtl.getQTL().isVisible() == false)
				continue;
			// But if it is, is the QTL from DP or RP
			params.isRP = !(params.isDP = source.equalsIgnoreCase("DP"));


			double min = qtl.getQTL().getMin();
			double max = qtl.getQTL().getMax();

			// Array indices of the left and right flanking markers
			for (int m = 0; m < markers.size(); m++)
			{
				double pos = markers.get(m).position();

				if (pos >= min && params.LM == -1)
					params.LM = m;
				if (pos >= min && pos <= max)
					params.RM = m;

				// Quit searching once beyond this QTL's region
				if (pos > max)
					break;
			}

			// If there are no markers under this QTL, skip it for now... check with Kelly for options
			if (params.LM != -1 && params.RM != -1)
				qtlHash.put(qtl, params);
		}
	}

	private void prepareForVisualization()
	{
		prepareParentsForVisualization();
		changeColourScheme();
		addViewSetToDataSet();
	}

	private void prepareParentsForVisualization()
	{
		// Mark the parents lines as sortToTop special cases
		viewSet.getLines().get(rpIndex).getResults().setSortToTop(true);
		viewSet.getLines().get(dpIndex).getResults().setSortToTop(true);

		// Move the parent lines to the top of the display
		LineInfo rp = viewSet.getLines().get(rpIndex);
		LineInfo dp = viewSet.getLines().get(dpIndex);
		viewSet.getLines().remove(rp);
		viewSet.getLines().remove(dp);
		viewSet.getLines().add(0, rp);
		viewSet.getLines().add(1, dp);
	}

	private void changeColourScheme()
	{
		// Set the colour scheme to LINE_SIMILARITY and set the comparison line to the recurrent parent
		viewSet.setColorScheme(ColorScheme.LINE_SIMILARITY);
		viewSet.setComparisonLineIndex(0);
		viewSet.setComparisonLine(viewSet.getLines().get(0).getLine());
	}

	private void addViewSetToDataSet()
	{
		DataSet dataSet = viewSet.getDataSet();

		// Create titles for the new view and its results table
		int id = dataSet.getMabcCount() + 1;
		dataSet.setMabcCount(id);
		viewSet.setName(RB.format("gui.MenuAnalysis.mabc.view", id));

		// Add the results viewset to the dataset
		dataSet.getViewSets().add(viewSet);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }

	private static class QTLParams
	{
		int LM = -1;	// index of left marker under the QTL (flanking?)
		int RM = -1;	// index of right marker under the QTL
		boolean isDP;	// "Source" tag set to "DP" or not
		boolean isRP;	// "Source" tag set to "RP" or not
	}
}