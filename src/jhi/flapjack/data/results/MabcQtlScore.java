// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import jhi.flapjack.data.*;

public class MabcQtlScore extends XMLRoot
{
	public QTLInfo qtl;
	public double drag;
	public int status;

	public MabcQtlScore()
	{
	}

	public MabcQtlScore(QTLInfo qtl)
	{
		this.qtl = qtl;
	}

	public QTLInfo getQtl()
	{
		return qtl;
	}

	public void setQtl(QTLInfo qtl)
	{
		this.qtl = qtl;
	}

	public double getDrag()
	{
		return drag;
	}

	public void setDrag(double drag)
	{
		this.drag = drag;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}


}