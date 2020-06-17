// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.io.*;
import java.text.*;

import scri.commons.gui.*;

public class SummaryTableExporter extends SimpleJob
{
	private SummaryTable table;
	private File file;

	private DecimalFormat df;

	public SummaryTableExporter(SummaryTable table, File file)
	{
		this.table = table;
		this.file = file;

		df = new DecimalFormat("#.#########");
	}

	@Override
	public void runJob(int i) throws Exception
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		// Print table header
		StringBuilder headerBuilder = new StringBuilder();
		for (int col = 0; col < table.getColumnCount(); col++)
		{
			if (headerBuilder.length() > 0)
				headerBuilder.append("\t");
			headerBuilder.append(table.getColumnName(col));
		}
		out.println(headerBuilder.toString());

		// Print table data
		for (int row = 0; row < table.getRowCount(); row++)
		{
			int col0 = table.convertColumnIndexToView(0);

			StringBuilder builder = new StringBuilder();
			for (int col=0; col < table.getColumnCount(); col++)
			{
				if (builder.length() > 0)
					builder.append("\t");

				Object obj = table.getValueAt(row, col);

				if (obj instanceof Float || obj instanceof Double)
					builder.append(getNumberString(obj));
				else
					builder.append(obj);
			}

			out.println(builder.toString());
		}

		out.close();
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