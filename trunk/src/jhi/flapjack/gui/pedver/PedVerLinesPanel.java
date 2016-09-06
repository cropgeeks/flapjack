// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

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

public class PedVerLinesPanel extends JPanel implements ActionListener, ITableViewListener, TableModelListener
{
	private LineDataTable table;
	private PedVerLinesTableModel model;

	private LinkedTableHandler tableHandler;

	private PedVerLinesPanelNB controls;

	public PedVerLinesPanel(GTViewSet viewSet)
	{
		controls = new PedVerLinesPanelNB(this);

		table = (LineDataTable) controls.table;
		table.addViewListener(this);

		// Extract the test line's info from the first line in the view (they
		// all hold the same reference anyway)
		LineInfo line = viewSet.getLines().get(0);
		PedVerLinesResults results = line.results().getPedVerLinesStats().getPedVerLinesResults();

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.pedver.PedVerLinesPanel.title")), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewSet.getDataSet(), viewSet);

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
	}

	public void updateModel(DataSet dataSet, GTViewSet viewSet)
	{
		model = new PedVerLinesTableModel(dataSet, viewSet);
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

		else if (e.getSource() == controls.autoResize)
			table.autoResize(controls.autoResize.isSelected());
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = table.getMenu().createPopupMenu();

		menu.add(new JPopupMenu.Separator(), 1);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void tableSorted()
	{
	}

	public void tableChanged(TableModelEvent e)
	{
		tableFiltered();
	}

	public void tableFiltered()
	{
		controls.filterLabel.setText(table.getLineStatusText());
	}
}