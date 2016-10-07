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

public class MissingMarkersDialog extends JDialog implements ActionListener
{
	private JButton bFilter, bCancel, bHelp;
	private boolean isOK = false;

	private MissingMarkersPanelNB nbPanel;

	public MissingMarkersDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.MissingMarkersDialog.title"),
			true
		);

		add(createButtons(), BorderLayout.SOUTH);
		nbPanel = new MissingMarkersPanelNB(viewSet, bFilter);
		add(nbPanel);

		if (viewSet.getView(0).countSelectedLines() == 0)
			bFilter.setEnabled(false);

		FlapjackUtils.initDialog(this, bFilter, bCancel, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bFilter = new JButton("Filter");
		bFilter.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
//		RB.setText(bHelp, "gui.text.help");
//		FlapjackUtils.setHelp(bHelp, "gui.dialog.DataImportDialog");

		JPanel p1 = new DialogPanel();
		p1.add(bFilter);
		p1.add(bCancel);
//		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFilter)
		{
			nbPanel.applySettings();
			isOK = true;
		}

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public boolean[] getSelectedChromosomes()
	{
		return nbPanel.csd.getSelectedChromosomes();
	}
}