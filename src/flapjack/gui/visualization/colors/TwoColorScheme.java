package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;

public class TwoColorScheme extends ColorScheme
{
	// The index of the line to compare all other lines with
	private int lineIndex;

	private Vector<ColorState> aStates = new Vector<ColorState>();
	private Vector<ColorState> bStates = new Vector<ColorState>();
	// TODO: Once we have proper highlighting, there'll be no need for this style?
	private Vector<ColorState> aStatesDark = new Vector<ColorState>();

	private static final Color COLOR_A = new Color(255, 120, 120);
	private static final Color COLOR_B = new Color(120, 255, 120);
	private static final Color COLOR_A_DRK = new Color(255, 90, 90);

	public TwoColorScheme(GTView view, int w, int h)
	{
		super(view);

		if (view.selectedLine != -1)
			lineIndex = view.selectedLine;

		for (int i = 0; i < stateTable.size(); i++)
		{
			AlleleState state = stateTable.getAlleleState(i);
			ColorState cA = null;
			ColorState cB = null;
			ColorState cADark = null;

			// Use white for the default unknown state
			if (state.isUnknown())
				cA = cB = cADark = new SimpleColorState(state, Color.white, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				cA = new HomozygousColorState(state, COLOR_A, w, h);
				cB = new HomozygousColorState(state, COLOR_B, w, h);
				cADark = new HomozygousColorState(state, COLOR_A_DRK, w, h);
			}

			// Heterozygous states
			else
			{
				cA = new HeterozygeousColorState(state, COLOR_A, COLOR_A, COLOR_A, w, h);
				cB = new HeterozygeousColorState(state, COLOR_B, COLOR_B, COLOR_B, w, h);
				cADark = new HeterozygeousColorState(state, COLOR_A_DRK, COLOR_A_DRK, COLOR_A_DRK, w, h);
			}

			aStates.add(cA);
			bStates.add(cB);
			aStatesDark.add(cADark);
		}
	}

	public BufferedImage getImage(int line, int marker)
	{
		int state = view.getState(line, marker);

		// If it's the index line, return the darker version
		if (line == lineIndex)
			return aStatesDark.get(state).getImage();

		// Otherwise do the comparison
		int compState = view.getState(lineIndex, marker);

		if (state == compState)
			return aStates.get(state).getImage();
		else
			return bStates.get(state).getImage();
	}

	public Color getColor(int line, int marker)
	{
		int state = view.getState(line, marker);

		// If it's the index line, return the darker version
		if (line == lineIndex)
			return COLOR_A_DRK;

		// Otherwise do the comparison
		int compState = view.getState(lineIndex, marker);

		if (state == 0)
			return aStates.get(0).getColor();
		if (state == compState)
			return COLOR_A;
		else
			return COLOR_B;
	}
}