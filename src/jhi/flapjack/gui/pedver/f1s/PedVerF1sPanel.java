// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver.f1s;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.Icons;

public class PedVerF1sPanel extends JPanel implements ActionListener, ListSelectionListener, ITableViewListener, TableModelListener
{
	public JTabbedPane tabs;

	private LineDataTable table;
	private PedVerF1sTableModel model;
	private GTViewSet viewSet;

	private LinkedTableHandler tableHandler;

	private PedVerF1sPanelNB controls;
	private PedVerF1sSummaryPanelNB summaryControls;

	// Variables used to 'remember' what the user picked last time they
	// auto-selected or ranked lines
	private int rank = 1;

	public PedVerF1sPanel(GTViewSet viewSet)
	{
		controls = new PedVerF1sPanelNB(this);
		summaryControls = new PedVerF1sSummaryPanelNB(this, viewSet);
		this.viewSet = viewSet;

		table = (LineDataTable) controls.table;
		table.getSelectionModel().addListSelectionListener(this);
		table.addViewListener(this);

		setLayout(new BorderLayout());
		add(new TitlePanel("Pedigree Verification of F1s (Known Parents)"), BorderLayout.NORTH);

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
		table.setColorCells(true);

		controls.autoResize.setSelected(tableHandler.isAutoResize());
		summaryControls.autoResize.setSelected(viewSet._getPedVerF1sBatchList().isAutoResize());
	}

	private void updateModel(GTViewSet viewSet)
	{
		model = new PedVerF1sTableModel(viewSet);
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
			rank = table.rankSelectedLines(rank, model.getRankIndex());

		else if (e.getSource() == controls.autoResize)
			table.autoResize(controls.autoResize.isSelected(), false);

		else if (e.getSource() == controls.bThreshold)
			table.thresholdDialog();

		else if (e.getSource() == summaryControls.autoResize)
		{
			boolean state = summaryControls.autoResize.isSelected();
			((SummaryTable)summaryControls.table).autoResize(state);
			viewSet._getPedVerF1sBatchList().setAutoResize(state);
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