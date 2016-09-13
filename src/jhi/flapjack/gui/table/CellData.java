// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;

/*
CellData is a wrapper around (technically) any object we might want to display
in the LineDataTable class, however it has only been designed with the following
types in mind: LineInfo, Double, Boolean, Integer, and String. Instances are
created dynamically by the table models whenever their getValueAt() methods are
called, returning a CellData wrapper around the actual value along with a
reference to the LineInfo that the row corresponds to. This object may have a
flag set stating that it should always sort to the top of the table - the sort
comparators of this class handle these special cases.
Special cell renderers are also needed, because none of the default JTable ones
can handle a CellData object, nor do its editors perform correctly with 'bool'
columns (which are now CellData columns holding a boolean).
*/
public class CellData
{
	private LineInfo line;
	private Object data;

	public CellData(LineInfo line, Object data)
	{
		this.line = line;
		this.data = data;
	}

	LineInfo getLineInfo()
		{ return line; }

	public Object getData()
		{ return data; }

	@Override
	public String toString()
	{
		return data.toString();
	}

	static class CellDataComparator implements Comparator<CellData>
	{
		private TableRowSorter<LineDataTableModel> sorter;

		CellDataComparator(TableRowSorter<LineDataTableModel> sorter)
		{
			this.sorter = sorter;
		}

		public int compare(CellData v1, CellData v2)
		{
			boolean asc = sorter.getSortKeys().get(0).getSortOrder() == SortOrder.ASCENDING;

			if (v1.line != null && v2.line != null)
			{
				boolean v1SortToTop = v1.line.getResults().isSortToTop();
				boolean v2SortToTop = v2.line.getResults().isSortToTop();

				// If v1 is a special row and v2 isn't
				if (v1SortToTop && !v2SortToTop)
					return asc ? -1 : 1;
				// If v2 is a special row and v1 isn't
				if (v2SortToTop && !v1SortToTop)
					return asc ? 1 : -1;
				// if both v1 and v2 are special rows, we still need to "sort"
				// between them, but simply return 0 so they maintain their
				// order regardless
				if (v1SortToTop && v2SortToTop)
					return 0;
			}

			return compareTo(v1, v2);
		}

		private int compareTo(CellData v1, CellData v2)
		{
			// Only attempt these comparisons if both objects are non null
			if (v1.data instanceof Double && v2.data instanceof Double)
				return ((Double)v1.data).compareTo((Double)v2.data);

			else if (v1.data instanceof Integer && v2.data instanceof Integer)
				return ((Integer)v1.data).compareTo((Integer)v2.data);

			else if (v1.data instanceof Boolean && v2.data instanceof Boolean)
				return ((Boolean)v1.data).compareTo((Boolean)v2.data);

			else if (v1.data instanceof Float && v2.data instanceof Float)
				return ((Float)v1.data).compareTo((Float)v2.data);

			else if (v1.data != null && v2.data != null)
				return v1.data.toString().compareTo(v2.data.toString());

			// Deal with one or both objects being null
			else if (v1.data != null)
				return 1;
			else if (v2.data != null)
				return -1;
			else
				return 0;
		}
	}

	// Deals with the fact that our fake double header for the JTable means
	// that String data can be found in numerical columns.
	static class DefaultRenderer extends DefaultTableCellRenderer
	{
		private static BooleanRenderer bRenderer = new BooleanRenderer();

		protected final NumberFormat nf = NumberFormat.getInstance();

		private boolean colorCells = true;

		static Color bgCol1 = UIManager.getColor("Table.selectionBackground");
		static Color bgCol2 = UIManager.getColor("Table.background");
		static Color bgNoSort = new Color(206,221,235);
		static Color bgNoSortSel = bgCol1.darker();

		void setColorCells(boolean colorCells)
			{ this.colorCells = colorCells;	}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object o,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			// If we have a boolean we have to use the boolean renderer
			if (o instanceof Boolean)
				return bRenderer.getTableCellRendererComponent(table, o,
					isSelected, hasFocus, row, column);
			else if (((CellData)o).getData() instanceof Boolean)
				return bRenderer.getTableCellRendererComponent(table,
					((CellData)o).getData(), isSelected, hasFocus, row, column);

			// Otherwise we get the object out of CellData and pass it to the
			// super class getTableCellRendererComponent
			Object value = ((CellData)o).getData();
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			// Align numerical values to the right
			if (value instanceof Number)
			{
				setText(nf.format((Number)value));
				setHorizontalAlignment(JLabel.RIGHT);
			}
			else
				setHorizontalAlignment(JLabel.LEFT);

			int iRow = table.getRowSorter().convertRowIndexToModel(row);
			LineDataTableModel model = ((LineDataTable)table).getLineDataTableModel();
			Color bg = model.getDisplayColor(iRow, column);

			if (colorCells && bg != null)
				setBackground(isSelected ? bg.darker() : bg);
			else
				setBackground(calcBackground(table, row, isSelected));

			return this;
		}
	}

	private static Color calcBackground(JTable table, int row, boolean isSelected)
	{
		LineDataTableModel model = (LineDataTableModel) table.getModel();
		int modelRow = table.convertRowIndexToModel(row);
		Object obj = model.getObjectAt(modelRow, 0);

		if (obj instanceof LineInfo && ((LineInfo)obj).getResults().isSortToTop())
			return isSelected ? DefaultRenderer.bgNoSortSel : DefaultRenderer.bgNoSort;
		else
			return isSelected ? DefaultRenderer.bgCol1 : DefaultRenderer.bgCol2;
	}

	// Code taken straight from the source of JTable (with selection-bg tweak)
	static class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource
    {
        private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        public BooleanRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
            setBorderPainted(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            }
            else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setSelected((value != null && ((Boolean)value).booleanValue()));

            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            } else {
                setBorder(noFocusBorder);
            }

			setBackground(calcBackground(table, row, isSelected));

            return this;
        }
    }
}