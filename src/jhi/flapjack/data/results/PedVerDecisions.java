// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.awt.*;

public abstract class PedVerDecisions
{
	// Constants representing the different decision models for Ped Ver F1
	public static final int SIMPLE_MODEL = 0;
	public static final int INTERMEDIATE_MODEL = 1;
	public static final int DETAILED_MODEL = 2;

	// Constants representing possible decision classes for Ped Ver F1
	public static final int PARENT_1 = 0;
	public static final int PARENT_2 = 1;
	public static final int EXPECTED_F1 = 2;
	public static final int TRUE_F1 = 3;
	public static final int UNDECIDED_HYBRID = 4;
	public static final int UNDECIDED_INBRED = 5;
	public static final int LIKE_P1 = 6;
	public static final int LIKE_P2 = 7;
	public static final int NO_DECISION = 8;

	static Color parent1Color = new Color(50,136,189);
	static Color parent2Color = new Color(102,194,165);
	static Color expectedF1Color = new Color(230,245,152);
	static Color trueF1Color = new Color(171,221,164);
	static Color undecidedHybridColor = new Color(255,255,191);
	static Color undecidedInbredColor = new Color(254,224,139);
	static Color likeP1Color = new Color(253,174,97);
	static Color likeP2Color = new Color(244,109,67);
	static Color noDecisionColor = new Color(188,86,90);

	public abstract String[] getDecisionClasses();

	public int getDecision(PedVerF1sResult result)
	{
		int decision = NO_DECISION;

		// If this line is a parent, or an F1, set its decision to the appropriate value
		if (result.isP1() || result.isP2() || result.isF1())
		{
			if (result.isP1() )
				decision = PARENT_1;

			if (result.isP2())
				decision = PARENT_2;

			if (result.isF1())
				decision = EXPECTED_F1;
		}

		else if (result.canDetermineLineType())
		{
			// If this line is heterozygous it can be a True F1, or an undecided hybrid mixutre
			if (result.isLineHet())
				decision = result.isAlleleMatchExpected() ? TRUE_F1 : UNDECIDED_HYBRID;

				// Otherwise it is likely a male, or female self
			else
			{
				if (result.isLikeP1())
					decision = LIKE_P1;

				else
					decision = result.isLikeP2() ? LIKE_P2 : UNDECIDED_INBRED;
			}
		}

		return decision;
	}

	// Used to get the decision for display wherever we need a textual version of it
	public abstract String getDecisionString(PedVerF1sResult result);

	// Used to get a Color representing the decision for a given result
	public abstract Color getDecisionColor(PedVerF1sResult result);
}