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

	public SimMatrix()
	{
	}

	private SimMatrix(ArrayList<LineInfo> lineInfos, ArrayList<ArrayList<Float>> lineScores)
	{
		this.lineInfos = lineInfos;
		this.lineScores = lineScores;
	}

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

	/**
	 * Returns a StringBuilder object that represents a textual formatting of
	 * the simmatrix object (basically the 2D array as "written out").
	 */
	public StringBuilder createStringMatrix()
	{
		StringBuilder sb1 = new StringBuilder();

		int lineCount = lineInfos.size();

		for (int i = 0; i < lineCount; i++)
			sb1.append((i == 0 ? "":"\t") + lineInfos.get(i).name());
		sb1.append(System.getProperty("line.separator"));

		for (int i = 0; i < lineCount; i++)
		{
			StringBuilder sb2 = new StringBuilder();

			for (int j = 0; j < lineCount; j++)
			{
				if (j <= i)
					sb2.append("\t" + lineScores.get(i).get(j));
				else
					sb2.append("\t" + lineScores.get(j).get(i));
			}

			sb1.append(sb2.toString().trim());
			sb1.append(System.getProperty("line.separator"));
		}

		return sb1;
	}

	public SimMatrix cloneAndReorder(ArrayList<Integer> rIntOrder, ArrayList<LineInfo> newLineOrder)
	{
		// Clone scores ArrayList
		// TODO: Double check initial ArrayList creation for slowdown issues (e.g. large cimmyt data)
		ArrayList<ArrayList<Float>> newScores = new ArrayList<ArrayList<Float>>();
		for (int i = 0; i < rIntOrder.size(); i++)
		{
			newScores.add(new ArrayList<Float>());
			for (int j = 0; j <= i; j++)
				newScores.get(i).add(1f);
		}

		// Iterate over the old matrix and copy each value into the new matrix
		// at its new (ordered) position
		for (int i=0; i < lineScores.size(); i++)
		{
			// x = the index of the line at i in the old matrix
			int x = rIntOrder.get(i);

			for (int j=0; j <= i; j++)
			{
				// y = the index of the line at i in the old matrix
				int y = rIntOrder.get(j);

				// Set the score at i,j in our new line scores data structure
				// to the score at x,y (or y,x if x > y) in the old scores data
				// structure as this is the score for the lines at i,j in our
				// re-ordered similarity matrix
				float score =  x > y ? lineScores.get(x).get(y) : lineScores.get(y).get(x);
				newScores.get(i).set(j, score);
			}
		}

		return new SimMatrix(newLineOrder, newScores);
	}
}