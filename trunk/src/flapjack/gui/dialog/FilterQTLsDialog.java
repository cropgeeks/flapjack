// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class FilterQTLsDialog extends JDialog implements ActionListener
{
	private JButton bFilter, bClose, bHelp;

	private FilterQTLsPanelNB nbPanel;

	public FilterQTLsDialog(GenotypePanel gPanel, DataSet dataSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.FilterQTLsDialog.title"),
			false
		);

		nbPanel = new FilterQTLsPanelNB(gPanel, dataSet);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiFilterQTLDialogX = getLocation().x;
				Prefs.guiFilterQTLDialogY = getLocation().y;
			}
		});

		getRootPane().setDefaultButton(bFilter);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setResizable(false);

		SwingUtils.positionWindow(
			this, null, Prefs.guiFilterQTLDialogX, Prefs.guiFilterQTLDialogY);

		setVisible(true);
	}

	private JPanel createButtons()
	{
		bFilter = new JButton(RB.getString("gui.dialog.FilterQTLsDialog.bFilter"));
		RB.setText(bFilter, "gui.dialog.FilterQTLsDialog.bFilter");
		bFilter.addActionListener(this);

		bClose = new JButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Filter_QTLs");

		JPanel p1 = new DialogPanel();
		p1.add(bFilter);
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFilter)
			nbPanel.filterQTLs();

		else if (e.getSource() == bClose)
			setVisible(false);
	}
}