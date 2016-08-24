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
	private JTable table;
	private PedVerF1sTableModel model;

	private LinkedTableHandler tableHandler;

	private PedVerF1sPanelNB controls;

	public PedVerF1sPanel(GTViewSet viewSet)
	{
		controls = new PedVerF1sPanelNB(this);

		table = controls.table;
		((LineDataTable)table).addViewListener(this);

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
		tableHandler.linkTable((LineDataTable)table, model);
	}

	public void updateModel(DataSet dataSet, GTViewSet viewSet)
	{
		model = new PedVerF1sTableModel(dataSet, viewSet);
		model.addTableModelListener(this);

		table.setModel(model);
		((LineDataTable)table).setViewSet(viewSet);

		tableFiltered();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bFilter)
			((LineDataTable)table).filterDialog();

		else if (e.getSource() == controls.bSort)
			((LineDataTable)table).sortDialog();

		else if (e.getSource() == controls.bSelect)
			((LineDataTable)table).selectDialog();

		else if (e.getSource() == controls.autoResize)
			((LineDataTable)table).autoResize(controls.autoResize.isSelected());
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = ((LineDataTable)table).getPopupMenu();

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
		controls.filterLabel.setText(
			"Visible lines: " + table.getRowCount() + "/" + model.getRowCount());
	}
}