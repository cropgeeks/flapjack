// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

public class MABCThresholds extends XMLRoot
{
	private int percData;
	private int rppTotal;
	private int qtlAlleleCount;

	public MABCThresholds()
	{
	}

	public MABCThresholds(int percData, int rppTotal, int qtlAlleleCount)
	{
		this.percData = percData;
		this.rppTotal = rppTotal;
		this.qtlAlleleCount = qtlAlleleCount;
	}

	// Factory methods to get user default thresholds
	public static MABCThresholds fromUserDefaults()
	{
		MABCThresholds thresholds = new MABCThresholds();

		thresholds.setPercData(Prefs.mabcDataThreshold);
		thresholds.setRppTotal(Prefs.mabcRPPTotalThreshold);
		thresholds.setQtlAlleleCount(Prefs.mabcQTLAlleleCountThreshold);

		return thresholds;
	}

	// Methods required for XML serialization


	public int getPercData()
		{ return percData; }

	public void setPercData(int percData)
		{ this.percData = percData; }

	public int getRppTotal()
		{ return rppTotal; }

	public void setRppTotal(int rppTotal)
		{ this.rppTotal = rppTotal; }

	public int getQtlAlleleCount()
		{ return qtlAlleleCount; }

	public void setQtlAlleleCount(int qtlAlleleCount)
		{ this.qtlAlleleCount = qtlAlleleCount; }
}