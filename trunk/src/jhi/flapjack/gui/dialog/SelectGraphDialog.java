// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class SelectGraphDialog extends JDialog implements ActionListener
{
	private JButton bClose;
	private SelectGraphPanelNB panel;
	private GenotypePanel gPanel;

	public SelectGraphDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.SelectGraphDialog.title"),
			true);

		this.gPanel = gPanel;

		add(panel = new SelectGraphPanelNB(gPanel));
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		panel.graph1.addActionListener(this);
		panel.graph2.addActionListener(this);
		panel.graph3.addActionListener(this);
		panel.graphTypeCombo.addActionListener(this);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = new JButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = new DialogPanel();
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof JComboBox)
		{
			Prefs.guiGraphStyle = panel.graphTypeCombo.getSelectedIndex();

			int[] graphs = gPanel.getViewSet().getGraphs();
			graphs[0] = panel.graph1.getSelectedIndex();
			graphs[1] = panel.graph2.getSelectedIndex() - 1;
			graphs[2] = panel.graph3.getSelectedIndex() - 1;

			gPanel.refreshView();
			gPanel.setVisibleStates();
			Actions.projectModified();
		}

		else if (e.getSource() == bClose)
		{
			setVisible(false);
		}
	}
}