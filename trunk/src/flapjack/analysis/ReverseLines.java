package flapjack.analysis;

import java.util.*;

import flapjack.data.*;

public class ReverseLines
{
	private GTView view;

	public ReverseLines(GTView view)
	{
		this.view = view;
	}

	public void run()
	{
		Vector<Integer> lines = view.getLines();

		Vector<Integer> reversed = new Vector<Integer>(lines.size());

		for (int i = 0; i < lines.size(); i++)
			reversed.add(lines.size()-1-i);

		view.setLines(reversed);
	}
}