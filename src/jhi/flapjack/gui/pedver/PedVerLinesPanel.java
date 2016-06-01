// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import jhi.flapjack.data.DataSet;
import jhi.flapjack.data.GTViewSet;
import jhi.flapjack.data.results.PedVerLinesResults;
import jhi.flapjack.gui.TitlePanel;
import jhi.flapjack.gui.table.LineDataTable;
import scri.commons.gui.RB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class PedVerLinesPanel extends JPanel implements ActionListener
{
	private JTable table;
	private PedVerLinesTableModel model;

	private PedVerLinesPanelNB controls;

	NumberFormat nf = NumberFormat.getPercentInstance();

	public PedVerLinesPanel(GTViewSet viewSet)
	{
		controls = new PedVerLinesPanelNB();

		table = controls.table;

		nf.setMinimumFractionDigits(2);

		PedVerLinesResults results = viewSet.getPedVerLinesResults();

		controls.lblTestMarkerCount.setText(RB.format("gui.pedver.PedVerLinesPanel.markerCount", results.getTestMarkerCount()));
		controls.lblTestPercentage.setText(RB.format("gui.pedver.PedVerLinesPanel.markerPercentage", nf.format(results.getTestMarkerPresentPercentage())));
		controls.lblTestHetCount.setText(RB.format("gui.pedver.PedVerLinesPanel.hetCount", results.getTestHetCount()));
		controls.lblTestHetPercentage.setText(RB.format("gui.pedver.PedVerLinesPanel.hetPercentage", nf.format(results.getTestHetPercentage())));

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.pedver.PedVerLinesPanel.title")), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewSet.getDataSet(), viewSet);
	}

	public void updateModel(DataSet dataSet, GTViewSet viewSet)
	{
		model = new PedVerLinesTableModel(dataSet, viewSet);

		table.setModel(model);
		((LineDataTable)table).setViewSet(viewSet);
	}

	public void actionPerformed(ActionEvent e)
	{
	}

	public void modelChanged()
	{
		model.fireTableStructureChanged();
	}
}