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
import javax.swing.table.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class LineDataTable extends JTable
{
	private GTViewSet viewSet;
	private LinkedList<ITableViewListener> viewListeners;

	private LineDataTableModel model;
	private TableRowSorter<LineDataTableModel> sorter;
	private LineDataTableMenu menu;
	private CellData.DefaultRenderer renderer;

	// A list of objects used the last time a sortDialog, filterDialog, or selectDialog was run
	private SortColumn[] lastSort;
	private FilterColumn[] lastFilter, lastSelect;

	// Is the table currently filtered?
	private boolean isFiltered = false;
	// Is the table currently coloured?
	private boolean colorCells = true;

	public LineDataTable()
	{
		viewListeners = new LinkedList<ITableViewListener>();

		renderer = new CellData.DefaultRenderer();
		setDefaultRenderer(CellData.class, renderer);

		menu = new LineDataTableMenu(this);
	}

	boolean isFiltered()
		{ return isFiltered; }

	FilterColumn[] getlastFilter()
		{ return lastFilter; }

	public LineDataTableMenu getMenu()
		{ return menu; }

	LineDataTableModel getLineDataTableModel()
		{ return model; }

	public void addViewListener(ITableViewListener listener)
		{ viewListeners.add(listener); }

	public void setColorCells(boolean colorCells)
		{ renderer.setColorCells(this.colorCells = colorCells); }

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
				column.setPreferredWidth(120);
			}
		}

		// Safety net for Matisse code calling setModel with a DefaultTableModel
		if (tm instanceof LineDataTableModel)
		{
			model = (LineDataTableModel) tm;

			// Let the user sort by column
			sorter = new TableRowSorter<>(model);
			setRowSorter(sorter);

			// Provide our special comparator to handle sortToTop functionality
			CellData.CellDataComparator c = new CellData.CellDataComparator(sorter);
			for (int i = 0; i < model.getColumnCount(); i++)
				sorter.setComparator(i, c);

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

	public void exportData()
	{
		ExportDialog dialog = new ExportDialog();

		if (dialog.isOK() == false)
			return;

		// Gather the options selected by the user
		File filename = dialog.getFilename();
		int exportType = Prefs.guiLDTableExportType;
		boolean exportHeaders = Prefs.guiLDTableExportHeaders;

		LineDataTableExporter exporter = new LineDataTableExporter(
			this, filename, exportType, exportHeaders);

		ProgressDialog pDialog = new ProgressDialog(exporter,
			RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			RB.format("gui.dialog.ExportDataDialog.exportLabel"), Flapjack.winMain);

		if (pDialog.failed("gui.error"))
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
				CellData cell = (CellData) entry.getValue(0);
				LineInfo line = cell.getLineInfo();

				if (line != null && line.getVisibility() == LineInfo.HIDDEN)
					return false;

				return true;
			}
		});

		return filters;
	}

	void reapplyFilter()
	{
		if (isFiltered)
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
		isFiltered = true;

		filter(data);
	}

	private void filter(FilterColumn[] data)
	{
		// Create the default filter that will remove manually hidden lines
		ArrayList<RowFilter<LineDataTableModel,Object>> filters = createBaseFilters();

		// Scan and build the needed filters
		if (data != null)
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
		isFiltered = false;

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

		model.selectLines(data, true);
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

	public String getLineStatusText()
	{
		return "Line count: " + model.getRowCount() + ", visible: "
			+ getRowCount() + ", selected: " + model.visAndSelCount();
	}

	public Object getObjectAt(int row, int col)
	{
		return ((CellData) getValueAt(row, col)).getData();
	}

	public TableCellEditor getCellEditor(int row, int col)
	{
		int modelRow = convertRowIndexToModel(row);
		int modelCol = convertColumnIndexToModel(col);

		// Because we use CellData for each value, JTable no longer provides
		// automatic editors because the class for each cell doesn't match
		// anything it understands. So for CellData.data==Boolean we use:
		if (model.getObjectColumnClass(modelCol) == Boolean.class)
		{
			JCheckBox checkBox = new JCheckBox();
			checkBox.setHorizontalAlignment(JCheckBox.CENTER);

			return new DefaultCellEditor(checkBox)
			{
				@Override
				public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
				{
					// The 'value' used to set the check box's initial state is
					// wrong, because the default editor couldn't map a CellData
					// to a bool (so it just defaults to true). We need to look
					// up the actual value using the model and use that instead
					boolean b = (boolean) model.getObjectAt(modelRow, modelCol);
					return super.getTableCellEditorComponent(table, b, isSelected, row, column);
				}
			};
		}

		else if (model.getObjectColumnClass(modelCol) == String.class)
			return new DefaultCellEditor(new JTextField());

		else
			return super.getCellEditor(row, col);
	}
}