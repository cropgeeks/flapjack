// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.event.*;
import javax.swing.*;

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
}