package flapjack.gui.visualization.colors;

import java.util.*;

import flapjack.data.*;

public class ColorTable
{
	private Vector<ColoredAlleleState> states = new Vector<ColoredAlleleState>();

	public ColorTable(StateTable stateTable, int w, int h)
	{
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			states.add(new ColoredAlleleState(state, w, h));
		}
	}

	public ColoredAlleleState get(int index)
	{
		return states.get(index);
	}
}