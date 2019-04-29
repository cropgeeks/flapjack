// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class MABCStatsDialog extends JDialog implements ActionListener
{
	private MABCStatsSinglePanelNB singlePanel;

	private JButton bOK, bCancel, bHelp;
	private boolean isOK;

	public MABCStatsDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.MABCStatsDialog.title"),
			true
		);

		singlePanel = new MABCStatsSinglePanelNB(viewSet);

		add(singlePanel);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bOK, bCancel, true, singlePanel);
	}

	public MABCStatsSinglePanelNB getSingleUI()
		{ return singlePanel; }

	private JPanel createButtons()
	{
		bOK = new JButton("Run");
		bOK.addActionListener(this);

		bCancel = new JButton(RB.getString("gui.text.close"));
		bCancel.addActionListener(this);

		bHelp = new JButton(RB.getString("gui.text.help"));
		FlapjackUtils.setHelp(bHelp, "mabc.html");

		JPanel p1 = new DialogPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			// if (on this tab)
			if (true)
				if (singlePanel.isOK() == false)
					return;

			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }
}