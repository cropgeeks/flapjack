package flapjack.data;

import java.util.*;

public class StateTable
{
	// TODO: What to do about storing (potentially?) redundant data, eg having a
	// state for A/T and another for T/A

	private Vector<AlleleState> states = new Vector<AlleleState>();

	public StateTable()
	{
	}

	public StateTable(int notused)
	{
		states.add(new AlleleState());
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

	public int getStateCode(String rawData, boolean create)
	{
		// If there's no state information, return our default "unknown" code
		if (rawData.length() == 0)
			return 0;

		// TODO: offer a choice of what the blank/unknown char will be
		// Replace "-" dash characters with the unknown state
		if (rawData.equals("-"))
			return 0;

		for (int i = 0; i < states.size(); i++)
			if (states.get(i).matchesAlleleState(rawData))
				return i;

		if (create == false)
			return -1;

		// If it wasn't found and needs to be created, then add it
		states.add(new AlleleState(rawData));
		return states.size() - 1;
	}
}