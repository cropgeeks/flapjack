// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
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

public class PedVerLinesStatsDialog extends JDialog implements ActionListener
{
	private JTabbedPane tabs;
	private PedVerLinesStatsSinglePanelNB singlePanel;
	private PedVerLinesStatsBatchPanelNB batchPanel;

	private JButton bOK, bCancel, bHelp;
	private boolean isOK, isSingle;

	public PedVerLinesStatsDialog(GTViewSet viewSet, ArrayList<GTViewSet> viewSets)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.PedVerLinesStatsDialog.title"),
			true
		);

		JPanel overview = createOverviewPanel();
		singlePanel = new PedVerLinesStatsSinglePanelNB(viewSet);
		batchPanel = new PedVerLinesStatsBatchPanelNB(viewSets);

		add(createButtons(), BorderLayout.SOUTH);

		tabs = new JTabbedPane();
		tabs.addTab("Overview", overview);
		tabs.addTab("Single Analysis", singlePanel);
		tabs.addTab("Batch Analysis", batchPanel);
		tabs.setSelectedIndex(Prefs.pedVerLinesDialogTab);
		bOK.setEnabled(tabs.getSelectedIndex() != 0);
		tabs.addChangeListener(e -> {
			bOK.setEnabled(tabs.getSelectedIndex() != 0);
			Prefs.pedVerLinesDialogTab = tabs.getSelectedIndex();
		});

		add(tabs);

		FlapjackUtils.initDialog(this, bOK, bCancel, true, overview, singlePanel, batchPanel);
	}

	public PedVerLinesStatsSinglePanelNB getSingleUI()
		{ return singlePanel; }

	public PedVerLinesStatsBatchPanelNB getBatchUI()
		{ return batchPanel; }

	private JPanel createButtons()
	{
		bOK = new JButton("Run");
		bOK.addActionListener(this);
		bOK.setEnabled(false);

		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

//		bHelp = new JButton(RB.getString("gui.text.help"));
//		FlapjackUtils.setHelp(bHelp, "pedver_f1s_known_parents.html");

		JPanel p1 = new DialogPanel();
		p1.add(bOK);
		p1.add(bCancel);
//		p1.add(bHelp);

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
		JLabel label = new JLabel("<html><p>Pedigree Verification of Lines (Parent Contribution) will...</p><p>&nbsp;</p><p>"
			+ "You can either run a single analysis that will process only the currently selected view, or a<br>"
			+ "batch analysis that will calculate statistics for all datasets and views currently loaded.</p></html>");

		panel.add(label);
		return panel;
	}
}