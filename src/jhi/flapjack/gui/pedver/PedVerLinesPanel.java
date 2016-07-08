// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class PedVerLinesPanel extends JPanel implements ActionListener
{
	private JTable table;
	private PedVerLinesTableModel model;

	private PedVerLinesPanelNB controls;

	NumberFormat nf = NumberFormat.getPercentInstance();

	public PedVerLinesPanel(GTViewSet viewSet)
	{
		controls = new PedVerLinesPanelNB(this);

		table = controls.table;

		nf.setMinimumFractionDigits(2);

		// Extract the test line's info from the first line in the view (they
		// all hold the same reference anyway)
		LineInfo line = viewSet.getLines().get(0);
		PedVerLinesResults results = line.results().getPedVerLinesStats().getPedVerLinesResults();

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
		if (e.getSource() == controls.bFilter)
			((LineDataTable)table).filterDialog();

		else if (e.getSource() == controls.bSort)
			((LineDataTable)table).sortDialog();

		else if (e.getSource() == controls.bSelect)
			((LineDataTable)table).selectDialog();

		else if (e.getSource() == controls.autoResize)
			((LineDataTable)table).autoResize(controls.autoResize.isSelected());
	}

	public void modelChanged()
	{
		model.fireTableStructureChanged();
	}
}