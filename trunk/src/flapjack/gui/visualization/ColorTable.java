package flapjack.gui.visualization;

import java.util.*;

import flapjack.data.*;

class ColorTable
{
	private Vector<ColoredAlleleState> states = new Vector<ColoredAlleleState>();

	ColorTable(StateTable stateTable, int w, int h)
	{
		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);

			states.add(new ColoredAlleleState(state, w, h));
		}
	}

	ColoredAlleleState get(int index)
	{
		return states.get(index);
	}
}