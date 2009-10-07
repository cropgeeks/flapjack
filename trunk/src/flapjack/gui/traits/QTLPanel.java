// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.traits;

import flapjack.analysis.QTLTrackOptimiser;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class QTLPanel extends JPanel implements ActionListener
{
	private DataSet dataSet;

	private JLabel errorLabel;
	private JTable table;
	private QTLTableModel model;

	private NBQTLControlPanel controls;

//	private QTLTrackOptimiser optimiser;

	public QTLPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		errorLabel = new JLabel("<html>" + RB.getString("gui.traits.QTLPanel.errorMsg"));
		errorLabel.setForeground(Color.red);
		errorLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		table = new JTable();
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(Float.class, TraitsPanel.traitsRenderer);

		controls = new NBQTLControlPanel();
		controls.bImport.addActionListener(this);
		controls.bRemove.addActionListener(this);

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(errorLabel, BorderLayout.NORTH);
		add(new JScrollPane(table));
		add(controls, BorderLayout.SOUTH);

		updateModel();
	}

	public void updateModel()
	{
		model = new QTLTableModel(dataSet, table);

		if (SystemUtils.jreVersion() >= 1.6)
			new SortHandler();

		table.setModel(model);
		controls.statusLabel.setText(
			RB.format("gui.traits.QTLPanel.traitCount", table.getRowCount()));

		errorLabel.setVisible(model.qtlOffMap);

		// Messy...
		if (table.getColumnCount() > 0)
		{
			table.getColumnModel().getColumn(0).setCellRenderer(new QTLNameRenderer());
			table.getColumnModel().getColumn(5).setCellRenderer(new QTLTraitRenderer());
		}
	}

	// This is done in a separate class to hide its implementation from OS X on
	// Java5 that will throw ClassNotFoundExceptions if it tries to run it
	private class SortHandler
	{
		SortHandler()
		{
			table.setRowSorter(new TableRowSorter<QTLTableModel>(model));
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bImport)
			Flapjack.winMain.mFile.importQTLData();

		else if (e.getSource() == controls.bRemove)
		{
			String msg = RB.getString("gui.traits.QTLPanel.removeMsg");
			String[] options = new String[] {
					RB.getString("gui.traits.TraitsPanel.remove"),
					RB.getString("gui.text.cancel") };

			int response = TaskDialog.show(msg, TaskDialog.QST, 1, options);

			if (response == 0)
				removeAllTraits();
		}
	}

	private void removeAllTraits()
	{
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
			c.getTrackSet().removeAllElements();

		updateModel();
		Actions.projectModified();
	}

	// Renderer for the QTL name column of the table
	class QTLNameRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			QTL qtl = (QTL) table.getValueAt(row, 0);
			setText(qtl.getName());

			if (qtl.isAllowed())
				setIcon(null);
			else
				setIcon(Icons.getIcon("QTLDISABLED"));

			return this;
		}
	}

	// Renderer for the QTL trait column of the table
	class QTLTraitRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			QTL qtl = (QTL) table.getValueAt(row, 0);
			setText(qtl.getTrait());

			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) image.createGraphics();

			Color c = qtl.getDisplayColor();
			Color c1 = c.brighter();
			Color c2 = c.darker();
			g.setPaint(new GradientPaint(0, 0, c1, 20, 10, c2));

			g.fillRect(0, 0, 20, 10);
			g.setColor(Color.black);
			g.drawRect(0, 0, 20, 10);
			g.dispose();

			setIcon(new ImageIcon(image));

			return this;
		}

		public Insets getInsets(Insets i)
			{ return new Insets(0, 3, 0, 0); }
	}
}