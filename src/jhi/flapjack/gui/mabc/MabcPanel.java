// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

public class MabcPanel extends JPanel implements ActionListener
{
	private JTable table;
	private MabcTableModel model;
	private GTViewSet viewSet;

	private MabcPanelNB controls;

	public MabcPanel(GTViewSet viewSet)
	{
		controls = new MabcPanelNB(this);
		this.viewSet = viewSet;

		table = controls.table;

		setLayout(new BorderLayout());
		add(new TitlePanel("MABC Results"), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewSet);

		controls.bExport.addActionListener(this);
	}

	public void updateModel(GTViewSet viewset)
	{
		model = new MabcTableModel(viewset);

		table.setModel(model);
		((LineDataTable)table).setViewSet(viewSet);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bSort)
			((LineDataTable)table).multiColumnSort();

		else if (e.getSource() == controls.bExport)
			((LineDataTable)table).exportData();
	}
}