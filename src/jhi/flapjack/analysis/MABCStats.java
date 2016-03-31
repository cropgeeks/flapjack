// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class MABCStats extends SimpleJob
{
	private GTViewSet viewSet;

	private float maxMarkerCoverage = 10;

	public MABCStats(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	ArrayList<LineStats> initLineStats()
		throws Exception
	{
		// TODO: HANDLE ALL CHROMOSOMES!!!
		int chrCount = viewSet.chromosomeCount();

		ArrayList<LineStats> lineStats = new ArrayList<>();

		for (LineInfo line: viewSet.getLines())
			lineStats.add(new LineStats(chrCount));

		return lineStats;
	}

	public void runJob(int index)
		throws Exception
	{
		// TODO: Deal with dummy lines, markers, etc
		// TODO: Deal with all chromosomes view: marker.position vs marker.getRealPosition
		// TODO: Deal with FJ not having a chr length (as opposed to last marker's pos)

		ArrayList<LineStats> lineStats = initLineStats();

		int A = viewSet.getDataSet().getStateTable().indexOf("A");
		int H = viewSet.getDataSet().getStateTable().indexOf("H");

		// Stores results for each chromosome
		double[] coverage = new double[viewSet.chromosomeCount()];         // <---- ALL CHROMOSOMES
		// Store genome coverage
		double genomeCoverage = 0;

		// For every chromsome
		for (int c = 0; c < viewSet.getViews().size(); c++)
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
					LineStats stats = lineStats.get(j);

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




		for (int c = 0; c < viewSet.chromosomeCount(); c++)
		{

		}





/*		for (ChrStats cStats: chrStats)
		{
			System.out.println("NEW CHR");
			System.out.println("************");
			for (int i = 0; i < cStats.sums.size(); i++)
			{
				Sums sums = cStats.sums.get(i);



//				double col5 = sums.sumRP * (1.0/stats.genomeCoverage);
//				System.out.println("col5 = " + col5);

				sums.sumRP *= 1.0/cStats.coverage;
				System.out.println(sums.sumRP);
			}
		}
*/
		for (LineStats lStats: lineStats)
		{
			// Calculate RPP Total for this line
			for (double d: lStats.sumRP)
				lStats.rppTotal += d;

			lStats.rppTotal *= (1.0/genomeCoverage);

			// Update the stored RP values to be ??? percentages?
			for (int c = 0; c < lStats.sumRP.size(); c++)
			{
				double value = lStats.sumRP.get(c);
				lStats.sumRP.set(c, value * (1.0/coverage[c]));
			}

			System.out.println(lStats + "\t" + (genomeCoverage/genomeLength));
		}

		genomeCoverage /= genomeLength;
		System.out.println("COVERAGE: " + genomeCoverage);
	}

	/*

	#dividing sums by length of covered genome for each chromosome and the total
	for(i in c(1:nChr)){
		sumRP[,(nChr+1)]=sumRP[,(nChr+1)]+(sumRP[,i]*(1/sum(covered)))
		sumRP[,i]=sumRP[,i]*1/covered[i]
		sumDonor[,(nChr+1)]=sumDonor[,(nChr+1)]+(sumDonor[,i] *(1/sum(covered)))
		sumDonor[,i]=sumDonor[,i]*1/covered[i]
	}
		#calculating coverage by dividing covered genome by total genome length
		sumRP[,(nChr+2)]=sum(covered/sumTotal)
		sumDonor[,(nChr+2)]=sum(covered/sumTotal)

	write(t(sumRP),file="RPP.csv",ncol=length(sumRP[1,]),sep=",")

	*/


	private static class Stats
	{
		// Per chromosome RPPs for every line
		ArrayList<Double> chrRPPs = new ArrayList<>();
		// And a summary total per line
		ArrayList<Double> rppTotals = new ArrayList<>();

		double genomeCoverage;
	}

	private static class Sums
	{
		double sumRP;
		double sumDO;
	}

	private static class LineStats
	{
		// One per chromosome...
		ArrayList<Double> sumRP = new ArrayList<>();
		ArrayList<Double> sumDO = new ArrayList<>();

		double rppTotal;

		LineStats(int chrCount)
		{
			for (int i = 0; i < chrCount; i++)
			{
				sumRP.add(0.0);
				sumDO.add(0.0);
			}
		}

		// Fudge to do a += on values held in the ArrayList
		void updateRP(int index, double value)
		{
			sumRP.set(index, sumRP.get(index) + value);
		}

		void updateDO(int index, double value)
		{
			sumDO.set(index, sumDO.get(index) + value);
		}

		public String toString()
		{
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();

			String str = "";
			for (double d: sumRP)
				str += nf.format(d) + "\t";
			str += nf.format(rppTotal);

			return str;
		}
	}
}