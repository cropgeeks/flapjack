// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.simmatrix;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;

import scri.commons.gui.*;

class SimMatrixTableModel extends AbstractTableModel
{
	private ArrayList<ArrayList<Float>> lineScores;
	private ArrayList<LineInfo> lineInfos;
	private int colCount;

	SimMatrixTableModel(SimMatrix matrix)
	{
		lineScores = matrix.getLineScores();
		lineInfos = matrix.getLineInfos();

		// TODO: unsafe (needs to be actual list of line names)
		colCount = lineScores.get(lineScores.size()-1).size();

		System.out.println("rowCount: " + lineScores.size());
	}

	public String getColumnName(int col)
	{
	    return lineInfos.get(col).name();
	}

	public int getColumnCount()
	{
		return colCount;
	}

	public int getRowCount()
	{
		return lineScores.size();
	}

	public Object getValueAt(int row, int col)
	{
		if (col <= row)
			return lineScores.get(row).get(col);
		else
			return lineScores.get(col).get(row);
	}

	static TableCellRenderer getCellRenderer()
	{
//		return new HeatMapRenderer();

		return new NumberFormatCellRenderer();
	}

	static Random rnd = new Random();

	static class HeatMapRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			float f = (Float) value;

			int r = (int)(f * 255);
			int g = (int)(f * 255);
			int b = (int)(f * 255);

			setBackground(new Color(r, g, b));

			return this;
		}
	}
}