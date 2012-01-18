// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

public class QTLInfo extends XMLRoot
{
	private QTL qtl;
	private int index;

	public QTLInfo()
	{
	}

	public QTLInfo(QTL qtl, int index)
	{
		this.qtl = qtl;
		this.index = index;
	}

	// Methods required for XML serialization

	public QTL getQTL()
		{ return qtl; }

	public void setQTL(QTL qtl)
		{ this.qtl = qtl; }

	public int getIndex()
		{ return index; }

	public void setIndex(int index)
		{ this.index = index; }

	public float min()
		{ return qtl.getMin(); }

	public float max()
		{ return qtl.getMax(); }
}