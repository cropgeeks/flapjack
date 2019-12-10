// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.forwardbreeding.*;
import jhi.flapjack.gui.mabc.*;
import jhi.flapjack.gui.pedver.f1s.*;
import jhi.flapjack.gui.pedver.lines.*;

import scri.commons.gui.*;

public class SummaryTable extends JTable
{
	private SummaryTableModel model;
	private TableRowSorter<SummaryTableModel> sorter;

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
		SummaryTableModel model = (SummaryTableModel) getModel();

		if (model instanceof MabcSummaryTableModel)
		{
			MabcBatchList list = ((MabcSummaryTableModel)model).getBatchList();
			MabcSummary summary = list.getSummaries().get(modelRow);

			// Selects (and returns) the panel in the nav pane
			MabcPanel panel = (MabcPanel) Flapjack.winMain.getNavPanel().selectMabcNode(summary.getViewSet());
			// Tell the panel we want to show the first tab
			panel.tabs.setSelectedIndex(0);
		}

		else if (model instanceof PedVerF1sSummaryTableModel)
		{
			PedVerF1sBatchList list = ((PedVerF1sSummaryTableModel)model).getBatchList();
			PedVerF1sSummary summary = list.getSummaries().get(modelRow);

			// Selects (and returns) the panel in the nav pane
			PedVerF1sPanel panel = (PedVerF1sPanel) Flapjack.winMain.getNavPanel().selectPedVerF1sNode(summary.getViewSet());
			// Tell the panel we want to show the first tab
			panel.tabs.setSelectedIndex(0);
		}

		else if (model instanceof PedVerLinesSummaryTableModel)
		{
			PedVerLinesBatchList list = ((PedVerLinesSummaryTableModel)model).getBatchList();
			PedVerLinesSummary summary = list.getSummaries().get(modelRow);

			// Selects (and returns) the panel in the nav pane
			PedVerLinesPanel panel = (PedVerLinesPanel) Flapjack.winMain.getNavPanel().selectPedVerLinesNode(summary.getViewSet());
			// Tell the panel we want to show the first tab
			panel.tabs.setSelectedIndex(0);
		}

		else if (model instanceof ForwardBreedingSummaryTableModel)
		{
			ForwardBreedingBatchList list = ((ForwardBreedingSummaryTableModel)model).getBatchList();
			ForwardBreedingSummary summary = list.getSummaries().get(modelRow);

			// Selects (and returns) the panel in the nav pane
			ForwardBreedingPanel panel = (ForwardBreedingPanel) Flapjack.winMain.getNavPanel().selectForwardBreedingNode(summary.getViewSet());
			// Tell the panel we want to show the first tab
			panel.tabs.setSelectedIndex(0);
		}
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

	@Override
	public void setModel(TableModel tm)
	{
		boolean firstInit = (getModel() == null);

		super.setModel(tm);

		// Set a default width per column (only when first creating the table)
//		if (firstInit)
//		{
//			for (int i = 0; i < getColumnCount(); i++)
//			{
//				TableColumn column = getColumnModel().getColumn(i);
//				column.setPreferredWidth(120);
//			}
//		}

		// Safety net for Matisse code calling setModel with a DefaultTableModel
		if (tm instanceof SummaryTableModel)
		{
			model = (SummaryTableModel) tm;

			// Let the user sort by column
			sorter = new TableRowSorter<>(model);
			setRowSorter(sorter);
		}
	}

	public void autoResize(boolean autoResize)
	{
		if (autoResize)
			setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		else
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
}