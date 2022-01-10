// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver.lines;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class PedVerLinesPanel extends JPanel implements ActionListener, ListSelectionListener, ITableViewListener, TableModelListener
{
	public JTabbedPane tabs;

	private LineDataTable table;
	private PedVerLinesTableModel model;
	private GTViewSet viewSet;

	private LinkedTableHandler tableHandler;

	private PedVerLinesPanelNB controls;
	private PedVerLinesSummaryPanelNB summaryControls;

	// Variables used to 'remember' what the user picked last time they
	// auto-selected or ranked lines
	private int rank = 1;

	public PedVerLinesPanel(GTViewSet viewSet)
	{
		controls = new PedVerLinesPanelNB(this);
		summaryControls = new PedVerLinesSummaryPanelNB(this, viewSet);
		this.viewSet = viewSet;

		table = (LineDataTable) controls.table;
		table.getSelectionModel().addListSelectionListener(this);
		table.addViewListener(this);

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.pedver.PedVerLinesPanel.title")), BorderLayout.NORTH);

		tabs = new JTabbedPane();
		tabs.add(controls, "Analysis Results");
		tabs.add(summaryControls, "Results Summary");
		add(tabs);

		updateModel(viewSet);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handlePopup(e);
			}
			public void mouseReleased(MouseEvent e) {
				handlePopup(e);
			}
		});

		tableHandler = viewSet.tableHandler();
		tableHandler.linkTable(table, model);

		controls.autoResize.setSelected(tableHandler.isAutoResize());
		summaryControls.autoResize.setSelected(viewSet.getPedVerLinesBatchList().isAutoResize());
	}

	public void updateModel(GTViewSet viewSet)
	{
		model = new PedVerLinesTableModel(viewSet);
		model.addTableModelListener(this);

		table.setModel(model);
		table.setViewSet(viewSet);

		tableFiltered();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bSort)
			table.sortDialog();

		else if (e.getSource() == controls.bExport)
			table.exportData();

		else if (e.getSource() == controls.bRank)
			table.rankSelectedLines(rank, model.getRankIndex());

		else if (e.getSource() == controls.autoResize)
			table.autoResize(controls.autoResize.isSelected(), false);

		else if (e.getSource() == controls.bThreshold)
			table.pedVerLinesThresholdDialog();

		else if (e.getSource() == summaryControls.autoResize)
		{
			boolean state = summaryControls.autoResize.isSelected();
			((SummaryTable)summaryControls.table).autoResize(state);
			viewSet.getPedVerLinesBatchList().setAutoResize(state);
		}

		else if (e.getSource() == summaryControls.bExport)
			((SummaryTable)summaryControls.table).exportData();
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		controls.bRank.setEnabled(
			table.getSelectionModel().getMinSelectionIndex() != -1);
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = table.getMenu().createPopupMenu();

		final JMenuItem mRank = new JMenuItem();
		mRank.setText("Rank...");
		mRank.setIcon(Icons.getIcon("RANK"));
		mRank.addActionListener(event -> rank = table.rankSelectedLines(rank, model.getRankIndex()));
		mRank.setEnabled(table.getSelectionModel().getMinSelectionIndex() != -1);

		menu.add(mRank, 1);
		menu.add(new JPopupMenu.Separator(), 2);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void tablePreSorted() {}

	public void tableSorted() {}

	public void tableChanged(TableModelEvent e)
	{
		tableFiltered();
	}

	public void tableFiltered()
	{
		controls.filterLabel.setText(table.getLineStatusText());
	}
}