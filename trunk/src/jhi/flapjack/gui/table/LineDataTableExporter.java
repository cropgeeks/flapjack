package jhi.flapjack.gui.table;

import java.io.*;
import java.text.NumberFormat;

import scri.commons.gui.*;

/**
 * Created by gs40939 on 16/05/2016.
 */
public class LineDataTableExporter extends SimpleJob
{
	private final LineDataTable table;
	private final File file;

	private final NumberFormat nf = NumberFormat.getInstance();

	public LineDataTableExporter(LineDataTable table, File file)
	{
		this.table = table;
		this.file = file;
	}

	@Override
	public void runJob(int i) throws Exception
	{
		PrintWriter writer = new PrintWriter(file);

		// Print table header
		StringBuilder headerBuilder = new StringBuilder();
		for (int col = 0; col < table.getColumnCount(); col++)
		{
			headerBuilder.append(table.getColumnName(col));
			if (col < table.getColumnCount()-1)
				headerBuilder.append("\t");
		}
		writer.println(headerBuilder.toString());

		// Print table data
		for (int row = 0; row < table.getRowCount(); row++)
		{
			StringBuilder builder = new StringBuilder();
			for (int col=0; col < table.getColumnCount(); col++)
			{
				Object obj = table.getValueAt(table.convertRowIndexToModel(row), col);
				if (obj instanceof Float || obj instanceof Double)
					builder.append(nf.format(obj));
				else
					builder.append(table.getValueAt(table.convertRowIndexToModel(row), col));

				if (col < table.getColumnCount()-1)
					builder.append("\t");
			}

			writer.println(builder.toString());
		}

		writer.close();
	}
}
