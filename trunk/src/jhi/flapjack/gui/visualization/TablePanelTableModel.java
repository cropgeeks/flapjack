package jhi.flapjack.gui.visualization;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.MABCLineStats;
import jhi.flapjack.gui.table.*;

import java.text.DecimalFormat;

/**
 * Created by gs40939 on 18/05/2016.
 */
public class TablePanelTableModel extends LineDataTableModel
{
	private final GTViewSet viewSet;
	private final boolean showMabc;

	private DecimalFormat df = new DecimalFormat("0.00");

	private int rppIndex;
	private int rppTotalIndex;
	private int rppCoverageIndex;
	private int qtlIndex;

	public TablePanelTableModel(GTViewSet viewSet, boolean showMabc)
	{
		this.viewSet = viewSet;
		this.showMabc = showMabc;

		int noCols = 1;

		if (viewSet != null)
		{
			if (viewSet.getDisplayLineScores())
				noCols++;


			if (showMabc)
			{
				MABCLineStats lineStats = viewSet.getLines().get(0).results().getMABCLineStats();
				if (lineStats != null)
				{
					// Use information from the first result to determine the UI
					int chrCount = lineStats.getChrScores().size();
					int qtlCount = lineStats.getQTLScores().size();

					// Column indices
					rppIndex = noCols;
					rppTotalIndex = rppIndex + chrCount;
					rppCoverageIndex = rppTotalIndex + 1;
					qtlIndex = rppCoverageIndex + 1;

					// TODO: UPDATE!
					noCols = qtlIndex + (qtlCount * 2);
				}
			}
		}

		columnNames = new String[noCols];
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

		else if (viewSet.getDisplayLineScores() && columnIndex == 1)
			return df.format(viewSet.getLines().get(rowIndex).getScore());

		else if (showMabc)
		{
			MABCLineStats stats = viewSet.getLines().get(rowIndex).results().getMABCLineStats();
			if (stats != null)
			{
				if (columnIndex >= rppIndex && columnIndex < rppTotalIndex)
					return df.format(stats.getChrScores().get(columnIndex - rppIndex).sumRP);

				else if (columnIndex == rppTotalIndex)
					return df.format(stats.getRPPTotal());

				else if (columnIndex == rppCoverageIndex)
					return df.format(stats.getGenomeCoverage());

				else if (columnIndex >= qtlIndex)
				{
					columnIndex = columnIndex - qtlIndex;
					int qtl = columnIndex / 2;

					MABCLineStats.QTLScore score = stats.getQTLScores().get(qtl);

					if (columnIndex % 2 == 0)
						return df.format(score.drag);
					else
						return score.status ? 1 : 0;
				}
			}
		}
		return -1;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return LineInfo.class;
		else
			return Double.class;
	}
}
