// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
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

	// Constructor used by the Traits Heatmap (Menu) to auto run sorts without
	// the full GUI being presented to the user first
	public SortLinesByTrait(GTViewSet viewSet, int trait, boolean asc)
	{
		super(viewSet);

		this.traits = new int[] { trait };
		this.asc = new boolean[] { asc };
		this.autoAssign = false;
	}

	@Override
	protected ArrayList<LineInfo> doSort(GTView view)
	{
		int numLines = view.lineCount();
		ArrayList<LineScore> scores = new ArrayList<>(numLines);
		for (int i = 0; i < numLines && okToRun; i++, linesScored++)
			// Don't do a trait sort on a splitter line
			if (!view.isSplitter(i))
				scores.add(new LineScore(view.getViewSet().getLines().get(i)));

		// Now sort the array based on those scores
		Collections.sort(scores);

		// Then create a new line ordering for the view
		ArrayList<LineInfo> lineOrder = new ArrayList<>(numLines);
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
		// The tv[] array will hold TraitValue objects that match the traits[]
		// being compared, eg if the indexes passed in are 5 and 8 then the tv
		// object will store data from line.getTraitValues()[5] and [8]
		private TraitValue[] tv;

		LineScore(LineInfo lineInfo)
		{
			this.lineInfo = lineInfo;

			// Get at the trait data for this line
			Line line = lineInfo.getLine();
			ArrayList<TraitValue> traitValues = line.getTraitValues();

			// Get the values
			tv = new TraitValue[traits.length];

			// And cache them for easy access
			for (int i = 0; i < traits.length; i++)
				tv[i] = traitValues.get(traits[i]);
		}

		public int compareTo(LineScore other)
		{
			// This is a recursive procedure, which we start by calling with a
			// traitIndex of 0 so that the two lines are compared on the first
			// selected trait...
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
			if (result == 0 && (i < traits.length-1))
				return compareTo(other, i+1);

			return result;
		}
	}
}