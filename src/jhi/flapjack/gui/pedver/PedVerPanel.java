// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

public class PedVerPanel extends JPanel implements ActionListener
{
	private JTable table;
	private PedVerTableModel model;

	private PedVerPanelNB controls;

	public PedVerPanel(GTViewSet viewSet)
	{
		controls = new PedVerPanelNB(this);

		table = controls.table;

		setLayout(new BorderLayout());
		add(new TitlePanel("PedVer Known Parents Results"), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewSet.getDataSet(), viewSet);
	}

	public void updateModel(DataSet dataSet, GTViewSet viewSet)
	{
		model = new PedVerTableModel(dataSet, viewSet);

		table.setModel(model);
		((LineDataTable)table).setViewSet(viewSet);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bFilter)
			((LineDataTable)table).filter();

		else if (e.getSource() == controls.bSort)
			((LineDataTable)table).multiColumnSort();

		else if (e.getSource() == controls.bExport)
			((LineDataTable)table).exportData();
	}

	public void modelChanged()
	{
		model.fireTableStructureChanged();
	}
}