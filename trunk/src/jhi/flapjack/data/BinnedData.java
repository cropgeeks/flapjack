// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.text.*;
import java.util.*;
import scri.commons.gui.RB;

public class BinnedData extends XMLRoot
{
	private ArrayList<BinData> bins;

	public BinnedData()
	{
		bins = new ArrayList<BinData>();
	}


	// Methods required for XML serialization

	public ArrayList<BinData> getBins()
		{ return bins; }

	public void setBinData(ArrayList<BinData> bins)
		{ this.bins = bins; }


	// Other methods


	public void addBin(int index, float min, float max)
	{
		bins.add(new BinData(index, min, max));
	}

	public BinData getBin(int index)
	{
		return bins.get(index);
	}

	public String getBinForState(String alleleState)
	{
		if (bins.isEmpty() == false)
		{
			try
			{
				int bin = Integer.parseInt(alleleState);
				return getBin(bin).range();
			}
			catch (NumberFormatException e) {}
		}

		return "";
	}

	public boolean containsBins()
	{
		return !bins.isEmpty();
	}

	public static class BinData extends XMLRoot
	{
		public int index;
		public float min;
		public float max;

		private static final NumberFormat nf = NumberFormat.getInstance();

		public BinData()
		{
		}

		BinData(int index, float min, float max)
		{
			this.index = index;
			this.min = min;
			this.max = max;

			nf.setMaximumFractionDigits(3);
		}

		public String range()
		{
			return RB.format("data.BinnedData.BinData.range", nf.format(min), nf.format(max));
		}

		@Override
		public String toString()
		{
			return index + " " + range();
		}
	}
}