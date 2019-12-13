// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

/**
 * Created by gs40939 on 16/05/2016.
 */
public class LineDataTableExporter extends SimpleJob
{
	// Objects used for a single export
	private LineDataTable table;
	private DataSet dataSet;

	// Objects used for a batch export
	private ArrayList<LineDataTable> tables;
	private ArrayList<DataSet> dataSets;

	private File file;
	private int exportType;
	private boolean exportHeaders, exportTraits;

	private DecimalFormat df;

	public LineDataTableExporter(LineDataTable table, File file, int exportType, boolean exportHeaders, boolean exportTraits)
	{
		this.table = table;
		this.file = file;
		this.exportType = exportType;
		this.exportHeaders = exportHeaders;
		this.exportTraits = exportTraits;

		dataSet = table.getDataSet();

		df = new DecimalFormat("#.#########");

		// If the exportType is 0 (export everything), then we need to create
		// a new JTable (using the same model) so any filters active on the real
		// table won't apply to the exported data
		if (exportType == 0)
		{
			LineDataTable clone = new LineDataTable();
			clone.setModel(table.getModel());
			clone.setColumnModel(table.getColumnModel());
			clone.getRowSorter().setSortKeys(table.getRowSorter().getSortKeys());
			clone.setLastSelect(table.getLastSelect());

			this.table = clone;
		}
	}

	public LineDataTableExporter(ArrayList<LineDataTable> tables, File file, int exportType, boolean exportTraits)
	{
		this.tables = tables;
		this.file = file;
		this.exportType = exportType;
		this.exportHeaders = false;
		this.exportTraits = exportTraits;

		dataSets = tables.stream()
			.map(LineDataTable::getDataSet)
			.collect(Collectors.toCollection(ArrayList::new));

		df = new DecimalFormat("#.#########");
	}

	@Override
	public void runJob(int i) throws Exception
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		// Print sorting/filtering headers
		if (exportHeaders)
			printInfoHeaders(out);

		// Single run
		if (tables == null)
			printTable(out, table, dataSet);

		// Batch run
		else
		{
			for (int t = 0; t < tables.size(); t++)
			{
				printTable(out, tables.get(t), dataSets.get(t));
			}
		}

		out.close();
	}

	private void printTable(PrintWriter out, LineDataTable myTable, DataSet myDataSet)
	{
		// Print table header
		StringBuilder headerBuilder = new StringBuilder();
		for (int col = 0; col < myTable.getColumnCount(); col++)
		{
			if (myTable.skipExport(col))
				continue;

			if (headerBuilder.length() > 0)
				headerBuilder.append("\t");
			headerBuilder.append(myTable.getColumnName(col));
		}
		if (exportTraits)
		{
			for (Trait t: myDataSet.getTraits())
			{
				headerBuilder.append("\t");
				headerBuilder.append(t.getName());
			}
		}
		out.println(headerBuilder.toString());

		// Print table data
		for (int row = 0; row < myTable.getRowCount(); row++)
		{
			int col0 = myTable.convertColumnIndexToView(0);
			LineInfo line = (LineInfo)myTable.getObjectAt(row, col0);
			if (exportType == 2 && !line.getSelected())
				continue;

			StringBuilder builder = new StringBuilder();
			for (int col=0; col < myTable.getColumnCount(); col++)
			{
				if (myTable.skipExport(col))
					continue;

				if (builder.length() > 0)
					builder.append("\t");

				Object obj = myTable.getObjectAt(row, col);

				if (obj instanceof Float || obj instanceof Double)
					builder.append(getNumberString(obj));
				else
					builder.append(obj);
			}

			// Traits...
			if (exportTraits)
			{
				for (TraitValue t: line.getLine().getTraitValues())
				{
					builder.append("\t");
					builder.append(t.tableValue() == null ? "" : t.tableValue());
				}
			}

			out.println(builder.toString());
		}
	}

	private void printInfoHeaders(PrintWriter out)
	{
		// Output the FILTER settings
		if (table.getTableFilter() != null)
		{
			for (FilterColumn col: table.getTableFilter())
				if (col.disabled() == false)
					out.println("# FILTER\t"
						+ col.name + "\t"
						+ col.toShortString()
						+ (col.getValue() != null ? ("\t" + col.getValue()) : ""));
		}

		// Output the SORT settings
		List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
		if (sortKeys.size() > 0)
		{
			for (int i = 0; i < sortKeys.size(); i++)
			{
				int col = sortKeys.get(i).getColumn();
				SortOrder ord = sortKeys.get(i).getSortOrder();

				out.println("# SORT " + i + "\t"
					+ table.getModel().getColumnName(col) + "\t"
					+ ord);
			}
		}

		// Output the SELECTION settings
		if (table.getLastSelect() != null)
		{
			for (FilterColumn col : table.getLastSelect())
				if (col.disabled() == false)
					out.println("# SELECT\t"
						+ col.name + "\t"
						+ col.toShortString()
						+ (col.getValue() != null ? ("\t" + col.getValue()) : ""));
		}
	}

	private String getNumberString(Object value)
	{
		if (value instanceof Double)
			if (Double.isNaN((Double) value) || Double.isInfinite((Double) value))
				return value.toString();

		if (value instanceof Float)
			if (Float.isNaN((Float) value) || Float.isInfinite((Float) value))
				return value.toString();

		return df.format(value);
	}
}