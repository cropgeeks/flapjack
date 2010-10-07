// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class AlleleStatisticsDialog extends JDialog implements ActionListener
{
	private JButton bClose;

	private NBAlleleStatisticsPanel nbPanel;

	public AlleleStatisticsDialog(GTViewSet viewSet, ArrayList<int[]> results)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.AlleleStatisticsDialog.title"),
			true
		);

		nbPanel = new NBAlleleStatisticsPanel(viewSet, results);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
//		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}
}