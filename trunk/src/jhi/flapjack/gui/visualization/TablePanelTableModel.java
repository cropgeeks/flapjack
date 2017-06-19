package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.text.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

public class TablePanelTableModel extends LineDataTableModel
{
	private final GTViewSet viewSet;

	private DecimalFormat df = new DecimalFormat("0.00");

	// Variables for tracking where columns are in the table
	private int padIndex = -1;
	private int lineScoreIndex = -1;
	private int traitsOffset = -1;
	private int linkedOffset = -1;

	private int[] traitsModelCols;
	private int[] linkedModelCols;

	private LineDataTableModel traitsModel;
	private LineDataTableModel linkedModel;

	public TablePanelTableModel(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		// In the basic case (just showing line names) we only have one column
		int noCols = 1;
		columnNames = new String[noCols];
		columnNames[0] = "Line";

		if (viewSet != null)
		{
			lines = viewSet.getLines();
			// Grab the linked model and the column indices we need from that model
			linkedModelCols = viewSet.getLinkedModelCols();
			traitsModelCols = viewSet.getTraits();

			linkedModel = viewSet.tableHandler().model();
			traitsModel = Flapjack.winMain.getNavPanel().getTraitsPanel(viewSet.getDataSet(), false).getTraitsTab(false).getModel();

			// If we have line scores, or linked model columns we need to
			// add more columns to our table model
			if (viewSet.getDisplayLineScores() /*|| traitsModelCols.length > 0*/ || linkedModelCols.length > 0 )       // <------------------------- UNCOMMENT FOR TRAITS
			{
				// Add an extra column for the padding column
				padIndex = 1;
				noCols++;

				// Setup the line score index
				if (viewSet.getDisplayLineScores())
				{
					lineScoreIndex = noCols;
					noCols++;
				}

//				if (traitsModelCols.length > 0)																			// <------------------------- UNCOMMENT FOR TRAITS
//				{
//					traitsOffset = noCols;
//					noCols += traitsModelCols.length;
//				}

				// Setup the linked table start index (and offset within the
				// linked columns) based on the column indices in this class
				if (linkedModelCols.length > 0)
				{
					linkedOffset = noCols;
					noCols += linkedModelCols.length;
				}
			}

			columnNames = new String[noCols];
			columnNames[0] = "Line";

			if (padIndex != -1)
				columnNames[1] = "";

			if (lineScoreIndex != -1)
				columnNames[lineScoreIndex] = "Sort score";

			if (traitsOffset != -1)
				for (int i = 0; i < traitsModelCols.length; i++)
					columnNames[i + traitsOffset] = viewSet.getDataSet().getTraits().get(i).getName();

			if (linkedOffset != -1)
				for (int i = 0; i < linkedModelCols.length; i++)
					columnNames[i + linkedOffset] = viewSet.tableHandler().model().getColumnName(linkedModelCols[i]);
		}
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	@Override
	public int getRowCount()
	{
		return viewSet == null ? 0 : lines.size();
	}

	@Override
	public Object getObjectAt(int row, int col)
	{
		LineInfo line = lines.get(row);

		if (col == 0)
			return line;

		else if (linkedOffset != -1 && col >= linkedOffset)
			return linkedModel.getObjectForLine(
				line, linkedModelCols[col - linkedOffset]);

		else if (traitsOffset != -1 && col >= traitsOffset)
		{
			return line.getLine().getTraitValues().get(
				traitsModelCols[col - traitsOffset]).tableValue();
		}

		else if (col == lineScoreIndex)
			return df.format(lines.get(row).getScore());

		return "";
	}

	@Override
	public Class<?> getObjectColumnClass(int col)
	{
		if (col == 0)
			return LineInfo.class;

		else if (linkedOffset != -1 && col >= linkedOffset)
			return linkedModel.getObjectColumnClass(linkedModelCols[col - linkedOffset]);

		else if (traitsOffset != -1 && col >= traitsOffset)
			return traitsModel.getColumnClass(traitsModelCols[col - traitsOffset]);

		else if (col == lineScoreIndex)
			return Double.class;

		return String.class;
	}

	public Color getDisplayColor(int row, int col)
	{
		if (col == 0)
			return null;

		else if (linkedOffset != -1 && col >= linkedOffset)
			return linkedModel.getDisplayColor(row, linkedModelCols[col - linkedOffset]);

		else if (traitsOffset != -1 && col >= traitsOffset)
			return traitsModel.getDisplayColor(row, traitsModelCols[col - traitsOffset]);

		return null;
	}
}