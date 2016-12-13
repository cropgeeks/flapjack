// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class SelectTraitsDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private SelectTraitsPanelNB nbPanel;

	public SelectTraitsDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.SelectTraitsDialog.title"),
			true
		);

		nbPanel = new SelectTraitsPanelNB(viewSet);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bOK, bCancel, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bOK = new JButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Select_Traits");

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
			nbPanel.isOK();
			Actions.projectModified();
		}

		setVisible(false);
	}
}