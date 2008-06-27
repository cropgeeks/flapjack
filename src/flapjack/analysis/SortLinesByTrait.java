package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class SortLinesByTrait implements ILineSorter
{
	private GTViewSet viewSet;

	// Trait indices and ascending/decending info
	private int[] traits;
	private boolean[] asc;

	private int linesScored = 0;

	public SortLinesByTrait(GTViewSet viewSet, int[] traits, boolean[] asc)
	{
		this.viewSet = viewSet;

		this.traits = traits;
		this.asc = asc;
	}

	public int getMaximum()
		{ return viewSet.getView(0).getLineCount(); }

	public int getValue()
		{ return linesScored; }

	public void doSort()
	{
		// Access the first chromosome (just to get at the lines data)
		GTView view = viewSet.getView(0);

		// Store a local reference to the line ordering for quicker access
		Vector<LineInfo> lines = view.getLines();

		// Create an array to hold the score for each line
		Vector<LineScore> scores = new Vector<LineScore>(lines.size());

		// Work out what those scores are
		for (int i = 0; i < view.getLineCount(); i++, linesScored++)
			scores.add(new LineScore(lines.get(i)));

		// Now sort the array based on those scores
		Collections.sort(scores);

		// Then create a new line ordering for the view
		LineInfo[] lineOrder = new LineInfo[scores.size()];
		for (int i = 0; i < scores.size(); i++)
			lineOrder[i] = scores.get(i).lineInfo;

		// And pass that order back to the view
		view.getViewSet().setLinesFromArray(lineOrder, true);

		// Because we've reordered the view (without it knowing), we MUST let
		// it know that it has to search for its comparison line's new position
		view.updateComparisons();

	}

	private class LineScore implements Comparable<LineScore>
	{
		LineInfo lineInfo;

		private float[] v;
		private boolean[] isDefined;
		private TraitValue[] tv;

		LineScore(LineInfo lineInfo)
		{
			this.lineInfo = lineInfo;

			// Get at the trait data for this line
			Line line = lineInfo.getLine();
			Vector<TraitValue> traitValues = line.getTraitValues();

			// Get the values
			v = new float[traits.length];
			isDefined = new boolean[traits.length];
			tv = new TraitValue[traits.length];

			for (int i = 0; i < traits.length; i++)
			{
				// Values can't be retrived for a trait if the index is -1
				if (traits[i] == -1)
					break;

				tv[i] = traitValues.get(traits[i]);
				v[i] = traitValues.get(traits[i]).getValue();
				isDefined[i] = traitValues.get(traits[i]).isDefined();
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

/*			// If ascending, and this line has no value, then it must go 1st
			if (asc[i] && !isDefined[i])
				return -1;
			// If ascending, and this line has a value but the other doesn't,
			// then it must go 2nd
			if (asc[i] && (isDefined[i] && !other.isDefined[i]))
				return 1;

			// If descending, and this line has no value, then it must go 2nd
			if (!asc[i] && !isDefined[i])
				return 1;
			// If descending, and this line has a value but the other doesn't,
			// then it must go 1st
			if (!asc[i] && (isDefined[i] && !other.isDefined[i]))
				return -1;


			if ((asc[i] && v[i] < other.v[i]) || (!asc[i] && v[i] > other.v[i]))
				return -1;

			// If it's equal (and it's possile to compare another trait)...
			else if (v[i] == other.v[i] && (i < traits.length-1) && traits[i+1] != -1)
				return compareTo(other, i+1);
			// Otherwise it's just equal
			else if (v[i] == other.v[i])
				return 0;

			else
				return 1;
*/
		}
	}
}