// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public abstract class SimMatrix extends XMLRoot
{
	// Tracks the indices of the lines originally used to generate this matrix
	protected ArrayList<LineInfo> lineInfos = new ArrayList<LineInfo>();

	public SimMatrix()
	{
	}


	// Methods required for XML serialization

	public void setLineInfos(ArrayList<LineInfo> lineInfos)
		{ this.lineInfos = lineInfos; }

	public ArrayList<LineInfo> getLineInfos()
		{ return lineInfos; }


	// Other methods

	public abstract void initialize(int size);

	public int size()
	{
		return lineInfos.size();
	}

	public abstract float valueAt(int i, int j);

	public abstract void setValueAt(int i, int j, float value);

	/**
	 * Returns a String representation of the "header" line that would be
	 * associated with this matrix, that is, the column headers of line names.
	 */
	public String createFileHeaderLine()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < lineInfos.size(); i++)
			sb.append("\t" + lineInfos.get(i).name());

		return sb.toString().trim();
	}

	/**
	 * Returns a String representation of the requested row of this matrix,
	 * suitable for writing that line of data to a text file.
	 */
	public String createFileLine(int i)
	{
		StringBuilder sb = new StringBuilder();

		for (int j = 0; j < lineInfos.size(); j++)
		{
			float f = 0;

			if (j <= i)
				sb.append("\t" + valueAt(i, j));
			else
				sb.append("\t" + valueAt(j, i));
		}

		// Trim off any leading/trailing tabs before returning
		return sb.toString().trim();
	}

	public abstract SimMatrix cloneAndReorder(ArrayList<Integer> rIntOrder, ArrayList<LineInfo> newLineOrder);
}