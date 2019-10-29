package jhi.flapjack.data.results;

import java.awt.*;

public class PedVerDecisionsDetailed extends PedVerDecisions
{
	public PedVerDecisionsDetailed()
	{
	}

	private String[] classes = new String[] { "TrueF1", "Undecided hybrid mixture", "Undecided inbred mixture", "Like P1", "Like P2", "No decision" };

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
			case TRUE_F1: 			return "True F1";
			case UNDECIDED_HYBRID: 	return "Undecided hybrid mixture";
			case UNDECIDED_INBRED: 	return "Undecided inbred mixture";
			case LIKE_P1: 			return "Like P1";
			case LIKE_P2: 			return "Like P2";

			default: 				return "No decision";
		}
	}

	@Override
	public Color getDecisionColor(PedVerF1sResult result)
	{
		switch(getDecision(result))
		{
			case PARENT_1: 			return parent1Color;
			case PARENT_2: 			return parent2Color;
			case EXPECTED_F1: 		return expectedF1Color;
			case TRUE_F1: 			return trueF1Color;
			case UNDECIDED_HYBRID: 	return undecidedHybridColor;
			case LIKE_P1: 			return likeP1Color;
			case LIKE_P2: 			return likeP2Color;
			case UNDECIDED_INBRED: 	return undecidedInbredColor;
			case NO_DECISION: 		return noDecisionColor;

			default: 				return null;
		}
	}
}
