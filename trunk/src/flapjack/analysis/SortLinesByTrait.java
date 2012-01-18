// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SortLinesByTrait extends SortLines
{
	// Trait indices and ascending/decending info
	private int[] traits;
	private boolean[] asc;

	// Auto assign the selected traits to the view's heatmap after the sort?
	private boolean autoAssign;

	public SortLinesByTrait(GTViewSet viewSet, int[] traits, boolean[] asc, boolean autoAssign)
	{
		super(viewSet);

		this.traits = traits;
		this.asc = asc;
		this.autoAssign = autoAssign;
	}

	@Override
	protected ArrayList<LineInfo> doSort(GTView view)
	{
		int numLines = view.lineCount();
		ArrayList<LineScore> scores = new ArrayList<LineScore>(numLines);
		for (int i = 0; i < numLines && okToRun; i++, linesScored++)
			// Don't do a trait sort on a splitter line
			if (!view.isSplitter(view.getViewSet().getLines().get(i).getLine()))
				scores.add(new LineScore(view.getViewSet().getLines().get(i)));

		// Now sort the array based on those scores
		Collections.sort(scores);

		// Then create a new line ordering for the view
		ArrayList<LineInfo> lineOrder = new ArrayList<LineInfo>(numLines);
		for (int i = 0; i < scores.size() && okToRun; i++)
			lineOrder.add(scores.get(i).lineInfo);

		return lineOrder;
	}

	@Override
	public void runJob(int jobIndex)
	{
		super.runJob(jobIndex);

		// Assign the traits to the heatmap?
		if (autoAssign)
			viewSet.setTraits(traits);
	}

	private class LineScore implements Comparable<LineScore>
	{
		private LineInfo lineInfo;
		private TraitValue[] tv;

		LineScore(LineInfo lineInfo)
		{
			this.lineInfo = lineInfo;

			// Get at the trait data for this line
			Line line = lineInfo.getLine();
			ArrayList<TraitValue> traitValues = line.getTraitValues();

			// Get the values
			tv = new TraitValue[traits.length];

			for (int i = 0; i < traits.length; i++)
			{
				// Values can't be retrived for a trait if the index is -1
				if (traits[i] == -1)
					break;

				tv[i] = traitValues.get(traits[i]);
			}
		}

		public int compareTo(LineScore other)
		{
			return compareTo(other, 0);
		}

		public int compareTo(LineScore other, int i)
		{
			// We want lines with undefined traits at the top (ascending) or
			// bottom (descending) of the final sort order

			int result = tv[i].compareTo(other.tv[i]);

			// Flip the result if the order should be descending
			if (asc[i] == false)
				result = result * -1;

			// If the result is equal, can we sort further?
			if (result == 0 && (i < traits.length-1) && traits[i+1] != -1)
				return compareTo(other, i+1);

			return result;
		}
	}
}