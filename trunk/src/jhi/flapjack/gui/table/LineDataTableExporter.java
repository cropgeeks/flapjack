package jhi.flapjack.gui.table;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

/**
 * Created by gs40939 on 16/05/2016.
 */
public class LineDataTableExporter extends SimpleJob
{
	private LineDataTable table;
	private File file;
	private int exportType;
	private boolean exportHeaders;

	private DecimalFormat df;

	public LineDataTableExporter(LineDataTable table, File file, int exportType, boolean exportHeaders)
	{
		this.table = table;
		this.file = file;
		this.exportType = exportType;
		this.exportHeaders = exportHeaders;

		df = new DecimalFormat("#.#########");

		// If the exportType is 0 (export everything), then we need to create
		// a new JTable (using the same model) so any filters active on the real
		// table won't apply to the exported data
		if (exportType == 0)
		{
			LineDataTable clone = new LineDataTable();
			clone.setModel(table.getModel());
			clone.getRowSorter().setSortKeys(table.getRowSorter().getSortKeys());

			this.table = clone;
		}
	}

	@Override
	public void runJob(int i) throws Exception
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		// Print sorting/filtering headers
		if (exportHeaders)
			printInfoHeaders(out);

		// Print table header
		StringBuilder headerBuilder = new StringBuilder();
		for (int col = 0; col < table.getColumnCount(); col++)
		{
			headerBuilder.append(table.getColumnName(col));
			if (col < table.getColumnCount()-1)
				headerBuilder.append("\t");
		}
		out.println(headerBuilder.toString());

		// Print table data
		for (int row = 0; row < table.getRowCount(); row++)
		{
			int col0 = table.convertColumnIndexToView(0);
			LineInfo line = (LineInfo)table.getObjectAt(row, col0);
			if (exportType == 2 && !line.getSelected())
				continue;

			StringBuilder builder = new StringBuilder();
			for (int col=0; col < table.getColumnCount(); col++)
			{
				Object obj = table.getObjectAt(row, col);

				if (obj instanceof Float || obj instanceof Double)
					builder.append(df.format(obj));
				else
					builder.append(obj);

				if (col < table.getColumnCount()-1)
					builder.append("\t");
			}

			out.println(builder.toString());
		}

		out.close();
	}

	private void printInfoHeaders(PrintWriter out)
		throws Exception
	{
		// Output the FILTER settings
		if (table.isFiltered() && table.getlastFilter() != null)
		{
			for (FilterColumn col: table.getlastFilter())
				if (col.disabled() == false)
					out.println("# FILTER\t"
						+ col.name + "\t"
						+ col.toShortString()
						+ (col.value != null ? ("\t" + col.value) : ""));
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
	}
}