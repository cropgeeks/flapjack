// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

public class PedVerF1sPanel extends JPanel implements ActionListener, ITableViewListener, TableModelListener
{
	private LineDataTable table;
	private PedVerF1sTableModel model;

	private LinkedTableHandler tableHandler;

	private PedVerF1sPanelNB controls;

	public PedVerF1sPanel(GTViewSet viewSet)
	{
		controls = new PedVerF1sPanelNB(this);

		table = (LineDataTable) controls.table;
		table.addViewListener(this);

		setLayout(new BorderLayout());
		add(new TitlePanel("Pedigree Verification of F1s (Known Parents)"), BorderLayout.NORTH);

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

		controls.autoResize.setSelected(tableHandler.isAutoResize());
	}

	public void updateModel(DataSet dataSet, GTViewSet viewSet)
	{
		model = new PedVerF1sTableModel(dataSet, viewSet);
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
			table.autoResize(controls.autoResize.isSelected(), false);
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