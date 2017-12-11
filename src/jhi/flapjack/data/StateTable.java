// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

public class StateTable extends XMLRoot
{
	private ArrayList<AlleleState> states = new ArrayList<>();

	// USED AT LOAD TIME ONLY
	private HashMap<String,AlleleState> rawToAlleleHash = new HashMap<>();

	public StateTable()
	{
	}

	public StateTable(int notused)
	{
		AlleleState unknown = new AlleleState("", "/");

		states.add(unknown);
		rawToAlleleHash.put("", unknown);
	}

	void validate()
		throws NullPointerException
	{
		states.forEach(AlleleState::validate);
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

	public int getStateCode(String rawData, boolean create, String missingString, String hetSepStr)
	{
		// If there's no state information, return our default "unknown" code
		if (rawData.equals(missingString))
			return 0;

		// Attempt to collapse strings like AA back down to just A
		if (hetSepStr.isEmpty() && rawData.length() > 0)
			if (rawData.matches(rawData.charAt(0) + "{"+rawData.length()+"}+")) // regex: X{n}+
				rawData = "" + rawData.charAt(0);


		// See if we already have an AlleleState for this rawData string?
		if (rawToAlleleHash.containsKey(rawData))
			return states.indexOf(rawToAlleleHash.get(rawData));

		if (create == false)
			return -1;

		// If it wasn't found and needs to be created, then create/add it
		AlleleState newState = new AlleleState(rawData, hetSepStr);
		states.add(newState);
		rawToAlleleHash.put(rawData, newState);

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

	public boolean isHet(int stateIndex)
	{
		return states.get(stateIndex).isHomozygous() == false;
	}

	public boolean isHom(int stateIndex)
	{
		return states.get(stateIndex).isHomozygous();
	}

	public void resetTable()
	{
		// Keep the default empty state
		AlleleState emptyState = states.get(0);

		// Clear the table
		states.clear();
		rawToAlleleHash.clear();

		// Then readd the empty state
		states.add(emptyState);
		rawToAlleleHash.put("", emptyState);
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
			if (state.isHomozygous() && state.homzAllele().equals("A"))
				A = true;
			if (state.isHomozygous() && state.homzAllele().equals("C"))
				C = true;
			if (state.isHomozygous() && state.homzAllele().equals("G"))
				G = true;
			if (state.isHomozygous() && state.homzAllele().equals("T"))
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
			if (state.isHomozygous() && state.homzAllele().equals("0"))
				a0 = true;
			if (state.isHomozygous() && state.homzAllele().equals("1"))
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
			if (state.isHomozygous() && state.homzAllele().equals("A"))
				A = true;
			if (state.isHomozygous() && state.homzAllele().equals("B"))
				B = true;
			if (state.isHomozygous() && state.homzAllele().equals("H"))
				H = true;
		}

		if (A && B && H)
		{
			// Force the H state to be heterozygous
/*			for (AlleleState state: states)
				if (state.isHomozygous() && state.getState(0).equals("H"))
				{
					state.setRawData("A/B");
					state.setStates(new String[] { "A", "B" });
					state.setHomozygous(false);
				}
*/
			return true;
		}

		return false;
	}

	// A MAGIC population must have states 1-8 (and the unknown state) making 9
	// states in total every state must be a number between 1-8 (stored and
	// checked in the values array as 0-7.
	public boolean containsMagic()
	{
		if (states.size() != 9)
			return false;

		boolean[] values = new boolean[8];

		for (AlleleState state : states)
		{
			if (state.isHomozygous())
			{
				try
				{
					int i = Integer.parseInt(state.homzAllele());
					values[i-1] = true;
				}
				catch (Exception e) { }
			}
		}

		for (boolean b : values)
			if (b == false)
				return false;

		return true;
	}

	// With binned data every state other than the unknown must be an integer
	public boolean containsBins()
	{
		for (AlleleState state : states)
		{
			if (state.isHomozygous() == false)
				return false;

			if (state.isUnknown())
				continue;

			try
			{
				Integer.parseInt(state.homzAllele());
			}
			catch (NumberFormatException e) { return false; }
		}

		return true;
	}

	/**
	 * Returns an exact count of the number of unique states within the table,
	 * including all homozygote and heterozygote alleles. For example, A, G, T,
	 * A/T, and G/A would return 3.
	 */
	public HashMap<String, String> uniqueAlleles()
	{
		HashMap<String, String> hashtable = new HashMap<>();

		for (AlleleState state: states)
			for (String allele : state.getStates())
				hashtable.put(allele, allele);

		return hashtable;
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

					if (s1.matches(s2))
						score = 1.0f;

					// TODO: This is only really correct for diploid data, as
					// A/T/A vs A/T/G should really score 0.6666
					else if (s1.matchesAnyAllele(s2))
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