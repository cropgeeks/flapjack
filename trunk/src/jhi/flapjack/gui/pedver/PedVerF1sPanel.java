// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

public class PedVerF1sPanel extends JPanel implements ActionListener
{
	private JTable table;
	private PedVerF1sTableModel model;

	private PedVerF1sPanelNB controls;

	// Remembers the last set of auto-select values used
	private FilterColumn[] lastSelect;

	public PedVerF1sPanel(GTViewSet viewSet)
	{
		controls = new PedVerF1sPanelNB(this);

		table = controls.table;

		setLayout(new BorderLayout());
		add(new TitlePanel("PedVer Known Parents Results"), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewSet.getDataSet(), viewSet);
	}

	public void updateModel(DataSet dataSet, GTViewSet viewSet)
	{
		model = new PedVerF1sTableModel(dataSet, viewSet);

		table.setModel(model);
		((LineDataTable)table).setViewSet(viewSet);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bFilter)
			((LineDataTable)table).filter();

		else if (e.getSource() == controls.bSort)
			((LineDataTable)table).multiColumnSort();

		else if (e.getSource() == controls.bSelect)
			displayAutoSelectDialog();
	}

	public void modelChanged()
	{
		model.fireTableStructureChanged();
	}

	public void displayAutoSelectDialog()
	{
		FilterDialog dialog = FilterDialog.getSelectDialog(model.getFilterableColumns(), lastSelect);
		if (dialog.isOK() == false)
			return;

		// Get the list of columns to use for selection
		FilterColumn[] data = dialog.getResults();
		// Remember it for next time in case the user runs it again
		lastSelect = dialog.getResults();

		model.selectLines(data);
	}
}