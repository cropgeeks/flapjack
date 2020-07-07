// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

public class QTLInfo extends XMLRoot
{
	private QTL qtl;
	private int index;
	private double mapOffset;

	public QTLInfo()
	{
	}

	public QTLInfo(QTL qtl, int index)
	{
		this.qtl = qtl;
		this.index = index;
	}

	public QTLInfo(QTL qtl, int index, double mapOffset)
	{
		this.mapOffset = mapOffset;

		this.qtl = qtl;
		this.index = index;
	}

	// Copy constructor
	QTLInfo(QTLInfo qtlInfo)
	{
		this.qtl = qtlInfo.qtl;
		this.index = qtlInfo.index;
		this.mapOffset = qtlInfo.mapOffset;
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

	public double getMapOffset()
		{ return mapOffset; }

	public void setMapOffset(double mapOffset)
		{ this.mapOffset = mapOffset; }


	// Other methods

	public double displayPosition()
		{ return mapOffset + qtl.getPosition(); }

	public double min()
		{ return mapOffset + qtl.getMin(); }

	public double max()
		{ return mapOffset + qtl.getMax(); }
}