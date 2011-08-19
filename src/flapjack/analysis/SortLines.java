package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

import scri.commons.gui.*;

abstract class SortLines extends SimpleJob
{
	protected GTViewSet viewSet;
	protected int linesScored = 0;

	public SortLines(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		maximum = viewSet.getView(0).getLineCount();
	}

	@Override
	public int getValue()
		{ return linesScored; }

	public void runJob(int jobNum)
	{
		// Access the first chromosome (just to get at the lines data)
		GTView view = viewSet.getView(0);

		int splitter = view.getSplitterIndex();

		// Store the lines up to the splitter for later use
		ArrayList<LineInfo> splitLines = new ArrayList<LineInfo>();
		for (int i=0; i <= splitter; i++)
			splitLines.add(view.getLineInfo(i));

		int numLines = view.getLineCount();

		// Sort the lines based on some criteria
		ArrayList<LineInfo> lineOrder = doSort(view, numLines);

		// Remove the lines up to the splitter from the arraylist, then add them
		// at the start again
		lineOrder.removeAll(splitLines);
		lineOrder.addAll(0, splitLines);

		// Pass the sorted order back to the view
		view.getViewSet().setLinesFromArray(lineOrder.toArray(new LineInfo[numLines]), true);
	}

	protected abstract ArrayList<LineInfo> doSort(GTView view, int numLines);
}