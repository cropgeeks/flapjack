// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.awt.*;

public class PedVerDecisionsIntermediate extends PedVerDecisions
{
	public PedVerDecisionsIntermediate()
	{
	}

	private String[] classes = new String[] { "TrueF1", "P1 Self", "Mixture", "No decision" };

	@Override
	public String[] getDecisionClasses()
	{
		return classes;
	}

	@Override
	public String getDecisionString(PedVerF1sResult result)
	{
		switch (getDecision(result))
		{
			case PARENT_1: 			return "Parent 1";
			case PARENT_2: 			return "Parent 2";
			case EXPECTED_F1: 		return "Expected F1";
			case TRUE_F1:			return "True F1";
			case LIKE_P1: 			return "Like P1";
			case UNDECIDED_HYBRID:
			case UNDECIDED_INBRED:
			case LIKE_P2:			return "Mixture";

			default:				return "No decision";
		}
	}

	@Override
	public Color getDecisionColor(PedVerF1sResult result)
	{
		switch (getDecision(result))
		{
			case PARENT_1: 			return parent1Color;
			case PARENT_2: 			return parent2Color;
			case EXPECTED_F1: 		return expectedF1Color;
			case TRUE_F1:			return trueF1Color;
			case LIKE_P1: 			return likeP1Color;
			case UNDECIDED_HYBRID:
			case UNDECIDED_INBRED:
			case LIKE_P2:			return undecidedHybridColor;

			default:				return noDecisionColor;
		}
	}
}