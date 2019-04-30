// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class MABCStatsDialog extends JDialog implements ActionListener, ChangeListener
{
	private JTabbedPane tabs;
	private MABCStatsSinglePanelNB singlePanel;
	private MABCStatsBatchPanelNB batchPanel;

	private JButton bOK, bCancel, bHelp;
	private boolean isOK;

	public MABCStatsDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.MABCStatsDialog.title"),
			true
		);

		singlePanel = new MABCStatsSinglePanelNB(viewSet);
		batchPanel = new MABCStatsBatchPanelNB();

		tabs = new JTabbedPane();
		tabs.addTab("Overview", createOverviewPanel());
		tabs.addTab("Single Analysis", singlePanel);
		tabs.addTab("Batch Analysis", batchPanel);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		tabs.addChangeListener(this);
		FlapjackUtils.initDialog(this, bOK, bCancel, true, singlePanel);
	}

	public MABCStatsSinglePanelNB getSingleUI()
		{ return singlePanel; }

	private JPanel createButtons()
	{
		bOK = new JButton("Run");
		bOK.addActionListener(this);
		bOK.setEnabled(false);

		bCancel = new JButton(RB.getString("gui.text.close"));
		bCancel.addActionListener(this);

		bHelp = new JButton(RB.getString("gui.text.help"));
		FlapjackUtils.setHelp(bHelp, "mabc.html");

		JPanel p1 = new DialogPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			// if (on this tab)
			if (true)
				if (singlePanel.isOK() == false)
					return;

			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == tabs)
		{
			if (tabs.getSelectedIndex() == 0)
				bOK.setEnabled(false);
			else
				bOK.setEnabled(true);
		}
	}

	public boolean isOK()
		{ return isOK; }

	JPanel createOverviewPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
		JLabel label = new JLabel("<html><p>Marker Assisted Back Crossing statistics will calculate Recurrent Parent Percentages for each<br>"
			+ "line across each chromosome, and will also display linkage drag and QTL status information if<br>"
			+ "appropriate.</p><p>&nbsp;</p><p>"
			+ "You can either run a single analysis that will process only the currently selected view, or a<br>"
			+ "batch analysis that will calculate statistics for all datasets and views currently loaded.</p></html>");

		panel.add(label);
		return panel;
	}
}