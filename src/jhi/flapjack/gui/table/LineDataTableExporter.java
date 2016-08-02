package jhi.flapjack.gui.table;

import java.io.*;
import java.text.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

/**
 * Created by gs40939 on 16/05/2016.
 */
public class LineDataTableExporter extends SimpleJob
{
	private final LineDataTable table;
	private final File file;
	private final boolean onlySelected;

	private final DecimalFormat df;

	public LineDataTableExporter(LineDataTable table, File file, boolean onlySelected)
	{
		this.table = table;
		this.file = file;
		this.onlySelected = onlySelected;

		df = new DecimalFormat("#.#########");
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
			LineInfo line = (LineInfo)table.getValueAt(row, 0);
			if (onlySelected && !line.getSelected())
				continue;

			StringBuilder builder = new StringBuilder();
			for (int col=0; col < table.getColumnCount(); col++)
			{
				Object obj = table.getValueAt(row, col);

				if (obj instanceof Float || obj instanceof Double)
					builder.append(df.format(obj));
				else
					builder.append(obj);

				if (col < table.getColumnCount()-1)
					builder.append("\t");
			}

			writer.println(builder.toString());
		}

		writer.close();
	}
}
