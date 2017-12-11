// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;
import java.util.stream.*;

public class AlleleState extends XMLRoot
{
	private String[] states;

	private boolean isHomozygous = true;

	public AlleleState()
	{
	}

	public AlleleState(String rawData, String hetSepStr)
	{
		rawData = new String(rawData.toUpperCase());

		// If we want to separate on a known string, eg A/B as A & B
		if (!hetSepStr.isEmpty())
			states = rawData.split(hetSepStr);
		// Or if we want to separate on every character, eg AB as A & B
		else
		{
			states = new String[rawData.length()];

			for (int i = 0; i < states.length; i++)
				states[i] = "" + rawData.charAt(i);
		}

		if (states.length > 1)
			isHomozygous = false;
	}

	void validate()
		throws NullPointerException
	{
		if (states == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public String[] getStates()
		{ return states; }

	public void setStates(String[] states)
		{ this.states = states; }

	public boolean isHomozygous()
		{ return isHomozygous; }

	public void setHomozygous(boolean isHomozygous)
		{ this.isHomozygous = isHomozygous; }

	// Other methods

	public String toString()
	{
		return Stream.of(states).collect(Collectors.joining("/"));
	}

	public String homzAllele()
	{
		if (!isHomozygous())
			throw new IllegalStateException("Attempted to get a homozygous state for an AlleleState that is heterozygous.");

		return states[0];
	}

	public String getState(int index)
	{
		return states[index];
	}

	/**
	 * Returns true if this allele state contains the same information as the
	 * other allele state. (eg, A/A/T has 2 As and 1 T and should match A/T/A
	 * or T/A/A).
	 */
	public boolean matches(AlleleState other)
	{
		String[] otherStates = other.states;

		if (states.length != otherStates.length)
			return false;

		// Count the number of times each allele appears and compare that count
		// between the two AlleleState objects
		for (int i = 0; i < states.length; i++)
			if (countState(states[i]) == other.countState(states[i]) == false)
				return false;

		return true;
	}

	// Returns true if this state is homozgeous and is being compared against
	// another homozegous state that has the same value
	public boolean isSameHomzAs(AlleleState other)
	{
		if (!isHomozygous() || !other.isHomozygous)
			return false;

		return (states[0].equals(other.states[0]));
	}

	// Returns a count of the number of times this allele appears in this data
	// (eg, will return 2 for A/A/T on a search of A)
	private int countState(String allele)
	{
		int count = 0;
		for (String s: states)
			if (s.equals(allele))
				count++;

		return count;
	}

	public boolean isUnknown()
	{
		return states[0].isEmpty();
	}

	/**
	 * Returns true if any allele matches any other allele in the other state,
	 * eg A will match with A/T or G/A, etc
	 */
	public boolean matchesAnyAllele(AlleleState other)
	{
		for (String a1: states)
			for (String a2: other.states)
				if (a1.equals(a2))
					return true;

		return false;
	}

	private boolean hasAllele(String allele, AlleleState other)
	{
		return Arrays.stream(other.states).anyMatch(allele::equals);
	}

	/**
	 * Checks that all of the alleles in this AlleleState can be found across
	 * the parent AlleleStates. eg parents are C and T and progeny is C, T, or
	 * C/T are a match, whereas A, G, C/A, T/A are examples of mismatches.
	 */
	public boolean allelesContainedInParents(AlleleState parent1, AlleleState parent2)
	{
		for (String a1: states)
			if (!hasAllele(a1, parent1) && !hasAllele(a1, parent2))
				return false;

		return true;
	}

	// ***DO NOT USE*** for compatability with old versions only
	public String xmlGetRawData()
		{ return toString(); }

	// Returns true if this is a "het"-like state (eg A/A) that should really
	// have been encoded as normal homozygote (eg A)
	public boolean isHomzEncodedAsHet()
	{
		if (isHomozygous)
			return false;

		String str = states[0];
		for (int i = 1; i < states.length; i++)
			if (states[i].equals(str) == false)
				return false;

		return true;
	}
}