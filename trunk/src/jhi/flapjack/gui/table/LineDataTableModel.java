// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;

public abstract class LineDataTableModel extends AbstractTableModel
{
	protected DataSet dataSet;
	protected String[] columnNames;


	@Override
	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Color getDisplayColor(int row, int col)
	{
		return null;
	}
}