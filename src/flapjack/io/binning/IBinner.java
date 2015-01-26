// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binning;

import java.util.*;

interface IBinner
{
	public int bin(float value);

	public ArrayList<float[]> getBinSummary();
}