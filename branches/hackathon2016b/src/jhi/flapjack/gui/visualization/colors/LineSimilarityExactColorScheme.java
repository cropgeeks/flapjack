package jhi.flapjack.gui.visualization.colors;

import jhi.flapjack.data.*;

import scri.commons.gui.RB;

public class LineSimilarityExactColorScheme extends LineSimilarityColorScheme
{
	/** Empty constructor that is ONLY used for color customization purposes. */
	public LineSimilarityExactColorScheme() {}

	public LineSimilarityExactColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);
	}

	protected ColorState getState(int line, int marker)
	{
		int state = view.getState(line, marker);
		int comparisonIndex = view.getViewSet().getComparisonLineIndex();

		// If it's the index line, return the darker version
		if (line == comparisonIndex)
			return compStates.get(state);

		// Try to do the comparison
		if (comparisonIndex != -1)
		{
			int compState = view.getState(comparisonIndex, marker);

			if (compState == 0)
				return gsStates.get(state);

			if (state == compState)
				return mtchStatesY.get(state);

			return  mtchStatesN.get(state);
		}

		// If it's not the same, or we can't do a comparison...
		return gsStates.get(state);
	}

	public int getModel()
		{ return LINE_SIMILARITY_EXACT_MATCH; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorLineSimExact"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.LineSimilarityExactMatchColorScheme");
	}
}
