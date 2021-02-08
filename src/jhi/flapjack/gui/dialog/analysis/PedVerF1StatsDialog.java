// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class PedVerF1StatsDialog extends JDialog implements ActionListener
{
	private JTabbedPane tabs;
	private PedVerF1StatsSinglePanelNB singlePanel;
	private PedVerF1StatsBatchPanelNB batchPanel;

	private JButton bOK, bCancel, bHelp;
	private boolean isOK, isSingle;

	public PedVerF1StatsDialog(GTViewSet viewSet, ArrayList<GTViewSet> viewSets)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.PedVerF1StatsDialog.title"),
			true
		);

		JPanel overview = createOverviewPanel();
		singlePanel = new PedVerF1StatsSinglePanelNB(viewSet);
		batchPanel = new PedVerF1StatsBatchPanelNB(viewSets);

		add(createButtons(), BorderLayout.SOUTH);

		tabs = new JTabbedPane();
		tabs.addTab("Overview", overview);
		tabs.addTab("Single Analysis", singlePanel);
		tabs.addTab("Batch Analysis", batchPanel);
		tabs.setSelectedIndex(Prefs.pedVerF1DialogTab);
		bOK.setEnabled(tabs.getSelectedIndex() != 0);
		tabs.addChangeListener(e -> {
			bOK.setEnabled(tabs.getSelectedIndex() != 0);
			Prefs.pedVerF1DialogTab = tabs.getSelectedIndex();
		});

		add(tabs);

		FlapjackUtils.initDialog(this, bOK, bCancel, true, overview, singlePanel, batchPanel);
	}

	public PedVerF1StatsSinglePanelNB getSingleUI()
		{ return singlePanel; }

	public PedVerF1StatsBatchPanelNB getBatchUI()
		{ return batchPanel; }

	private JPanel createButtons()
	{
		bOK = new JButton("Run");
		bOK.addActionListener(this);
		bOK.setEnabled(false);

		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		bHelp = new JButton(RB.getString("gui.text.help"));
		FlapjackUtils.setHelp(bHelp, "pedver_f1s_known_parents.html");

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

	public boolean isOK()
		{ return isOK; }

	public boolean isSingle()
		{ return isSingle; }

	private JPanel createOverviewPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
		JLabel label = new JLabel("<html><p>Pedigree Verification of F1s (Known Parents) will calculate statistics for each line comparing it to<br>"
			+ "the parents and either a supplied or simulated F1.</p><p>&nbsp;</p><p>"
			+ "You can either run a single analysis that will process only the currently selected view, or a<br>"
			+ "batch analysis that will calculate statistics for all datasets and views currently loaded.</p></html>");

		panel.add(label);
		return panel;
	}
}