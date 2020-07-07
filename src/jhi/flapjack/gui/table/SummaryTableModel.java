// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import scri.commons.gui.*;

public abstract class SummaryTableModel extends AbstractTableModel
{
	protected String[] columnNames, ttNames;

	@Override
	public String getColumnName(int col)
		{ return columnNames[col]; }

	public String getToolTip(int col)
	{
		// Return the tooltip text (if it was defined) and if not, just use the
		// standard column name instead
		return ttNames != null && ttNames[col] != null ? ttNames[col] : columnNames[col];
	}

	@Override
	public int getColumnCount()
		{ return columnNames.length; }

	static class DoubleNumRenderer extends NumberFormatCellRenderer
	{
		// White
		static Color col1 = new Color(255,255,255);
		// Greenish
		static Color col2 = new Color(188,209,151);
		static int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		static int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

		static Color bgColSel = UIManager.getColor("Table.selectionBackground");
		static Color bgCol = UIManager.getColor("Table.background");

		@Override
		public Component getTableCellRendererComponent(JTable table, Object obj,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, obj, isSelected,
				hasFocus, row, column);

			if (obj instanceof Double)// && column >= 5)
			{
				double value = (double) obj;

				if (value < 0 || value > 100 || Double.isNaN(value))
				{
					setBackground(isSelected ? bgColSel : bgCol);
					return this;
				}

				value = value / 100d;

				double f1 = 1f - value;
				double f2 = value;

				Color color = new Color(
					(int) (f1 * c1[0] + f2 * c2[0]),
					(int) (f1 * c1[1] + f2 * c2[1]),
					(int) (f1 * c1[2] + f2 * c2[2]));

				setBackground(isSelected ? color.darker() : color);
			}
			else
				setBackground(isSelected ? bgColSel : bgCol);

			return this;
		}
	}
}