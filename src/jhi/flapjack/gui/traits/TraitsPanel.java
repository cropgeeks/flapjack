// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.traits;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class TraitsPanel extends JPanel implements ActionListener
{
	private DataSet dataSet;

	private LineDataTable table;
	private TraitsTableModel model;

	private TraitsPanelNB controls;

	public TraitsPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		controls = new TraitsPanelNB(dataSet);
		controls.bImport.addActionListener(this);
		controls.bExport.addActionListener(this);
		controls.bRemove.addActionListener(this);
		controls.bColors.addActionListener(this);

		table = (LineDataTable) controls.table;
		table.autoResize(false, true);

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel();
	}

	public LineDataTableModel getModel()
		{ return model; }

	public void updateModel()
	{
		model = new TraitsTableModel(dataSet);

		table.setModel(model);

		controls.statusLabel.setText(
			RB.format("gui.traits.TraitsPanel.traitCount",
			(table.getColumnCount()-1)));

		// Enable/disable the "remove" button based on the trait count
		controls.bExport.setEnabled(table.getColumnCount()-1 > 0);
		controls.bRemove.setEnabled(table.getColumnCount()-1 > 0);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bImport)
			Flapjack.winMain.mFile.fileImport(1);

		else if (e.getSource() == controls.bExport)
			Flapjack.winMain.mData.dataExportTraits();

		else if (e.getSource() == controls.bRemove)
		{
			String msg = RB.getString("gui.traits.TraitsPanel.removeMsg");
			String[] options = new String[] {
					RB.getString("gui.traits.TraitsPanel.remove"),
					RB.getString("gui.text.cancel") };

			int response = TaskDialog.show(msg, TaskDialog.QST, 1, options);

			if (response == 0)
				removeAllTraits();
		}

		else if (e.getSource() == controls.bColors)
		{
			TraitColorsDialog dialog = new TraitColorsDialog(dataSet);

		}
	}

	public void removeAllTraits()
	{
		// Remove the traits from the dataset
		dataSet.getTraits().clear();

		// Remove the trait values from the lines
		for (Line line: dataSet.getLines())
			line.getTraitValues().clear();

		// Remove any trait display (column) indices from the views
		for (GTViewSet viewSet: dataSet.getViewSets())
		{
			viewSet.setTraits(new int[0]);
			viewSet.setTxtTraits(new int[0]);
		}
		
		updateModel();
		Actions.projectModified();
	}
}