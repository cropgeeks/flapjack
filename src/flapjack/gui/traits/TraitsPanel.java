// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.traits;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class TraitsPanel extends JPanel implements ActionListener
{
	private DataSet dataSet;

	private JTable table;
	private TraitsTableModel model;

	private TraitsPanelNB controls;

	public TraitsPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		controls = new TraitsPanelNB();
		controls.bImport.addActionListener(this);
		controls.bExport.addActionListener(this);
		controls.bRemove.addActionListener(this);

		table = controls.table;
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(Float.class, new TraitNumberFormatCellRenderer());

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel();
	}

	public void updateModel()
	{
		model = new TraitsTableModel(dataSet);

		table.setRowSorter(new TableRowSorter<TraitsTableModel>(model));
		table.setModel(model);

		// Size and set the editor for each column
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(120);

			if (table.getColumnClass(i) == String.class)
				column.setCellEditor(
					new DefaultCellEditor(model.getCategoryComboBox(i)));
		}

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
			viewSet.setTraits(new int[0]);

		updateModel();
		Actions.projectModified();
	}

	// Deals with the fact that our fake double header for the JTable means
	// that String data can be found in numerical columns.
	public class TraitNumberFormatCellRenderer extends NumberFormatCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			return this;
		}
	}
}