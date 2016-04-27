// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.dialog.*;

import scri.commons.gui.*;

public class MabcPanel extends JPanel implements ActionListener
{
	private JTable table;
	private MabcTableModel model;

	private MabcPanelNB controls;

	public MabcPanel(GTViewSet viewset, ArrayList<MABCLineStats> lineStats)
	{
		controls = new MabcPanelNB();

		table = controls.table;
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(Float.class, new TraitCellRenderer());
		table.setDefaultRenderer(String.class, new TraitCellRenderer());
		UIScaler.setCellHeight(table);

		setLayout(new BorderLayout());
		add(new TitlePanel("MABC Results"), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewset.getDataSet(), lineStats);
	}

	public void updateModel(DataSet dataSet, ArrayList<MABCLineStats> lineStats)
	{
		model = new MabcTableModel(dataSet, lineStats);

		table.setRowSorter(new TableRowSorter<MabcTableModel>(model));
		table.setModel(model);

		// Size and set the editor for each column
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(120);
		}

		NumberFormat nf = NumberFormat.getInstance();
//		controls.coverageLabel.setText("RPP Coverage: " + nf.format(lineStats.get(0).getCoverage()));
	}

	public void actionPerformed(ActionEvent e)
	{
	}

	// Deals with the fact that our fake double header for the JTable means
	// that String data can be found in numerical columns.
	public class TraitCellRenderer extends DefaultTableCellRenderer
	{
		protected final NumberFormat nf = NumberFormat.getInstance();

		private Color bgCol1 = UIManager.getColor("Table.selectionBackground");
		private Color bgCol2 = UIManager.getColor("Table.background");

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			if (value instanceof Float && value != null)
			{
				setText(nf.format((Number)value));
				setHorizontalAlignment(JLabel.RIGHT);
			}
			else
				setHorizontalAlignment(JLabel.LEFT);

			return this;
		}
	}
}