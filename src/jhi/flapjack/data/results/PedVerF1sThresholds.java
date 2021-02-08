// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

public class PedVerF1sThresholds extends XMLRoot
{
	// If adding fields, update clone() method below
	private int hetThreshold;
	private int f1Threshold;
	private int errorThreshold;
	private int parentHetThreshold;
	private int f1isHetThreshold;

	public PedVerF1sThresholds()
	{
	}

	public PedVerF1sThresholds(int hetThreshold, int f1Threshold, int errorThreshold, int parentHetThreshold, int f1isHetThreshold)
	{
		this.hetThreshold = hetThreshold;
		this.f1Threshold = f1Threshold;
		this.errorThreshold = errorThreshold;
		this.parentHetThreshold = parentHetThreshold;
		this.f1isHetThreshold = f1isHetThreshold;
	}

	// Factory methods to get user default thresholds
	public static PedVerF1sThresholds fromUserDefaults()
	{
		PedVerF1sThresholds thresholds = new PedVerF1sThresholds();

		thresholds.setErrorThreshold(Prefs.pedVerF1ErrorThreshold);
		thresholds.setF1isHetThreshold(Prefs.pedVerF1isHetThreshold);
		thresholds.setF1Threshold(Prefs.pedVerF1F1Threshold);
		thresholds.setHetThreshold(Prefs.pedVerF1HetThreshold);
		thresholds.setParentHetThreshold(Prefs.pedVerF1ParentHetThreshold);

		return thresholds;
	}

	public PedVerF1sThresholds clone()
	{
		PedVerF1sThresholds clone = new PedVerF1sThresholds();
		clone.hetThreshold = this.hetThreshold;
		clone.f1Threshold = this.f1Threshold;
		clone.errorThreshold = this.errorThreshold;
		clone.parentHetThreshold = this.parentHetThreshold;
		clone.f1isHetThreshold = this.f1isHetThreshold;

		return clone;
	}

	// Methods required for XML serialization

	public int getHetThreshold()
		{ return hetThreshold; }

	public void setHetThreshold(int hetThreshold)
		{ this.hetThreshold = hetThreshold; }

	public int getF1Threshold()
		{ return f1Threshold; }

	public void setF1Threshold(int f1Threshold)
		{ this.f1Threshold = f1Threshold; }

	public int getErrorThreshold()
		{ return errorThreshold; }

	public void setErrorThreshold(int errorThreshold)
		{ this.errorThreshold = errorThreshold; }

	public int getParentHetThreshold()
		{ return parentHetThreshold; }

	public void setParentHetThreshold(int parentHetThreshold)
		{ this.parentHetThreshold = parentHetThreshold; }

	public int getF1isHetThreshold()
		{ return f1isHetThreshold; }

	public void setF1isHetThreshold(int f1isHetThreshold)
		{ this.f1isHetThreshold = f1isHetThreshold; }
}