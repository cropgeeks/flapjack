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

	private HashMap<QTLInfo, QTLParams> qtlHash = new HashMap<>();

	private float maxMarkerCoverage = 10;

	private ArrayList<MABCLineStats> lineStats = new ArrayList<>();

	public MABCStats(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public ArrayList<MABCLineStats> getLineStats()
		{ return lineStats; }

	private void initLineStats()
		throws Exception
	{
		// TODO: HANDLE ALL CHROMOSOMES!!!
		int chrCount = viewSet.chromosomeCount();

		for (LineInfo line: viewSet.getLines())
			lineStats.add(new MABCLineStats(line, chrCount));
	}

	public void runJob(int index)
		throws Exception
	{
		initLineStats();

		calculateRPP();
		calculateLinkageDrag();
	}

	private void calculateRPP()
		throws Exception
	{
		// TODO: Deal with dummy lines, markers, etc
		// TODO: Deal with all chromosomes view: marker.position vs marker.getRealPosition
		// TODO: Deal with FJ not having a chr length (as opposed to last marker's pos)

		int A = viewSet.getDataSet().getStateTable().indexOf("A");
		int H = viewSet.getDataSet().getStateTable().indexOf("H");

		// Stores results for each chromosome
		double[] coverage = new double[viewSet.chromosomeCount()];         // <---- ALL CHROMOSOMES
		// Store genome coverage
		double genomeCoverage = 0;

		// For every chromsome
		for (int c = 0; c < viewSet.getViews().size(); c++) // <---- ALL CHROMOSOMES
		{
			GTView view = viewSet.getView(c);
			view.cacheLines();

			ArrayList<MarkerInfo> markers = view.selectedMarkersAsList(); //view.getMarkers();
			for (int i=0; i < markers.size(); i++)
			{
				MarkerInfo marker = markers.get(i);

				double gap = 0;
				double gapEnd = 0;

				// Add gap from 0 to first marker's position
				if (i == 0)
				{
					float pos = marker.position();
					gap = Math.min(pos, maxMarkerCoverage);
				}

				// For every other marker, add gap between marker and previous
				else if (i > 0)
				{

					float pos1 = marker.position();
					float prevPos = markers.get(i-1).position();

					gap = Math.min(pos1 - prevPos, maxMarkerCoverage);
				}

				// Add gap from last marker to chromosome's end/length
				if (i == markers.size()-1)
				{

					float chrLength = view.getChromosomeMap().getLength();
					float dist = chrLength - marker.position();

					gapEnd = Math.min(dist, maxMarkerCoverage);
				}

				coverage[c] += (gap + gapEnd);


				ArrayList<LineInfo> lines = view.getViewSet().getLines();
				for (int j = 0; j < lines.size(); j++)
				{
					LineInfo line = lines.get(j);
					MABCLineStats stats = lineStats.get(j);

					if (i == 0)
					{
						int state = view.getState(j, i);

						if (state == A)
							stats.updateRP(c, gap);
						else if (state == H)
						{
							stats.updateRP(c, gap/2.0);
							stats.updateDO(c, gap/2.0);
						}
						else
							stats.updateDO(c, gap);
					}

					else if (i > 0)
					{
						int state = view.getState(j, i);

						if (state == A)
							stats.updateRP(c, gap/2.0);
						else if (state == H)
						{
							stats.updateRP(c, gap/4.0);
							stats.updateDO(c, gap/4.0);
						}
						else
							stats.updateDO(c, gap/2.0);


						int statePrev = view.getState(j, i-1);
						if (statePrev == A)
							stats.updateRP(c, gap/2.0);
						else if (statePrev == H)
						{
							stats.updateRP(c, gap/4.0);
							stats.updateDO(c, gap/4.0);
						}
						else
							stats.updateDO(c, gap/2.0);
					}

					if (i == markers.size()-1)
					{
						int state = view.getState(j, i);

						if (state == A)
							stats.updateRP(c, gapEnd);
						else if (state == H)
						{
							stats.updateRP(c, gapEnd/2.0);
							stats.updateDO(c, gapEnd/2.0);
						}
						else
							stats.updateDO(c, gapEnd);
					}
				}
			}

			// Update the overal genome coverage
			genomeCoverage += coverage[c];
		}

		float genomeLength = 0;
		for (GTView view: viewSet.getViews())
			genomeLength += view.mapLength();


		for (MABCLineStats lStats: lineStats)
		{
			// Calculate RPP Total for this line
			double rppTotal = 0;
			for (double d: lStats.getSumRP())
				rppTotal += d;

			rppTotal *= (1.0/genomeCoverage);
			lStats.setRppTotal(rppTotal);
			lStats.setCoverage(genomeCoverage/genomeLength);

			// Update the stored RP values to be ??? percentages?
			for (int c = 0; c < lStats.getSumRP().size(); c++)
			{
				double value = lStats.getSumRP().get(c);
				lStats.getSumRP().set(c, value * (1.0/coverage[c]));
			}

//			System.out.println(lStats + "\t" + (genomeCoverage/genomeLength));
		}

		// TODO : Store globally for this result set
		genomeCoverage /= genomeLength;
	}

	// *****
	// selectedMarkersAsList (or lines etc) only works until we do view.getState(line, marker)
	// at which point the indices will all be wrong. MORE TO THINK ABOUT!!!
	// Could we build an AnalysisView wrapper that is given lists of lines, markers, etc
	// and duplicates hte functionality of view.getState() but obviously only
	// using the subsetted arrays rather than the full view??
	// *****

	private void calculateLinkageDrag()
	{
		int A = viewSet.getDataSet().getStateTable().indexOf("A");

		for (GTView view: viewSet.getViews())
			indexQTLs(view);


		// For each line in the dataset
		for (int j = 0; j < lineStats.size(); j++)
		{
			// Get its MABC stats collector thing
			MABCLineStats stats = lineStats.get(j);

			// For each QTL (across each of the chromosomes)
			for (GTView view: viewSet.getViews()) // <---- ALL CHROMOSOMES
			{
				ArrayList<MarkerInfo> markers = view.selectedMarkersAsList();

				for (QTLInfo qtl: view.visibleQTLsAsList())
				{
					QTLParams p = qtlHash.get(qtl);
					if (p == null)
						continue;

					MABCLineStats.QTLScore score = new MABCLineStats.QTLScore();
					stats.getQTLScores().add(score);

					// Calculate drag to left
					// Increase drag by the distance between this marker and its
					// left-neighbour (or chrStart), until neighbour is from DP (eg "A")
					for (int m = p.LM; m >= 0; m--)
					{
						if (m > 0 && view.getState(j, m-1) == A)
							break;
						if (m == 0)
							score.drag += markers.get(m).position();
						else
							score.drag += markers.get(m).position()-markers.get(m-1).position();
					}

					// Calculate drag to the right
					for (int m = p.RM; m < markers.size(); m++)
					{
						if (m < markers.size()-2 && view.getState(j, m+1) == A)
							break;
						if (m == markers.size()-1)
							score.drag += view.getChromosomeMap().getLength()-markers.get(m).position();
						else
							score.drag += markers.get(m+1).position()-markers.get(m).position();
					}

					// Finally, confirm status
					for (int m = p.LM; m <= p.RM; m++)
					{
						int allele = view.getState(j, m);
						if (p.isDP && allele == A || !p.isDP && allele != A)
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
	private void indexQTLs(GTView view)
	{
		ArrayList<MarkerInfo> markers = view.selectedMarkersAsList();

		for (QTLInfo qtl: view.visibleQTLsAsList())
		{
			QTLParams params = new QTLParams();

			// Work out the QTL's source value
			String source = qtl.getQTL().valueOf("Source");
			// If it's not there, we can't use it
			if (source == null)
				continue;
			if (source.equalsIgnoreCase("DP"))
				params.isDP = true;


			float min = qtl.getQTL().getMin();
			float max = qtl.getQTL().getMax();

			// Array indices of the left and right flanking markers
			for (int m = 0; m < markers.size(); m++)
			{
				float pos = markers.get(m).position();

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