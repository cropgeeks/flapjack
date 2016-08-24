// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class LineDataTable extends JTable
{
	private LinkedList<ITableViewListener> viewListeners;

	private LineDataTableModel model;
	private TableRowSorter<LineDataTableModel> sorter;
	private LineDataTableMenu menu;

	private boolean colorCells = true;

	private GTViewSet viewSet;

	// A list of objects used the last time a sortDialog, filterDialog, or selectDialog was run
	private SortColumn[] lastSort;
	private FilterColumn[] lastFilter, lastSelect;

	public LineDataTable()
	{
		viewListeners = new LinkedList<ITableViewListener>();

		setDefaultRenderer(String.class, new ColoredCellRenderer());
		setDefaultRenderer(Float.class, new ColoredCellRenderer());
		setDefaultRenderer(Double.class, new ColoredCellRenderer());

		menu = new LineDataTableMenu(this);

		UIScaler.setCellHeight(this);
	}

	public LineDataTableMenu getMenu()
		{ return menu; }

	LineDataTableModel getLineDataTableModel()
		{ return model; }

	public void addViewListener(ITableViewListener listener)
	{
		viewListeners.add(listener);
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

	@Override
	public void setModel(TableModel tm)
	{
		boolean firstInit = (getModel() == null);

		super.setModel(tm);

		// Set a default width per column (only when first creating the table)
		if (firstInit)
		{
			for (int i = 0; i < getColumnCount(); i++)
			{
				TableColumn column = getColumnModel().getColumn(i);
				column.setPreferredWidth(UIScaler.scale(120));
			}
		}

		// Safety net for Matisse code calling setModel with a DefaultTableModel
		if (tm instanceof LineDataTableModel)
		{
			model = (LineDataTableModel) tm;

			// Let the user sort by column
			sorter = new TableRowSorter<>(model);
			setRowSorter(sorter);

			// Catch sort events
			sorter.addRowSorterListener((RowSorterEvent e) ->
			{
				// We only want to deal with events of type sorted...not sort order changed
				if (e.getType() == RowSorterEvent.Type.SORTED)
				{
					for (ITableViewListener listener: viewListeners)
						listener.tableSorted();
				}
			});

			// Set the initial filter to not show any manually hidden lines
			sorter.setRowFilter(RowFilter.andFilter(createBaseFilters()));
		}
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public void autoResize(boolean autoResize)
	{
		if (autoResize)
			setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		else
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	public void exportData(boolean onlySelected)
	{
		String name = "table-data.txt";
		File saveAs = new File(Prefs.guiCurrentDir, name);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.txt"), "txt");

		// Ask the user for a filename to save the current view as
		String filename = FlapjackUtils.getSaveFilename("Save table data as", saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		LineDataTableExporter exporter = new LineDataTableExporter(this, new File(filename), onlySelected);
		ProgressDialog dialog = new ProgressDialog(exporter,
			RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			RB.format("gui.dialog.ExportDataDialog.exportLabel"), Flapjack.winMain);

		if (dialog.failed("gui.error"))
			return;

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}

	public void copyTableToClipboard()
	{
		StringBuilder text = new StringBuilder();
		String newline = System.getProperty("line.separator");
		DecimalFormat df = new DecimalFormat("#.#########");;

		// Column headers
		for (int c = 0; c < model.getColumnCount(); c++)
		{
			text.append(model.getColumnName(c));
			text.append(c < model.getColumnCount()-1 ? "\t" : newline);
		}

		// Each row
		for (int r = 0; r < getRowCount(); r++)
		{
			int row = convertRowIndexToModel(r);

			for (int c = 0; c < model.getColumnCount(); c++)
			{
				Object obj = model.getValueAt(row, c);
				if (obj instanceof Float || obj instanceof Double)
					text.append(df.format(obj));
				else
					text.append(obj);

				text.append(c < model.getColumnCount()-1 ? "\t" : newline);
			}
		}

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	public void sortDialog()
	{
		SortDialog dialog = new SortDialog(model.getSortableColumns(), lastSort);
		if (dialog.isOK() == false)
			return;

		// Get the list of columns to use for the sortDialog
		SortColumn[] data = dialog.getResults();
		// Remember it for next time in case the user runs another sortDialog
		lastSort = dialog.getResults();

		SortLinesByLineDataModel s = new SortLinesByLineDataModel(viewSet, sorter, data);
		Flapjack.winMain.mAnalysis.runSort(s);

		model.fireTableDataChanged();
	}

	// Creates an initial filter for the table that states: for any LineInfo in
	// the model that was manually hidden in the original view (meaning its
	// filtered flag = true), don't show it in the table.
	private ArrayList<RowFilter<LineDataTableModel,Object>> createBaseFilters()
	{
		ArrayList<RowFilter<LineDataTableModel,Object>> filters = new ArrayList<>();

		filters.add(new RowFilter<LineDataTableModel, Object>() {
			public boolean include(RowFilter.Entry<? extends LineDataTableModel, ? extends Object> entry)
			{
				// TODO: Exception due to TraitsPanel still using Line rather
				// than LineInfo in column 0
				try { return ((LineInfo)entry.getValue(0)).getVisibility() != LineInfo.HIDDEN; }
				catch (ClassCastException e)
				{ return true; }
			}
		});


		return filters;
	}

	void reapplyFilter()
	{
		filter(lastFilter);
	}

	public void filterDialog()
	{
		FilterDialog dialog = FilterDialog.getFilterDialog(model.getFilterableColumns(), lastFilter);
		if (dialog.isOK() == false)
			return;

		// Get the list of columns to use for the filtering
		FilterColumn[] data = dialog.getResults();
		// Remember it for next time in case the user runs another filterDialog
		lastFilter = dialog.getResults();

		filter(data);
	}

	private void filter(FilterColumn[] data)
	{
		// Create the default filter that will remove manually hidden lines
		ArrayList<RowFilter<LineDataTableModel,Object>> filters = createBaseFilters();

		// Scan and build the needed filters
		for (FilterColumn entry: data)
			if (!entry.disabled())
				filters.add(entry.createRowFilter());

		RowFilter<LineDataTableModel,Object> f = RowFilter.andFilter(filters);

		sorter.setRowFilter(f);

		// Notify listeners of the filter event
		for (ITableViewListener listener: viewListeners)
			listener.tableFiltered();
	}

	public void resetFilters()
	{
		model.clearAllFilters();
		sorter.setRowFilter(RowFilter.andFilter(createBaseFilters()));

		// Notify listeners of the filter event
		for (ITableViewListener listener: viewListeners)
			listener.tableFiltered();
	}

	public void selectDialog()
	{
		FilterDialog dialog = FilterDialog.getSelectDialog(model.getFilterableColumns(), lastSelect);
		if (dialog.isOK() == false)
			return;

		// Get the list of columns to use for selection
		FilterColumn[] data = dialog.getResults();
		// Remember it for next time in case the user runs it again
		lastSelect = dialog.getResults();

		model.selectLines(data);
	}

	void selectHighlighted(Boolean state)
	{
		ListSelectionModel lsModel = getSelectionModel();

		// Loop over every (selected) row, convert it to a model row, and
		// then set the new rank value on it
		for (int i = 0; i < getRowCount(); i++)
			if (lsModel.isSelectedIndex(i))
			{
				LineInfo line = model.getLines().get(convertRowIndexToModel(i));

				// Set the line's state to either true or false
				if (state != null)
					line.setSelected(state);
				// Or toggle it
				else
					line.setSelected(!line.getSelected());
			}

		model.fireTableRowsUpdated(0, model.getRowCount()-1);
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

			int iRow = table.getRowSorter().convertRowIndexToModel(row);
			Color bg = model.getDisplayColor(iRow, column);

			if (colorCells && bg != null)
				setBackground(isSelected ? bg.darker() : bg);
			else
				setBackground(isSelected ? bgCol1 : bgCol2);

			return this;
		}
	}
}