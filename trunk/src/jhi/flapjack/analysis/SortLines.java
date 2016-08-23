// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

abstract class SortLines extends SimpleJob
{
	protected GTViewSet viewSet;
	protected int linesScored = 0;

	public SortLines(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
		maximum = viewSet.getView(0).lineCount();
	}

	@Override
	public int getValue()
		{ return linesScored; }

	public void runJob(int jobNum)
	{
		// Access the first chromosome (just to get at the lines data)
		GTView view = viewSet.getView(0);

		// Make sure any dummy lines have been stripped out before sorting
		viewSet.removeAllDummyLines();

		int splitter = view.getSplitterIndex();

		// Store the lines up to the splitter for later use
		ArrayList<LineInfo> splitLines = new ArrayList<>();
		for (int i=0; i <= splitter; i++)
			splitLines.add(view.getLineInfo(i));

		// Sort the lines based on some criteria
		ArrayList<LineInfo> lineOrder = doSort(view);

		if (okToRun)
		{
			// Remove the lines up to the splitter from the arraylist, then add them
			// at the start again
			lineOrder.removeAll(splitLines);
			lineOrder.addAll(0, splitLines);

			// Pass the sorted order back to the view
			view.getViewSet().setLinesFromArray(lineOrder.toArray(new LineInfo[view.lineCount()]), true);
			view.getViewSet().tableHandler().viewChanged(true);
		}
	}

	protected abstract ArrayList<LineInfo> doSort(GTView view);
}