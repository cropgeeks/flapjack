package jhi.flapjack.gui.visualization.colors;

import jhi.flapjack.data.GTView;

/**
 * Created by gs40939 on 09/05/2016.
 */
public class LineSimilarityExactColorScheme extends LineSimilarityColorScheme
{
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

			if (state == compState)
				return mtchStatesY.get(state);

			return  mtchStatesN.get(state);
		}

		// If it's not the same, or we can't do a comparison...
		return mtchStatesN.get(state);
	}

	public int getModel()
	{ return LINE_SIMILARITY_EXACT_MATCH; }
}
