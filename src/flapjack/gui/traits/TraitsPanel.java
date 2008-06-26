package flapjack.gui.traits;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class TraitsPanel extends JPanel
{
	private DataSet dataSet;

	private JTable table;
	private TraitsTableModel model;

	public TraitsPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(String.class, new TraitsTableRenderer());
		table.setDefaultRenderer(Float.class, new TraitsTableRenderer());

		updateModel();

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(new JScrollPane(table));
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
}