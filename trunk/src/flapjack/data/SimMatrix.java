// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class SimMatrix extends XMLRoot
{
	// Tracks the indices of the lines originally used to generate this matrix
	protected ArrayList<LineInfo> lineInfos = new ArrayList<>();

	// The 2D matrix of scores
	private ArrayList<float[]> lineScores;

	public SimMatrix()
	{
	}

	private SimMatrix(ArrayList<LineInfo> lineInfos, ArrayList<float[]> lineScores)
	{
		this.lineInfos = lineInfos;
		this.lineScores = lineScores;
	}


	// Methods required for XML serialization

	public void setLineInfos(ArrayList<LineInfo> lineInfos)
		{ this.lineInfos = lineInfos; }

	public ArrayList<LineInfo> getLineInfos()
		{ return lineInfos; }

	public void setLineScores(ArrayList<float[]> lineScores)
		{ this.lineScores = lineScores; }

	public ArrayList<float[]> getLineScores()
		{ return lineScores; }


	// Other methods

	public void initialize(int size)
	{
		lineScores = new ArrayList<float[]>(size);

		for (int i = 0; i < size; i++)
		{
			lineScores.add(new float[i+1]);			// new float[size] to generate FULL matrix
			Arrays.fill(lineScores.get(i), 1f);
		}
	}

	public float valueAt(int i, int j)
	{
		return lineScores.get(i)[j];
	}

	public void setValueAt(int i, int j, float value)
	{
		lineScores.get(i)[j] = value;
	}

	public int size()
	{
		return lineInfos.size();
	}

	/**
	 * Returns a String representation of the "header" line that would be
	 * associated with this matrix, that is, the column headers of line names.
	 */
	public String createFileHeaderLine(boolean hasRowHeader)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < lineInfos.size(); i++)
			sb.append("\t" + lineInfos.get(i).name());

		if (hasRowHeader)
			return "\t" + sb.toString().trim();
		else
			return sb.toString().trim();
	}

	/**
	 * Returns a String representation of the requested row of this matrix,
	 * suitable for writing that line of data to a text file.
	 */
	public String createFileLine(int i, boolean hasRowHeader)
	{
		StringBuilder sb = new StringBuilder();
		if (hasRowHeader)
			sb.append("\t" + lineInfos.get(i));

		for (int j = 0; j < lineInfos.size(); j++)
		{
			if (j <= i)
				sb.append("\t" + valueAt(i, j));
			else
				sb.append("\t" + valueAt(j, i));
		}

		// Trim off any leading/trailing tabs before returning
		return sb.toString().trim();
	}

	public SimMatrix cloneAndReorder(ArrayList<Integer> rIntOrder, ArrayList<LineInfo> newLineOrder)
	{
		// Clone scores ArrayList
		ArrayList<float[]> newScores = new ArrayList<>(rIntOrder.size());
		for (int i = 0; i < rIntOrder.size(); i++)
		{
			newScores.add(new float[i+1]);			// new float[size] to generate FULL matrix
			Arrays.fill(newScores.get(i), 1f);
		}

		// Iterate over the old matrix and copy each value into the new matrix
		// at its new (ordered) position
		for (int i=0; i < lineScores.size(); i++)
		{
			// x = the index of the line at i in the old matrix
			int x = rIntOrder.get(i);

			for (int j=0; j <= i; j++)
			{
				// y = the index of the line at j in the old matrix
				int y = rIntOrder.get(j);

				// Set the score at i,j in our new line scores data structure
				// to the score at x,y (or y,x if x > y) in the old scores data
				// structure as this is the score for the lines at i,j in our
				// re-ordered similarity matrix
				float score =  x > y ? lineScores.get(x)[y] : lineScores.get(y)[x];
				newScores.get(i)[j] = score;
			}
		}

		return new SimMatrix(newLineOrder, newScores);
	}
}