// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

public class QTLInfo extends XMLRoot
{
	private QTL qtl;
	private int index;
	private float mapOffset;

	public QTLInfo()
	{
	}

	public QTLInfo(QTL qtl, int index)
	{
		this.qtl = qtl;
		this.index = index;
	}

	public QTLInfo(QTL qtl, int index, float mapOffset)
	{
		this.mapOffset = mapOffset;

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

	public float getMapOffset()
		{ return mapOffset; }

	public void setMapOffset(float mapOffset)
		{ this.mapOffset = mapOffset; }


	// Other methods

	public float displayPosition()
		{ return mapOffset + qtl.getPosition(); }

	public float min()
		{ return mapOffset + qtl.getMin(); }

	public float max()
		{ return mapOffset + qtl.getMax(); }
}