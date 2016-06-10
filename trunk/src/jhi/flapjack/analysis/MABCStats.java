// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

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

	private float maxMarkerCoverage = 10;

	// hard coded index of the RP line (index of the line minus duplicate, etc)
	int rpIndex = 0;

	public MABCStats(GTViewSet viewSet, boolean[] selectedChromosomes)
	{
		this.viewSet = viewSet;
		this.selectedChromosomes = selectedChromosomes;
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

				for (int mrkIndex = 0; mrkIndex < as.markerCount(viewIndex); mrkIndex++)
				{
					MarkerInfo marker = as.getMarker(viewIndex, mrkIndex);

					double gap = 0;
					double gapEnd = 0;

					// Add gap from 0 to first marker's position
					if (mrkIndex == 0)
					{
						double pos = marker.position();
						gap = Math.min(pos, maxMarkerCoverage);
					}

					// For every other marker, add gap between marker and previous
					else if (mrkIndex > 0)
					{

						double pos1 = marker.position();
						double prevPos = as.getMarker(viewIndex, mrkIndex-1).position();

						gap = Math.min(pos1 - prevPos, maxMarkerCoverage);
					}

					// Add gap from last marker to chromosome's end/length
					if (mrkIndex == as.markerCount(viewIndex)-1)
					{
						double chrLength = as.mapLength(viewIndex);
						double dist = chrLength - marker.position();

						gapEnd = Math.min(dist, maxMarkerCoverage);
					}

					chrScore.coverage += (gap + gapEnd);

					int state = as.getState(viewIndex, lineIndex, mrkIndex);
					int pState = as.getState(viewIndex, rpIndex, mrkIndex);


					if (mrkIndex == 0)
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

					else if (mrkIndex > 0)
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


						int statePrev = as.getState(viewIndex, lineIndex, mrkIndex-1);
						int pStatePrev = as.getState(viewIndex, rpIndex, mrkIndex-1);

						if (st.isHet(statePrev))
						{
							chrScore.sumRP += gap/4.0;
							chrScore.sumDO += gap/4.0;
						}
						else if (statePrev == pStatePrev)
							chrScore.sumRP += gap/2.0;
						else
							chrScore.sumDO += gap/2.0;
					}

					if (mrkIndex == as.markerCount(viewIndex)-1)
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
			}

			// Update rppTotal to be a percentage of genome coverage (?)
			rppTotal *= (1.0/stats.getGenomeCoverage());
			stats.setRppTotal(rppTotal);
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
						if (p.isDP && allele == rp || !p.isDP && allele != rp)
							score.status = false;
					}

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

	private static class QTLParams
	{
		int LM = -1;	// index of left marker under the QTL (flanking?)
		int RM = -1;	// index of right marker under the QTL
		boolean isDP;	// "Source" tag set to "DP" or not
	}
}