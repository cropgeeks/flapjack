// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

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

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.dialog.RenameDialog.renameButton"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		JPanel p1 = FlapjackUtils.getButtonPanel();
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