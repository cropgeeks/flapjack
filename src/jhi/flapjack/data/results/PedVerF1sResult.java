// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

public class PedVerF1sResult extends XMLRoot
{
	private int dataCount;
	private double percentData;
	private int heterozygousCount;
	private double percentHeterozygous;
	private double percentDeviationFromExpected;
	private double similarityToP1;
	private double similarityToP2;
	private double percentAlleleMatchExpected;
	private String decision;
	private boolean p1;
	private boolean p2;
	private boolean f1;
	private double parent1Heterozygosity;
	private double parent2Heterozygosity;
	private double f1Heterozygosity;

	private PedVerF1sThresholds thresholds;

	public PedVerF1sResult()
	{
	}

	// Follows the logic outlined in GOBii GR-212 to make a decision on which type of cross a line is
	public String getDecision()
	{
		// N/A is our default for the decision, will be returned if none of the other criteria are met
		decision = "N/A";

		// If this line is a parent, or an F1, set its decision to the appropriate value
		if (p1 || p2 || f1)
		{
			if (p1)
				decision = "Parent 1";

			if (p2)
				decision = "Parent 2";

			if (f1)
				decision = "Expected F1";
		}

		// Otherwise we have to determine what kind of line this is
		else
		{
			if (canDetermineLineType(thresholds.getParentHetThreshold(), thresholds.getF1isHetThreshold()))
			{
				boolean lineHet = percentHeterozygous >= thresholds.getHetThreshold();

				// If this line is heterozygous it can be a True F1, or an undecided hybrid mixutre
				if (lineHet)
				{
					boolean trueF1 = percentAlleleMatchExpected >= thresholds.getF1Threshold();

					decision = trueF1 ? "True F1" : "Undecided hybrid mixture";
				}
				// Otherwise it is likely a male, or female self
				else
				{
					boolean femaleSelf = similarityToP1 >= 85;

					if (femaleSelf)
						decision = "Female self";

					else
					{
						boolean maleSelf = similarityToP2 >= 85;

						decision = maleSelf ? "Male self" : "Undecided inbred mixture";
					}
				}
			}
		}

		return decision;
	}

	// We can only make decisions about lines if their parents are suitably inbred and the f1 is heterozygous
	private boolean canDetermineLineType(double parentHetThreshold, double f1IsHetThreshold)
	{
		boolean parent1Inbred = parent1Heterozygosity <= parentHetThreshold;
		boolean parent2Inbred = parent2Heterozygosity <= parentHetThreshold;
		boolean f1Het = f1Heterozygosity >= f1IsHetThreshold;

		return parent1Inbred && parent2Inbred && f1Het;
	}

	// Methods required for XML serialization

	public int getDataCount()
	{
		return dataCount;
	}

	public void setDataCount(int dataCount)
	{
		this.dataCount = dataCount;
	}

	public double getPercentData()
	{
		return percentData;
	}

	public void setPercentData(double percentData)
	{
		this.percentData = percentData;
	}

	public int getHeterozygousCount()
	{
		return heterozygousCount;
	}

	public void setHeterozygousCount(int heterozygousCount)
	{
		this.heterozygousCount = heterozygousCount;
	}

	public double getPercentHeterozygous()
	{
		return percentHeterozygous;
	}

	public void setPercentHeterozygous(double percentHeterozygous)
	{
		this.percentHeterozygous = percentHeterozygous;
	}

	public double getPercentDeviationFromExpected()
	{
		return percentDeviationFromExpected;
	}

	public void setPercentDeviationFromExpected(double percentDeviationFromExpected)
		{ this.percentDeviationFromExpected = percentDeviationFromExpected; }

	public double getSimilarityToP1()
		{ return similarityToP1; }

	public void setSimilarityToP1(double similarityToP1)
		{ this.similarityToP1 = similarityToP1; }

	public double getSimilarityToP2()
		{ return similarityToP2; }

	public void setSimilarityToP2(double similarityToP2)
		{ this.similarityToP2 = similarityToP2; }

	public double getPercentAlleleMatchExpected()
	{
		return percentAlleleMatchExpected;
	}

	public void setPercentAlleleMatchExpected(double percentAlleleMatchExpected)
		{ this.percentAlleleMatchExpected = percentAlleleMatchExpected; }

	public boolean isP1()
		{ return p1; }

	public void setP1(boolean p1)
		{ this.p1 = p1; }

	public boolean isP2()
		{ return p2; }

	public void setP2(boolean p2)
		{ this.p2 = p2; }

	public boolean isF1()
		{ return f1; }

	public void setF1(boolean f1)
		{ this.f1 = f1; }

	public PedVerF1sThresholds getThresholds()
		{ return thresholds; }

	public void setThresholds(PedVerF1sThresholds thresholds)
		{ this.thresholds = thresholds; }

	public double getParent1Heterozygosity()
		{ return parent1Heterozygosity; }

	public void setParent1Heterozygosity(double parent1Heterozygosity)
		{ this.parent1Heterozygosity = parent1Heterozygosity; }

	public double getParent2Heterozygosity()
		{ return parent2Heterozygosity; }

	public void setParent2Heterozygosity(double parent2Heterozygosity)
		{ this.parent2Heterozygosity = parent2Heterozygosity; }

	public double getF1Heterozygosity()
		{ return f1Heterozygosity; }

	public void setF1Heterozygosity(double f1Heterozygosity)
		{ this.f1Heterozygosity = f1Heterozygosity; }
}
