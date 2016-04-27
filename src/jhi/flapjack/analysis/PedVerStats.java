// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.io.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.Flapjack;

import scri.commons.gui.*;

public class PedVerStats extends SimpleJob
{
	private GTViewSet viewSet;

	private ArrayList<Score> scores = new ArrayList<>();

	public PedVerStats(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	// Code copied from simmatrix for refernece
	private boolean skipLine(int i, GTView view)
	{
		// Ignore the awkward cases
		if (view.isDummyLine(i) || view.isSplitter(i) || view.isDuplicate(i))
			return true;
		// Or unselected lines
//		if (view.isLineSelected(i) == false)
//			return true;

		return false;
	}

	public void runJob(int index)
		throws Exception
	{
		// Precalculate which allele states are hom or het
		StateTable st = viewSet.getDataSet().getStateTable();

		int lineCount = viewSet.getLines().size();
		for (int line = 0; line < lineCount; line++)     // <-- selectedLinesAsArray ??
		{
			if (skipLine(line, viewSet.getViews().get(0))) // < ffs iain what a mess :)
				continue;

			Score score = new Score(viewSet.getLines().get(line));
			scores.add(score);

			for (GTView view: viewSet.getViews())
			{
				int markerCount = view.markerCount();
				for (int marker = 0; marker < markerCount; marker++)
				{
					// Don't count markers that aren't selected
					if (view.isMarkerSelected(marker) == false)
						continue;

					int state = view.getState(line, marker);

					if (state == 0)
						score.misCount++;
					else if (st.isHet(state))
						score.hetCount++;
					else
						score.homCount++;

					score.mrkCount++;
				}
			}
		}

		fakeTraits();
	}

	private static class Score
	{
		LineInfo line;

		int mrkCount = 0;
		int hetCount = 0;
		int homCount = 0;
		int misCount = 0;
		float perHet;
		float perHetAdjMis;

		Score(LineInfo line)
		{
			this.line = line;
		}

		void calc()
		{
			perHet = (hetCount / (float)mrkCount) * 100;
			perHetAdjMis = (hetCount / (float)(mrkCount-misCount)) * 100;
		}
	}


	private void fakeTraits()
		throws Exception
	{
		DataSet dataSet = viewSet.getDataSet();
		Flapjack.winMain.getNavPanel().getTraitsPanel(viewSet.getDataSet()).getTraitsPanel().removeAllTraits();

		File tmp = new File("mabctraits");
		BufferedWriter out = new BufferedWriter(new FileWriter(tmp));

		// Headers
		out.write("# fjFile = PHENOTYPE");
		out.newLine();
//		out.write("Line\tMrkCount\tHomCount\tHetCount\tMisCount\t%Het\t%HetAdjMis");
		out.write("Line\tHomCount\tHetCount\tMisCount\t%Het\t%HetAdjMis");
		out.newLine();

		for (Score score: scores)
		{
			score.calc();

			out.write(score.line.name());
//			out.write("\t" + score.mrkCount);
			out.write("\t" + score.homCount);
			out.write("\t" + score.hetCount);
			out.write("\t" + score.misCount);
			out.write("\t" + score.perHet);
			out.write("\t" + score.perHetAdjMis);
			out.newLine();
		}

		out.close();

		Flapjack.winMain.mFile.importTraitData(tmp);
	}
}