// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.binning;

import java.util.*;

interface IBinner
{
	public int bin(float value);

	public ArrayList<float[]> getBinSummary();
}