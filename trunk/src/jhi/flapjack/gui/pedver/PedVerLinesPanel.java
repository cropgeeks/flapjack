// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import jhi.flapjack.data.DataSet;
import jhi.flapjack.data.GTViewSet;
import jhi.flapjack.gui.TitlePanel;
import jhi.flapjack.gui.table.LineDataTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PedVerLinesPanel extends JPanel implements ActionListener
{
	private JTable table;
	private PedVerLinesTableModel model;

	private PedVerLinesPanelNB controls;

	public PedVerLinesPanel(GTViewSet viewSet)
	{
		controls = new PedVerLinesPanelNB();

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
		model = new PedVerLinesTableModel(dataSet, viewSet);

		table.setModel(model);
		((LineDataTable)table).setViewSet(viewSet);
	}

	public void actionPerformed(ActionEvent e)
	{
	}

	public void modelChanged()
	{
		model.fireTableStructureChanged();
	}
}