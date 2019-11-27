// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class SummaryTable extends JTable
{
	public SummaryTable()
	{
		setDefaultRenderer(Double.class,
			new SummaryTableModel.DoubleNumRenderer());
		setDefaultRenderer(Integer.class,
			new NumberFormatCellRenderer());

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				int row = rowAtPoint(mouseEvent.getPoint());
				if (mouseEvent.getClickCount() == 2 && getSelectedRow() != -1)
					jumpToRow(convertRowIndexToModel(row));
			}
		});
	}

	private void jumpToRow(int modelRow)
	{
/*		AnalysisSummaryTableModel model = (AnalysisSummaryTableModel) getModel();

		PedVerF1sBatchList list = model.getBatchList();
		PedVerF1sSummary summary = list.getSummary(modelRow);

		// Selects (and returns) the panel in the nav pane
		PedVerF1sPanel panel = (PedVerF1sPanel) Flapjack.winMain.getNavPanel().selectPedVerLinesNode(summary.getViewSet());
		// Tell the panel we want to show the first tab
		panel.tabs.setSelectedIndex(0);
*/
	}

	// Ensures all column headers have tooltips
	@Override
	public JTableHeader createDefaultTableHeader()
	{
		return new JTableHeader(columnModel)
		{
			@Override
			public String getToolTipText(MouseEvent e)
			{
				Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				if (index >= 0 && index < columnModel.getColumnCount())
				{
					int realIndex = columnModel.getColumn(index).getModelIndex();
					return ((SummaryTableModel)getModel()).getToolTip(realIndex);
				}
				else
					return null;
			}
		};
	}
}