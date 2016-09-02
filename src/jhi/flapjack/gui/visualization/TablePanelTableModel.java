package jhi.flapjack.gui.visualization;

import java.text.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.table.*;

public class TablePanelTableModel extends LineDataTableModel
{
	private final GTViewSet viewSet;

	private DecimalFormat df = new DecimalFormat("0.00");

	// Variables for tracking where columns are in the table
	private int padding = 0;
	private int lineScoreIndex = 0;
	private int linkedOffset = 0;

	private int[] linkedModelCols;

	public TablePanelTableModel(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		// In the basic case (just showing line names) we only have one column
		int noCols = 1;

		// Setup the column names for the table
		columnNames = new String[noCols];
		columnNames[0] = "Line";

		if (viewSet != null)
		{
			lines = viewSet.getLines();
			// Grab the linked model and the column indices we need from that model
			linkedModelCols = viewSet.getLinkedModelCols();

			// If we have line scores, or linked model columns we need to
			// add more columns to our table model
			if (viewSet.getDisplayLineScores() || linkedModelCols.length > 0)
			{
				padding = 1;
				noCols++;

				// Setup the line score index
				if (viewSet.getDisplayLineScores())
				{
					lineScoreIndex = 2;
					noCols++;
				}

				// Setup the linked table start index (and offset within the
				// linked columns) based on the column indices in this class
				if (linkedModelCols.length > 0)
				{
					linkedOffset = lineScoreIndex == 0 ? 2 : 3;
					noCols += linkedModelCols.length;
				}
			}

			columnNames = new String[noCols];
			columnNames[0] = "Line";

			if (viewSet.getDisplayLineScores() || linkedModelCols.length > 0)
				columnNames[1] = "";

			if (viewSet.getDisplayLineScores())
				columnNames[lineScoreIndex] = "Sort score";

			if (linkedModelCols.length > 0)
			{
				for (int i = 0; i < linkedModelCols.length; i++)
					columnNames[i + linkedOffset] = viewSet.tableHandler().getModel().getColumnName(linkedModelCols[i]);
			}
		}
	}

	@Override
	public String getColumnName(int column)
	{
		if (column > padding && column > lineScoreIndex)
			return viewSet.tableHandler().getModel().getColumnName(linkedModelCols[column - linkedOffset]);

		else
			return super.getColumnName(column);
	}

	@Override
	public int getRowCount()
	{
		return viewSet == null ? 0 : lines.size();
	}

	@Override
	public Object getObjectAt(int rowIndex, int columnIndex)
	{
		if (columnIndex == 0)
			return lines.get(rowIndex);

		// Return an empty string for our padding column
		else if (columnIndex == padding)
			return "";

		// Return the line score if we're displaying them
		else if (viewSet.getDisplayLineScores() && columnIndex == lineScoreIndex)
			return df.format(lines.get(rowIndex).getScore());

		// Display the columns from our linked table (if we are showing any)
		else if (linkedModelCols.length > 0)
		{
			LineInfo lineInfo = lines.get(rowIndex);

			return viewSet.tableHandler().getModel().getValueForLine(lineInfo, linkedModelCols[columnIndex - linkedOffset]);
		}

		return -1;
	}

	@Override
	public Class<?> getObjectColumnClass(int columnIndex)
	{
		if (columnIndex == 0)
			return LineInfo.class;
		else if (columnIndex == padding)
			return String.class;
		else if (columnIndex == lineScoreIndex)
			return Double.class;

		// Otherwise return the column class of a column from the linked table
		else
		{
			return viewSet.tableHandler().getModel().getObjectColumnClass(linkedModelCols[columnIndex - linkedOffset]);
		}
	}
}
