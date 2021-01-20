// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

public class PedVerLinesThresholds extends XMLRoot
{
	// If adding fields, update clone() method below
	private int data;
	private int parentHet;
	private int simToParents;

	public PedVerLinesThresholds()
	{
	}

	public PedVerLinesThresholds(int data, int parentHet, int simToParents)
	{
		this.data = data;
		this.parentHet = parentHet;
		this.simToParents = simToParents;
	}

	// Factory methods to get user default thresholds
	public static PedVerLinesThresholds fromUserDefaults()
	{
		PedVerLinesThresholds thresholds = new PedVerLinesThresholds();

		thresholds.setData(Prefs.pedVerLinesDataThreshold);
		thresholds.setParentHet(Prefs.pedVerLinesParentHetThreshold);
		thresholds.setSimToParents(Prefs.pedVerLinesSimToParentsThreshold);

		return thresholds;
	}

	public PedVerLinesThresholds clone()
	{
		PedVerLinesThresholds clone = new PedVerLinesThresholds();
		clone.data = this.data;
		clone.parentHet = this.parentHet;
		clone.simToParents = this.simToParents;

		return clone;
	}


	// Methods required for XML serialization

	public int getData()
		{ return data; }

	public void setData(int data)
		{ this.data = data; }

	public int getParentHet()
		{ return parentHet; }

	public void setParentHet(int parentHet)
		{ this.parentHet = parentHet; }

	public int getSimToParents()
		{ return simToParents; }

	public void setSimToParents(int simToParents)
		{ this.simToParents = simToParents; }
}