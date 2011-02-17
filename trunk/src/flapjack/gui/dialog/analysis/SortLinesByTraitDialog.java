// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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

	private NBSortLinesByTraitPanel nbPanel;

	public SortLinesByTraitDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortLinesByTraitDialog.title"),
			true
		);

		nbPanel = new NBSortLinesByTraitPanel(gPanel);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		if (nbPanel.combo1.getItemCount() == 0)
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

	// Returns an array of length 1 to 3, containing the selected indexes of the
	// three combo boxes
	public int[] getTraitIndices()
	{
		int[] selected = new int[] {
			nbPanel.combo1.getSelectedIndex(),
			nbPanel.combo2.getSelectedIndex()-1,
			nbPanel.combo3.getSelectedIndex()-1
		};

		int count = 0;
		for (int i: selected)
			if (i != -1) count++;

		// The new array will only store values that are selected, so if only
		// one combo box was used, the array will have length == 1
		int[] traits = new int[count];
		for (int i = 0, t = 0; i < selected.length; i++)
			if (selected[i] != -1)
				traits[t++] = selected[i];

		return traits;
	}

	public boolean[] getAscendingIndices()
	{
		return new boolean[] {
			nbPanel.rAsc1.isSelected(),
			nbPanel.rAsc2.isSelected(),
			nbPanel.rAsc3.isSelected()
		};
	}
}