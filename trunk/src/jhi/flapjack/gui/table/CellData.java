package jhi.flapjack.gui.table;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;

// TODO: Describe!
public class CellData
{
	private LineInfo line;
	private Object data;

	public CellData(LineInfo line, Object data)
	{
		this.line = line;
		this.data = data;
	}

	public Object getData()
		{ return data; }

	@Override
	public String toString()
	{
		return data.toString();
	}

	public static class CellDataComparator implements Comparator<CellData>
	{
		private TableRowSorter<LineDataTableModel> sorter;

		public CellDataComparator(TableRowSorter<LineDataTableModel> sorter)
		{
			this.sorter = sorter;
		}

		public int compare(CellData v1, CellData v2)
		{
			boolean asc = sorter.getSortKeys().get(0).getSortOrder() == SortOrder.ASCENDING;

			if (v1.line != null && v2.line != null)
			{
				boolean v1SortToTop = v1.line.results().isSortToTop();
				boolean v2SortToTop = v2.line.results().isSortToTop();

				int value = 0;
				// If v1 is a special row and v2 isn't
				if (v1SortToTop && !v2SortToTop)
					return asc ? -1 : 1;
				// If v2 is a special row and v1 isn't
				if (v2SortToTop && !v1SortToTop)
					return asc ? 1 : -1;
				// if both v1 and v2 are special rows, we still need to sort between
				// them, but override the asc/dec so they maintain their order
				// regardless of the sort order
				if (v1SortToTop && v2SortToTop)
				{
					value = compareTo(v1, v2);
					return asc ? value : -value;
				}
			}

			return compareTo(v1, v2);
		}

		private int compareTo(CellData v1, CellData v2)
		{
			if (v1.data instanceof Double)
				return ((Double)v1.data).compareTo((Double)v2.data);

			if (v1.data instanceof Integer)
				return ((Integer)v1.data).compareTo((Integer)v2.data);

			if (v1.data instanceof Boolean)
				return ((Boolean)v1.data).compareTo((Boolean)v2.data);

			return v1.data.toString().compareTo(v2.data.toString());
		}
	}
}
