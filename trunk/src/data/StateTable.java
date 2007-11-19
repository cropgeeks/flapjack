package flapjack.data;

import java.util.*;

public class StateTable
{
	// TODO: What to do about storing (potentially?) redundant data, eg having a
	// state for A/T and another for T/A

	private Vector<AlleleState> states = new Vector<AlleleState>();

	public StateTable()
	{
		states.add(new AlleleState());
	}

	public int size()
	{
		return states.size();
	}

	public AlleleState getAlleleState(short code)
	{
		return states.get(code);
	}

	public short getStateCode(String[] stateArray, boolean create)
	{
		// If there's no state information, return our default "unknown" code
		if (stateArray.length == 0 || stateArray[0].length() == 0)
			return 0;

		AlleleState newState = new AlleleState(stateArray);

		for (short i = 0; i < states.size(); i++)
			if (states.get(i).matchesAlleleState(newState))
				return i;

		if (create == false)
			return -1;

		// If it wasn't found and needs to be created, then add it
		states.add(newState);
		return (short) (states.size() - 1);
	}
}