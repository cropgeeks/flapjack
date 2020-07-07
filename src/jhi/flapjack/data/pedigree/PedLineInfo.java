// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.pedigree;

import jhi.flapjack.data.*;

public class PedLineInfo extends XMLRoot
{
	public static final int TYPE_NA = 0;
	public static final int TYPE_RP = 1;
	public static final int TYPE_DP = 2;

	// The child whose parent is being described
	private Line progeny;

	// The parent line
	private Line parent;

	// The "type" relationship of this parent to its progeny
	private int type;

	public PedLineInfo()
	{
	}

	public PedLineInfo(Line progeny, Line parent, int type)
	{
		this.progeny = progeny;
		this.parent = parent;
		this.type = type;
	}

	public Line getProgeny()
		{ return progeny; }

	public void setProgeny(Line progeny)
		{ this.progeny = progeny; }

	public Line getParent()
		{ return parent; }

	public void setParent(Line parent)
		{ this.parent = parent; }

	public int getType()
		{ return type; }

	public void setType(int type)
		{ this.type = type; }
}