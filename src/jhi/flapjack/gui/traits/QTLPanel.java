// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.traits;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class QTLPanel extends JPanel implements ActionListener
{
	private DataSet dataSet;

	private JTable table;
	private QTLTableModel model;

	private QTLPanelNB controls;

//	private QTLTrackOptimiser optimiser;

	public QTLPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		controls = new QTLPanelNB(dataSet);
		controls.bImport.addActionListener(this);
		controls.bExport.addActionListener(this);
		controls.bRemove.addActionListener(this);
		controls.bFilter.addActionListener(this);

		table = controls.table;
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(String.class, new QTLTableModel.NumStrRenderer());
		table.setDefaultRenderer(Float.class, new QTLTableModel.NumStrRenderer());

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel();
	}

	public void updateModel()
	{
		model = new QTLTableModel(dataSet);

		if (SystemUtils.jreVersion() >= 1.6)
			new SortHandler();

		table.setModel(model);
		controls.statusLabel.setText(
			RB.format("gui.traits.QTLPanel.traitCount", table.getRowCount()));

		controls.errorLabel.setVisible(model.qtlOffMap);

		// Messy...
		if (table.getColumnCount() > 0)
		{
			table.getColumnModel().getColumn(0).setCellRenderer(new QTLNameRenderer());
//			table.getColumnModel().getColumn(5).setCellRenderer(new QTLTraitRenderer());
		}

		// Enable/disable the buttons based on the trait count
		controls.bExport.setEnabled(table.getColumnCount()-1 > 0);
		controls.bFilter.setEnabled(table.getColumnCount()-1 > 0);
		controls.bRemove.setEnabled(table.getColumnCount()-1 > 0);
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
			Flapjack.winMain.mFile.fileImport(2);

		else if (e.getSource() == controls.bExport)
			Flapjack.winMain.mData.dataExportQTLs();

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

		else if (e.getSource() == controls.bFilter)
			Flapjack.winMain.mData.dataFilterQTLs();
	}

	private void removeAllTraits()
	{
		// Step 1: Remove the *actual* QTLs from the chromosomes
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
		{
			c.getQtls().clear();
			c.getQtls().trimToSize();
		}

		// Step 2: Remove the info wrappers from the views
		for (GTViewSet viewSet: dataSet.getViewSets())
		{
			for (GTView view: viewSet.getViews())
			{
				view.setQTLs(new ArrayList<QTLInfo>());
			}
		}

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
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

			if (qtl.isAllowed())
			{
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
			}
			else
				setIcon(Icons.getIcon("QTLDISABLED"));

			return this;
		}
	}

	// Renderer for the QTL trait column of the table
/*	class QTLNameRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			QTL qtl = (QTL) table.getValueAt(row, 0);
			setText(qtl.getName());

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
	*/
}