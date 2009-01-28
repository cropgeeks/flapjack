package flapjack.data;

import java.util.*;

public class StateTable extends XMLRoot
{
	private Vector<AlleleState> states = new Vector<AlleleState>();

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

	public Vector<AlleleState> getStates()
		{ return states; }

	public void setStates(Vector<AlleleState> states)
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

		for (int i = 0; i < states.size(); i++)
			if (states.get(i).matchesAlleleState(rawData))
				return i;

		if (create == false)
			return -1;

		// If it wasn't found and needs to be created, then add it
		states.add(new AlleleState(rawData, useHetSep, hetSepStr));
		return states.size() - 1;
	}

	public int getHomozygousStateCount()
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
}