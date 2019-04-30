// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization.colors;

import jhi.flapjack.data.*;

import scri.commons.gui.RB;

public class LineSimilarityAnyColorScheme extends LineSimilarityColorScheme
{
	private StateTable stateTable;

	/** Empty constructor that is ONLY used for color customization purposes. */
	public LineSimilarityAnyColorScheme() {}

	public LineSimilarityAnyColorScheme(GTView view, int w, int h)
	{
		super(view, w, h);

		this.stateTable = view.getViewSet().getDataSet().getStateTable();
	}

	protected ColorState getState(int line, int marker)
	{
		int state = view.getState(line, marker);
		AlleleState alleleState = stateTable.getAlleleState(state);

		int comparisonIndex = view.getViewSet().getComparisonLineIndex();

		// If it's the index line, return the darker version
		if (line == comparisonIndex)
			return compStates.get(state);

		// Try to do the comparison
		if (comparisonIndex != -1)
		{
			int compState = view.getState(comparisonIndex, marker);
			AlleleState compAlleleState = stateTable.getAlleleState(compState);

			if (compState == 0)
				return gsStates.get(state);

			if (alleleState.matchesAnyAllele(compAlleleState))
				return mtchStatesY.get(state);

			return  mtchStatesN.get(state);
		}

		// If it's not the same, or we can't do a comparison...
		return gsStates.get(state);
	}

	public int getModel()
		{ return LINE_SIMILARITY_ANY_MATCH; }

	public String toString()
		{ return RB.getString("gui.Actions.vizColorLineSimAny"); }

	public String getDescription()
	{
		return RB.getString("gui.visualization.colors.LineSimilarityAnyMatchColorScheme");
	}
}