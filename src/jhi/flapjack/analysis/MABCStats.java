// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
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
public class MABCStats extends SimpleJob
{
	private GTViewSet viewSet;
	private boolean[] selectedChromosomes;

	private HashMap<QTLInfo, QTLParams> qtlHash = new HashMap<>();

	private double maxMarkerCoverage;

	// hard coded index of the RP line (index of the line minus duplicate, etc)
	int rpIndex = 0;
	int dpIndex = 1;

	boolean simpleStats;

	public MABCStats(GTViewSet viewSet, boolean[] selectedChromosomes, double maxMarkerCoverage, int rpIndex, int dpIndex, boolean simpleStats)
	{
		this.viewSet = viewSet;
		this.selectedChromosomes = selectedChromosomes;
		this.maxMarkerCoverage = maxMarkerCoverage;
		this.rpIndex = rpIndex;
		this.dpIndex = dpIndex;
		this.simpleStats = simpleStats;
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
			MABCLineStats stats = new MABCLineStats(line);
			line.results().setMABCLineStats(stats);

			// ...loop over each chromosome and work out RPP for it
			for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			{
				MABCLineStats.ChrScore chrScore = new MABCLineStats.ChrScore();
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

					// What state does the recurrant parent have at this marker?
					int pState = as.getState(viewIndex, rpIndex, mrkIndex);
					// If it's missing, or het, skip this position
					if (pState == 0 || st.isHet(pState))
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
						else if (state == pState)
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
						else if (state == pState)
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
						else if (state == pState)
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
				MABCLineStats.ChrScore chrScore = stats.getChrScores().get(viewIndex);

				// Calculate RPP Total for this line
				rppTotal += chrScore.sumRP;
				// Update the stored RP values to be ??? percentages?
				chrScore.sumRP *= (1.0/chrScore.coverage);
				if (Double.isNaN(chrScore.sumRP))
					chrScore.sumRP = 0;
			}

			// Update rppTotal to be a percentage of genome coverage (?)
			rppTotal *= (1.0/stats.getGenomeCoverage());
			if (Double.isNaN(rppTotal))
				rppTotal = 0;
			stats.setRppTotal(rppTotal);

			if (simpleStats)
				stats.setGenomeCoverage(1);
			else
				stats.setGenomeCoverage(stats.getGenomeCoverage()/genomeLength);
		}
	}

	private void calculateLinkageDrag(AnalysisSet as)
	{
		for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			indexQTLs(as, viewIndex);


		// For each line in the dataset
		for (int lineIndex = 0; lineIndex < as.lineCount(); lineIndex++)
		{
			// Get its MABC stats collector thing
			LineInfo line = as.getLine(lineIndex);
			MABCLineStats stats = line.results().getMABCLineStats();

			// For each QTL (across each of the chromosomes)
			for (int viewIndex = 0; viewIndex < as.viewCount(); viewIndex++)
			{
				ArrayList<MarkerInfo> markers = as.getMarkers(viewIndex);

				for (QTLInfo qtl: as.qtls(viewIndex))
				{
					QTLParams p = qtlHash.get(qtl);
					if (p == null)
						continue;

					MABCLineStats.QTLScore score = new MABCLineStats.QTLScore(qtl);
					stats.getQTLScores().add(score);

					// Calculate drag to left
					// Increase drag by the distance between this marker and its
					// left-neighbour (or chrStart), until neighbour is from DP (eg "A")
					for (int m = p.LM; m >= 0; m--)
					{
						if (m > 0 && as.getState(viewIndex, lineIndex, m-1) == as.getState(viewIndex, rpIndex, m-1))
							break;
						if (m == 0)
							score.drag += markers.get(m).position();
						else
							score.drag += markers.get(m).position()-markers.get(m-1).position();
					}

					// Calculate drag to the right
					for (int m = p.RM; m < markers.size(); m++)
					{
						if (m <= markers.size()-2 && as.getState(viewIndex, lineIndex, m+1) == as.getState(viewIndex, rpIndex, m+1))
							break;
						if (m == markers.size()-1)
							score.drag += as.mapLength(viewIndex)-markers.get(m).position();
						else
							score.drag += markers.get(m+1).position()-markers.get(m).position();
					}

					// Finally, confirm status
					for (int m = p.LM; m <= p.RM; m++)
					{
						int allele = as.getState(viewIndex, lineIndex, m);
						int rp = as.getState(viewIndex, rpIndex, m);

						if (allele == 0 || p.isDP && allele == rp || !p.isDP && allele != rp)
							score.status = 0;
					}

					// Update the sum of qtl status count (where status == 1)
					if (score.status == 1)
						stats.setQtlStatusCount(stats.getQtlStatusCount()+1);
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
			if (source == null)
				continue;
			if (source.equalsIgnoreCase("DP"))
				params.isDP = true;


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
		// Mark the parents lines as sortToTop special cases
		viewSet.getLines().get(rpIndex).results().setSortToTop(true);
		viewSet.getLines().get(dpIndex).results().setSortToTop(true);

		// Move the parent lines to the top of the display
		viewSet.moveLine(rpIndex, 0);
		viewSet.moveLine(dpIndex, 1);

		// Set the colour scheme to LINE_SIMILARITY and set the comparison line to the recurrent parent
		viewSet.setColorScheme(ColorScheme.LINE_SIMILARITY);
		viewSet.setComparisonLineIndex(0);
		viewSet.setComparisonLine(viewSet.getLines().get(0).getLine());
	}

	private static class QTLParams
	{
		int LM = -1;	// index of left marker under the QTL (flanking?)
		int RM = -1;	// index of right marker under the QTL
		boolean isDP;	// "Source" tag set to "DP" or not
	}
}