// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.stream.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class LineDataTableBatchExporter extends SimpleJob
{
	private ArrayList<LineDataTable> tables;
	private ArrayList<DataSet> dataSets;

	private File file;
	// 0=export selected, 1=export all
	private int exportType;
	private boolean exportTraits;

	private DecimalFormat df;

	public LineDataTableBatchExporter(ArrayList<LineDataTable> tables, File file, int exportType, boolean exportTraits)
	{
		this.tables = tables;
		this.file = file;
		this.exportType = exportType;
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

		for (int t = 0; t < tables.size(); t++)
		{
			printTable(out, t, tables.get(t), dataSets.get(t));
//			out.println();
		}

		out.close();
	}

	private void printTable(PrintWriter out, int dataSetIndex, LineDataTable myTable, DataSet myDataSet)
	{
		// Dataset name
//		out.println("# Dataset\t" + myDataSet.getName());

		// Print table header
		if (dataSetIndex == 0)
		{
			StringBuilder headerBuilder = new StringBuilder();

			// Initial column will now hold dataset name
			headerBuilder.append("DataSet");

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
		}

		// Print table data
		for (int row = 0; row < myTable.getRowCount(); row++)
		{
			int col0 = myTable.convertColumnIndexToView(0);
			LineInfo line = (LineInfo)myTable.getObjectAt(row, col0);
			if (exportType == 0 && !line.getSelected())
				continue;

			StringBuilder builder = new StringBuilder();

			builder.append(myDataSet.getName());

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