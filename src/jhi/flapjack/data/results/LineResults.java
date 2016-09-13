// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

/**
 * Holds information on one or more analyses that have been run against a given
 * line (LineInfo).
 */
public class LineResults extends XMLRoot
{
	private LineInfo lineInfo;

	// MABC analysis result
	private MabcResult mabcResult;

	private PedVerF1sResult pedVerF1sResult;
	private PedVerLinesResult pedVerLinesResult;

	// (Hopefully) common variables shared by multiple results types; a line can
	// have a rank (to assist in selection (that itself is held in the LineInfo)
	// and also some brief comments
	private int rank;
	private String comments;

	// If true, specifies that this line should stay at the top of any results
	// tables, regardless of the sort order applied to that table
	private boolean sortToTop;

	public LineResults()
	{
	}

	public LineResults(LineInfo lineInfo)
	{
		this.lineInfo = lineInfo;
	}

	// Methods required for XML serialization

	public LineInfo getLineInfo()
		{ return lineInfo; }

	public void setLineInfo(LineInfo lineInfo)
		{ this.lineInfo = lineInfo;	}

	public MabcResult getMabcResult()
		{ return mabcResult; }

	public void setMabcResult(MabcResult mabcResult)
		{ this.mabcResult = mabcResult; }

	public PedVerF1sResult getPedVerF1sResult()
		{ return pedVerF1sResult; }

	public void setPedVerF1sResult(PedVerF1sResult pedVerF1sResult)
		{ this.pedVerF1sResult = pedVerF1sResult; }

	public PedVerLinesResult getPedVerLinesResult()
		{ return pedVerLinesResult; }

	public void setPedVerLinesResult(PedVerLinesResult pedVerLinesResult)
		{ this.pedVerLinesResult = pedVerLinesResult; }

	public int getRank()
		{ return rank; }

	public void setRank(int rank)
		{ this.rank = rank; }

	public String getComments()
		{ return comments; }

	public void setComments(String comments)
		{ this.comments = comments; }

	public boolean isSortToTop()
		{ return sortToTop; }

	public void setSortToTop(boolean sortToTop)
		{ this.sortToTop = sortToTop; }

	// Other methods
}