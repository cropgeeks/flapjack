// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

public class StateTable extends XMLRoot
{
	private ArrayList<AlleleState> states = new ArrayList<>();

	public StateTable()
	{
	}

	public StateTable(int notused)
	{
		states.add(new AlleleState("", true, "/"));
	}

	void validate()
		throws NullPointerException
	{
		for (AlleleState state: states)
			state.validate();
	}


	// Methods required for XML serialization

	public ArrayList<AlleleState> getStates()
		{ return states; }

	public void setStates(ArrayList<AlleleState> states)
		{ this.states = states; }


	// Other methods

	public int size()
	{
		return states.size();
	}

	public AlleleState getAlleleState(int code)
	{
		return states.get(code);
	}

	public int getStateCode(String rawData, boolean create, String missingString, boolean useHetSep, String hetSepStr)
	{
		// If there's no state information, return our default "unknown" code
		if (rawData.equals(missingString))
			return 0;

		// Attempt to collapse strings like AA back down to just A
		if (!useHetSep && rawData.length() > 0)
			if (rawData.matches(rawData.charAt(0) + "{"+rawData.length()+"}+")) // regex: X{n}+
				rawData = "" + rawData.charAt(0);

		for (int i = 0; i < states.size(); i++)
			if (states.get(i).matchesAlleleState(rawData))
				return i;

		if (create == false)
			return -1;

		// If it wasn't found and needs to be created, then add it
		states.add(new AlleleState(rawData, useHetSep, hetSepStr));
		return states.size() - 1;
	}

	public int calculateHomozygousStateCount()
	{
		int count = 0;

		// Don't count the first (unknown) state
		for (int i = 1; i < states.size(); i++)
			if (states.get(i).isHomozygous())
				count++;

		return count;
	}

	public void deleteState(int index)
	{
		states.remove(index);
	}

	public void print()
	{
		System.out.println("State Table:");
		for (AlleleState state: states)
			System.out.println(state);
	}

	public void resetTable()
	{
		// Keep the default empty state
		AlleleState emptyState = states.get(0);

		// Clear the table
		states.clear();

		// Then readd the empty state
		states.add(emptyState);
	}

	/**
	 * Scans the state table and returns true if it looks like this data set
	 * contains nucleotide data.
	 */
	public boolean containsNucleotides()
	{
		boolean A = false, C = false, G = false, T = false;

		for (AlleleState state: states)
		{
			if (state.isHomozygous() && state.getState(0).equals("A"))
				A = true;
			if (state.isHomozygous() && state.getState(0).equals("C"))
				C = true;
			if (state.isHomozygous() && state.getState(0).equals("G"))
				G = true;
			if (state.isHomozygous() && state.getState(0).equals("T"))
				T = true;
		}

		return (A && C && G && T);
	}

	/**
	 * Scans the state table and returns true if it looks like this data set
	 * contains nucleotide data (plus 0 and 1 data).
	 */
	public boolean containsNucleotides01()
	{
		boolean a0 = false, a1 = false;

		for (AlleleState state: states)
		{
			if (state.isHomozygous() && state.getState(0).equals("0"))
				a0 = true;
			if (state.isHomozygous() && state.getState(0).equals("1"))
				a1 = true;
		}

		return containsNucleotides() && a0 && a1;
	}

	// Scans for ABH(CD) data, and if found, also overrides the H state to be
	// non-homozygous
	public boolean containsABHData()
	{
		boolean A = false, B = false, H = false;

		for (AlleleState state: states)
		{
			if (state.isHomozygous() && state.getState(0).equals("A"))
				A = true;
			if (state.isHomozygous() && state.getState(0).equals("B"))
				B = true;
			if (state.isHomozygous() && state.getState(0).equals("H"))
				H = true;
		}

		if (A && B && H)
		{
			// Force the H state to be heterozygous
			for (AlleleState state: states)
				if (state.isHomozygous() && state.getState(0).equals("H"))
					state.setHomozygous(false);

			return true;
		}

		return false;
	}

	/**
	 * Returns an exact count of the number of unique states within the table,
	 * including all homozygote and heterozygote alleles. For example, A, G, T,
	 * A/T, and G/A would return 3.
	 */
	public int calculateUniqueStateCount()
	{
		Hashtable<String, String> hashtable = new Hashtable<>();

		for (AlleleState state: states)
		{
			if (state.isHomozygous())
				hashtable.put(state.getState(0), state.getState(0));
			else
			{
				hashtable.put(state.getState(0), state.getState(0));
				hashtable.put(state.getState(1), state.getState(1));
			}
		}

		return hashtable.size() - 1;
	}

	/**
	 * Returns a similarity matrix that can be used for comparisons between
	 * lines.
	 */
	public float[][] calculateSimilarityMatrix()
	{
		float[][] matrix = new float[states.size()][states.size()];
		float score;

		for (int i = 0; i < states.size(); i++)
		{
			for (int j = 0; j < states.size(); j++)
			{
				// If either state is unknown, score 0
				if (i == 0 || j == 0)
					score = 0;
				// If they're identical, score 1
				else if (i == j)
					score = 1;

				// Otherwise we're in an A vs A/T type of situation and need to
				// look for possible half scores
				else
				{
					AlleleState s1 = states.get(i);
					AlleleState s2 = states.get(j);

					if (s1.matchesAnyAllele(s2))
						score = 0.5f;
					else
						score = 0;
				}

				matrix[i][j] = score;
			}
		}

		return matrix;
	}
}