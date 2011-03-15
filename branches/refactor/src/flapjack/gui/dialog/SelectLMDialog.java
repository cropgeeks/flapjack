// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class SelectLMDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel;
	private boolean isOK = false;

	private NBSelectLMPanel nbPanel;

	public SelectLMDialog(GTView view, boolean selectLines)
	{
		super(Flapjack.winMain, "", true);

		if (selectLines)
			setTitle(RB.getString("gui.dialog.SelectLMDialog.lineTitle"));
		else
			setTitle(RB.getString("gui.dialog.SelectLMDialog.markerTitle"));

		nbPanel = new NBSelectLMPanel(view, selectLines);

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
		bOK = SwingUtils.getButton(RB.getString("gui.text.ok"));
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

	public int getSelectedIndex()
		{ return nbPanel.combo.getSelectedIndex(); }
}