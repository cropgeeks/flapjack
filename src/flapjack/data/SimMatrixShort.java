// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class SimMatrixShort extends SimMatrix
{
	// The 2D matrix of scores
	private ArrayList<short[]> lineScores;

	public SimMatrixShort()
	{
	}

	private SimMatrixShort(ArrayList<LineInfo> lineInfos, ArrayList<short[]> lineScores)
	{
		this.lineInfos = lineInfos;
		this.lineScores = lineScores;
	}


	// Methods required for XML serialization

	public void setLineScores(ArrayList<short[]> lineScores)
		{ this.lineScores = lineScores; }

	public ArrayList<short[]> getLineScores()
		{ return lineScores; }


	// Other methods

	public void initialize(int size)
	{
		lineScores = new ArrayList<short[]>(size);

		for (int i = 0; i < size; i++)
		{
			lineScores.add(new short[i+1]);				// new short[size] to generate FULL matrix
			Arrays.fill(lineScores.get(i), (short)1);
		}
	}

	// NOTE: 0 should be stored as -32768 and 1 as +32767 (65526 inclusive)

	public float valueAt(int i, int j)
	{
		short sValue = lineScores.get(i)[j];//.get(j);

		return (sValue+32768) / 65535f;
	}

	public void setValueAt(int i, int j, float value)
	{
		short sValue = (short) ((value * 65535) - 32768);

		lineScores.get(i)[j] = sValue;//.set(j, sValue);
	}

	// IMPORTANT - any changes here also need to be made in SimMatrixFloat
	public SimMatrix cloneAndReorder(ArrayList<Integer> rIntOrder, ArrayList<LineInfo> newLineOrder)
	{
		// Clone scores ArrayList
		ArrayList<short[]> newScores = new ArrayList<short[]>(rIntOrder.size());
		for (int i = 0; i < rIntOrder.size(); i++)
		{
			newScores.add(new short[i+1]);			// new float[size] to generate FULL matrix
			Arrays.fill(newScores.get(i), (short)1);
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
				short score =  x > y ? lineScores.get(x)[y] : lineScores.get(y)[x];
				newScores.get(i)[j] = score;
			}
		}

		return new SimMatrixShort(newLineOrder, newScores);
	}
}