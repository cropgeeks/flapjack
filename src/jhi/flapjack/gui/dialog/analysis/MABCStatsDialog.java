// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
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
	private boolean isOK, isSingle;

	public MABCStatsDialog(GTViewSet viewSet, ArrayList<GTViewSet> viewSets)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.MABCStatsDialog.title"),
			true
		);

		singlePanel = new MABCStatsSinglePanelNB(viewSet);
		batchPanel = new MABCStatsBatchPanelNB(viewSets);

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

	public MABCStatsBatchPanelNB getBatchUI()
		{ return batchPanel; }

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
			if (tabs.getSelectedComponent() == singlePanel && !singlePanel.isOK())
				return;
			else if (tabs.getSelectedComponent() == batchPanel && !batchPanel.isOK())
				return;

			isSingle = tabs.getSelectedComponent() == singlePanel;
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

	public boolean isSingle()
		{ return isSingle; }

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