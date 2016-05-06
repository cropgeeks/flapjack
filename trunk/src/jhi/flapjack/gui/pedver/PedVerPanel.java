// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

public class PedVerPanel extends JPanel implements ActionListener
{
	private JTable table;
	private PedVerTableModel model;

	private PedVerPanelNB controls;

	public PedVerPanel(GTViewSet viewset, PedVerKnownParentsResults results)
	{
		controls = new PedVerPanelNB(results);

		table = controls.table;

		setLayout(new BorderLayout());
		add(new TitlePanel("PedVer Known Parents Results"), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewset.getDataSet(), results);
	}

	public void updateModel(DataSet dataSet, PedVerKnownParentsResults results)
	{
		model = new PedVerTableModel(dataSet, results);

		table.setModel(model);
	}

	public void actionPerformed(ActionEvent e)
	{
	}
}