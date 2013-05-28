// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class SimMatrixFloat extends SimMatrix
{
	// The 2D matrix of scores
	private ArrayList<float[]> lineScores;

	public SimMatrixFloat()
	{
	}

	private SimMatrixFloat(ArrayList<LineInfo> lineInfos, ArrayList<float[]> lineScores)
	{
		this.lineInfos = lineInfos;
		this.lineScores = lineScores;
	}


	// Methods required for XML serialization

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

	// IMPORTANT - any changes here also need to be made in SimMatrixShort
	public SimMatrix cloneAndReorder(ArrayList<Integer> rIntOrder, ArrayList<LineInfo> newLineOrder)
	{
		// Clone scores ArrayList
		ArrayList<float[]> newScores = new ArrayList<float[]>(rIntOrder.size());
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

		return new SimMatrixFloat(newLineOrder, newScores);
	}
}