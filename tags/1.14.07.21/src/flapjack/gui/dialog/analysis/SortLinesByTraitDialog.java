// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class SortLinesByTraitDialog extends JDialog implements ActionListener
{
	JButton bOK, bCancel;
	private boolean isOK = false;

	private SortLinesByTraitPanelNB nbPanel;

	public SortLinesByTraitDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortLinesByTraitDialog.title"),
			true
		);

		nbPanel = new SortLinesByTraitPanelNB(gPanel);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		if (nbPanel.model.getRowCount() == 0)
			bOK.setEnabled(false);

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
		if (e.getSource() == bOK && nbPanel.isOK())
			isOK = true;

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public int[] getTraitIndices()
	{
		return nbPanel.model.getTraitIndices();
	}

	public boolean[] getAscendingIndices()
	{
		return nbPanel.model.getAscendingIndices();
	}
}