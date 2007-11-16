package flapjack.data;

import java.util.*;

public class StateTable
{
	private Vector<String> states = new Vector<String>();

	public StateTable()
	{
		states.add("UNKNOWN");
	}

	public short getStateCode(String state, boolean create)
	{
		short index = (short) states.indexOf(state);

		if (create == false || index != -1)
			return index;

		// If it wasn't found and needs to be created, then add it
		states.add(state);
		return (short) (states.size() - 1);
	}

	public void print()
	{
		System.out.println("State lookup table:");
		for (int i = 0; i < states.size(); i++)
			System.out.println(i + ": " + states.get(i));
	}
}