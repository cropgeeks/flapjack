// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import scri.commons.gui.*;

public class FlapjackVersion
{
	public static void main(String[] args)
	{
		System.out.println("Flapjack " + Install4j.getVersion(new FlapjackVersion().getClass()));
		System.exit(0);
	}
}