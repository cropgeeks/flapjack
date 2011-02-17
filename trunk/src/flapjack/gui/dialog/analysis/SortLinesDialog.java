// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class SortLinesDialog extends JDialog implements ActionListener
{
	JButton bOK, bCancel;
	private boolean isOK = false;

	private GenotypePanel gPanel;
	private NBSortLinesPanel nbPanel;

	public SortLinesDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortLinesDialog.title"),
			true
		);

		this.gPanel = gPanel;
		nbPanel = new NBSortLinesPanel(this, gPanel.getViewSet());

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);
		checkSelectedLine();

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

	// Checks to ensure that a dummy line hasn't been selected (if it has, it's
	// not safe to run the sort as it'll get removed before it happens and then
	// there won't be a comparison line (or it'll be wrong!))
	private void checkSelectedLine()
	{
		int index = nbPanel.selectedLine.getSelectedIndex();
		GTView view = gPanel.getView();

		bOK.setEnabled(!view.isDummyLine(view.getLine(index)));
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == nbPanel.selectedLine)
			checkSelectedLine();

		else if (e.getSource() == bOK)
		{
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK() {
		return isOK;
	}

	public boolean[] getSelectedChromosomes()
	{
		return nbPanel.getSelectedChromosomes();
	}

	public Line getSelectedLine()
	{
		return (Line) nbPanel.selectedLine.getSelectedItem();
	}
}