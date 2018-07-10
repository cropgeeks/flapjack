// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.forwardbreeding;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class ForwardBreedingPanel extends JPanel implements ActionListener, ListSelectionListener, ITableViewListener, TableModelListener
{
	private LineDataTable table;
	private ForwardBreedingTableModel model;
	private GTViewSet viewSet;

	private LinkedTableHandler tableHandler;

	private ForwardBreedingPanelNB controls;

	private int rank = 1;

	public ForwardBreedingPanel(GTViewSet viewSet)
	{
		controls = new ForwardBreedingPanelNB(this);
		this.viewSet = viewSet;

		table = (LineDataTable) controls.table;
		table.getSelectionModel().addListSelectionListener(this);
		table.addViewListener(this);

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.forwardbreeding.ForwardBreedingPanel.title")), BorderLayout.NORTH);
		add(controls);

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
	}

	private void updateModel(GTViewSet viewset)
	{
		model = new ForwardBreedingTableModel(viewset);
		model.addTableModelListener(this);

		table.setModel(model);
		table.setViewSet(viewSet);

		tableFiltered();
	}

	@Override
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