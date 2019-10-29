package jhi.flapjack.data.results;

import java.awt.*;

public class PedVerDecisionsSimple extends PedVerDecisions
{
	public PedVerDecisionsSimple()
	{
	}

	private String[] classes = new String[] { "TrueF1", "No decision" };

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
			case PARENT_1: 		return "Parent 1";
			case PARENT_2: 		return "Parent 2";
			case EXPECTED_F1: 	return "Expected F1";
			case TRUE_F1: 		return "True F1";

			default: 			return "No decision";
		}
	}

	@Override
	public Color getDecisionColor(PedVerF1sResult result)
	{
		switch (getDecision(result))
		{
			case PARENT_1: return parent1Color;
			case PARENT_2: return parent2Color;
			case EXPECTED_F1: return expectedF1Color;
			case TRUE_F1: return trueF1Color;

			default: return noDecisionColor;
		}
	}
}
