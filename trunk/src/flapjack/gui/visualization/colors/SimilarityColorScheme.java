package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;

abstract class SimilarityColorScheme extends ColorScheme
{
	protected Vector<ColorState> aStates = new Vector<ColorState>();
	protected Vector<ColorState> bStates = new Vector<ColorState>();
	// TODO: Once we have proper highlighting, there'll be no need for this style?
	protected Vector<ColorState> aStatesDark = new Vector<ColorState>();

	protected static final Color COLOR_A = new Color(255, 120, 120);
	protected static final Color COLOR_B = new Color(120, 255, 120);
	protected static final Color COLOR_A_DRK = new Color(255, 90, 90);

	public SimilarityColorScheme(GTView view, int w, int h)
	{
		super(view);

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
}