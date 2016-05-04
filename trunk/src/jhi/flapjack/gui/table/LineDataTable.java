// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class LineDataTable extends JTable
{
	private LineDataTableModel model;

	private boolean colorCells = true;

	public LineDataTable()
	{
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getTableHeader().setReorderingAllowed(false);

		setDefaultRenderer(String.class, new ColoredCellRenderer());
		setDefaultRenderer(Float.class, new ColoredCellRenderer());
		setDefaultRenderer(Double.class, new ColoredCellRenderer());

		UIScaler.setCellHeight(this);
	}

	public boolean colorCells()
		{ return colorCells; }

	public void setColorCells(boolean colorCells)
		{ this.colorCells = colorCells; }

	// Ensures all column headers have tooltips
	@Override
	public JTableHeader createDefaultTableHeader()
	{
		return new JTableHeader(columnModel)
		{
			@Override
			public String getToolTipText(MouseEvent e)
			{
				Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				if (index >= 0 && index < columnModel.getColumnCount())
				{
					int realIndex = columnModel.getColumn(index).getModelIndex();
					return getModel().getColumnName(realIndex);
				}
				else
					return null;
			}
		};
	}

	@Override public void setModel(TableModel tm)
	{
		super.setModel(tm);

		// Safety net for Matisse code calling setModel with a DefaultTableModel
		if (tm instanceof LineDataTableModel)
		{
			model = (LineDataTableModel) tm;

			// Let the user sort by column
			setRowSorter(new TableRowSorter<LineDataTableModel>(model));
		}

		// Also set a default width per column
		for (int i = 0; i < getColumnCount(); i++)
		{
			TableColumn column = getColumnModel().getColumn(i);
			column.setPreferredWidth(UIScaler.scale(120));
		}
	}

	// Deals with the fact that our fake double header for the JTable means
	// that String data can be found in numerical columns.
	private class ColoredCellRenderer extends DefaultTableCellRenderer
	{
		protected final NumberFormat nf = NumberFormat.getInstance();

		private Color bgCol1 = UIManager.getColor("Table.selectionBackground");
		private Color bgCol2 = UIManager.getColor("Table.background");

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			// Align numerical values to the right
			if (value instanceof Number)
			{
				setText(nf.format((Number)value));
				setHorizontalAlignment(JLabel.RIGHT);
			}

			if (colorCells)
			{
				int iRow = table.getRowSorter().convertRowIndexToModel(row);
				Color bg = model.getDisplayColor(iRow, column);

				if (bg != null)
					setBackground(isSelected ? bg.darker() : bg);
				else
					setBackground(isSelected ? bgCol1 : bgCol2);
			}

			return this;
		}
	}
}