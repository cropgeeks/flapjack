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

public class SelectLMDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel;
	private boolean isOK = false;

	private SelectLMPanelNB nbPanel;

	public SelectLMDialog(GTView view, boolean selectLines)
	{
		super(Flapjack.winMain, "", true);

		if (selectLines)
			setTitle(RB.getString("gui.dialog.SelectLMDialog.lineTitle"));
		else
			setTitle(RB.getString("gui.dialog.SelectLMDialog.markerTitle"));

		nbPanel = new SelectLMPanelNB(view, selectLines);

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

	public int getSelectedIndex()
	{
		return ((SelectLMPanelNB.IndexWrapper)nbPanel.combo.getSelectedItem()).index;
	}
}