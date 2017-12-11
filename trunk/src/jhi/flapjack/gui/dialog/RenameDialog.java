// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class RenameDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel;
	private boolean isOK = false;

	private RenamePanelNB nbPanel;

	public RenameDialog(String currentValue)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.RenameDialog.title"),
			true
		);

		nbPanel = new RenamePanelNB(currentValue);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bOK, bCancel, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bOK = new JButton(RB.getString("gui.dialog.RenameDialog.renameButton"));
		bOK.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		JPanel p1 = new DialogPanel();
		p1.add(bOK);
		p1.add(bCancel);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
			isOK = true;

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public String getNewName()
		{ return nbPanel.getString(); }
}