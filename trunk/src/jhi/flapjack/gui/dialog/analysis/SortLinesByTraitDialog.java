// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

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