package jhi.flapjack.gui.visualization;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.table.*;

import java.text.DecimalFormat;

/**
 * Created by gs40939 on 18/05/2016.
 */
public class TablePanelTableModel extends LineDataTableModel
{
	private GTViewSet viewSet;

	private DecimalFormat df = new DecimalFormat("0.000");

	public TablePanelTableModel(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		if (viewSet == null || !viewSet.getDisplayLineScores())
			columnNames = new String [] { "" };
		else
			columnNames = new String[] { "", "" };
	}

	@Override
	public int getRowCount()
	{
		return viewSet == null ? 0 : viewSet.getLines().size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (columnIndex == 0)
			return viewSet.getLines().get(rowIndex);
		else
			return df.format(viewSet.getLines().get(rowIndex).getScore());
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return LineInfo.class;
		else if (columnIndex == 1)
			return String.class;
		else
			return Integer.class;
	}
}
