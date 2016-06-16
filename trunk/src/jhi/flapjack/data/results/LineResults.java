// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

/**
 * Holds information on one or more analyses that have been run against a given
 * line (LineInfo).
 */
public class LineResults
{
	private LineInfo lineInfo;

	// MABC analysis result
	private MABCLineStats mabcLineStats;

	private PedVerKnownParentsLineStats pedVerStats;

	private PedVerLinesLineStats pedVerLinesStats;

	public LineResults(LineInfo lineInfo)
	{
		this.lineInfo = lineInfo;
	}

	public LineInfo getLineInfo()
		{ return lineInfo; }

	public void setLineInfo(LineInfo lineInfo)
		{ this.lineInfo = lineInfo;	}

	public MABCLineStats getMABCLineStats()
		{ return mabcLineStats; }

	public void setMABCLineStats(MABCLineStats mabcLineStats)
		{ this.mabcLineStats = mabcLineStats; }

	public PedVerKnownParentsLineStats getPedVerStats()
		{ return pedVerStats; }

	public void setPedVerStats(PedVerKnownParentsLineStats pedVerStats)
		{ this.pedVerStats = pedVerStats; }

	public PedVerLinesLineStats getPedVerLinesStats()
		{ return pedVerLinesStats; }

	public void setPedVerLinesStats(PedVerLinesLineStats pedVerLinesStats)
		{ this.pedVerLinesStats = pedVerLinesStats; }
}