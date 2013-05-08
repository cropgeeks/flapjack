// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class SimMatrix extends XMLRoot
{
	// Tracks the indices of the lines originally used to generate this matrix
	private ArrayList<LineInfo> lineInfos = new ArrayList<LineInfo>();

	// The 2D matrix of scores
	private ArrayList<ArrayList<Float>> lineScores;


	public void setLineInfos(ArrayList<LineInfo> lineInfos)
		{ this.lineInfos = lineInfos; }

	public ArrayList<LineInfo> getLineInfos()
		{ return lineInfos; }

	public void setLineScores(ArrayList<ArrayList<Float>> lineScores)
		{ this.lineScores = lineScores; }

	public ArrayList<ArrayList<Float>> getLineScores()
		{ return lineScores; }


	public int size()
	{
		return lineInfos.size();
	}
}