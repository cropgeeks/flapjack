// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class SortLinesDialog extends JDialog implements ActionListener
{
	JButton bOK, bCancel;
	private boolean isOK = false;

	private GenotypePanel gPanel;
	private SortLinesPanelNB nbPanel;

	public SortLinesDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortLinesDialog.title"),
			true
		);

		this.gPanel = gPanel;
		nbPanel = new SortLinesPanelNB(this, gPanel.getViewSet());

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
		bOK = new JButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		JPanel p1 = new DialogPanel();
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

		bOK.setEnabled(!view.isDummyLine(index) && !view.isSplitter(index));
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
		return ((LineInfo)nbPanel.selectedLine.getSelectedItem()).getLine();
	}
}