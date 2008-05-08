package flapjack.gui.visualization.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;

abstract class SimilarityColorScheme extends ColorScheme
{
	protected Vector<ColorState> aStates = new Vector<ColorState>();
	protected Vector<ColorState> bStates = new Vector<ColorState>();
	// TODO: Once we have proper highlighting, there'll be no need for this style?
	protected Vector<ColorState> aStatesDark = new Vector<ColorState>();

	/** Empty constructor that is ONLY used for color customization purposes. */
	public SimilarityColorScheme() {}

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
				cA = cB = cADark = new SimpleColorState(state, Prefs.visColorBackground, w, h);

			// Homozygous states
			else if (state.isHomozygous())
			{
				cA = new HomozygousColorState(state, Prefs.visColorSimilarityState1, w, h);
				cB = new HomozygousColorState(state, Prefs.visColorSimilarityState2, w, h);
				cADark = new HomozygousColorState(state, Prefs.visColorSimilarityState1Dark, w, h);
			}

			// Heterozygous states
			else
			{
				cA = new HeterozygeousColorState(state, Prefs.visColorSimilarityState1, Prefs.visColorSimilarityState1, Prefs.visColorSimilarityState1, w, h);
				cB = new HeterozygeousColorState(state, Prefs.visColorSimilarityState2, Prefs.visColorSimilarityState2, Prefs.visColorSimilarityState2, w, h);
				cADark = new HeterozygeousColorState(state, Prefs.visColorSimilarityState1Dark, Prefs.visColorSimilarityState1Dark, Prefs.visColorSimilarityState1Dark, w, h);
			}

			aStates.add(cA);
			bStates.add(cB);
			aStatesDark.add(cADark);
		}
	}

	public void setColorSummaries(Vector<ColorSummary> colors)
	{
		Prefs.visColorSimilarityState1Dark = colors.get(0).color;
		Prefs.visColorSimilarityState1 = colors.get(1).color;
		Prefs.visColorSimilarityState2 = colors.get(2).color;
	}
}