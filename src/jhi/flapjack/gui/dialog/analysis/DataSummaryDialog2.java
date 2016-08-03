// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.analysis.AnalysisSet;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class DataSummaryDialog2 extends JDialog implements ActionListener
{
	private JButton bClose;

	private DataSummaryPanelNB2 nbPanel;

	public DataSummaryDialog2(GTViewSet viewSet, boolean[] selectedChromosoems, ArrayList<long[]> results, long alleleCount)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.AlleleStatisticsDialog.title"),
			true
		);

		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(selectedChromosoems)
			.withAllLines()
			.withAllMarkers();

		nbPanel = new DataSummaryPanelNB2(viewSet, as, results, alleleCount);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
//		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = new JButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = new DialogPanel();
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}
}