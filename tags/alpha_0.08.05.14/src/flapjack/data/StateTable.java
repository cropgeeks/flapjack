package flapjack.data;

import java.util.*;

public class StateTable extends XMLRoot
{
	// TODO: What to do about storing (potentially?) redundant data, eg having a
	// state for A/T and another for T/A

	private Vector<AlleleState> states = new Vector<AlleleState>();

	public StateTable()
	{
	}

	public StateTable(int notused)
	{
		states.add(new AlleleState("", "/"));
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

	public int getStateCode(String rawData, boolean create, String missingString, String heteroString)
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
		states.add(new AlleleState(rawData, heteroString));
		return states.size() - 1;
	}

	public int getHomozygousStateCount()
	{
		int count = 0;

		// Don't count the first (unknown) state
		for (int i = 1; i < states.size(); i++)
			if (states.get(i).isHomozygous())
			{
				System.out.println(states.get(i));
				count++;
			}

		return count;
	}

	/**
	 * Returns an array with each element being the total number of alleles for
	 * that state (where each index is equivalent to a state in the state table.
	 */
	public int[] getStatistics(GTView view)
	{
		// +1 because we use the last location to store the total count of
		// alleles within this view (chromosome)
		int[] statistics = new int[states.size()+1];

		view.cacheLines();

		for (int line = 0; line < view.getLineCount(); line++)
			for (int marker = 0; marker < view.getMarkerCount(); marker++)
			{
				int state = view.getState(line, marker);
				statistics[state]++;

				// Track the total
				statistics[statistics.length-1]++;
			}

		return statistics;
	}

	public void deleteState(int index)
	{
		states.remove(index);
	}
}