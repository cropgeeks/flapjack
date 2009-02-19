package flapjack.gui.traits;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
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

	private NBTraitsControlPanel controls;
	static TraitsTableRenderer traitsRenderer = new TraitsTableRenderer(JLabel.RIGHT);

	public TraitsPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(String.class, traitsRenderer);
		table.setDefaultRenderer(Float.class, traitsRenderer);

		controls = new NBTraitsControlPanel();
		controls.bImport.addActionListener(this);
		controls.bRemove.addActionListener(this);

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(new JScrollPane(table));
		add(controls, BorderLayout.SOUTH);

		updateModel();
	}

	public void updateModel()
	{
		model = new TraitsTableModel(dataSet, table);
		if (SystemUtils.jreVersion() >= 1.6)
			new SortHandler();

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
		controls.bRemove.setEnabled(table.getColumnCount()-1 > 0);
	}

	// This is done in a separate class to hide its implementation from OS X on
	// Java5 that will throw ClassNotFoundExceptions if it tries to run it
	private class SortHandler
	{
		SortHandler()
		{
			table.setRowSorter(new TableRowSorter<TraitsTableModel>(model));
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bImport)
			Flapjack.winMain.mFile.importTraitData();

		else if (e.getSource() == controls.bRemove)
		{
			String msg = RB.getString("gui.traits.TraitsPanel.removeMsg");
			String[] options = new String[] {
					RB.getString("gui.traits.TraitsPanel.remove"),
					RB.getString("gui.text.cancel") };

			int response = TaskDialog.show(msg, MsgBox.QST, 1, options);

			if (response == 0)
				removeAllTraits();
		}
	}

	private void removeAllTraits()
	{
		// Remove the traits from the dataset
		dataSet.getTraits().clear();

		// Remove the trait values from the lines
		for (Line line: dataSet.getLines())
			line.getTraitValues().clear();

		// Remove any trait display (column) indices from the views
		for (GTViewSet viewSet: dataSet.getViewSets())
			for (int i = 0; i < viewSet.getTraits().length; i++)
				viewSet.getTraits()[i] = -1;

		updateModel();
		Actions.projectModified();
	}
}

class TraitsTableRenderer extends DefaultTableCellRenderer
{
	private static NumberFormat nf = NumberFormat.getInstance();

	TraitsTableRenderer(int alignment)
	{
		setHorizontalAlignment(alignment);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected,
			hasFocus, row, column);

		if (value instanceof Number)
			setText(nf.format((Number)value));

		return this;
	}
}