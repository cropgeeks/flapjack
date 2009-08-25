package flapjack.gui.traits;

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

public class QTLPanel extends JPanel implements ActionListener, ChangeListener
{
	private DataSet dataSet;

	private JLabel errorLabel;
	private JTable table;
	private QTLTableModel model;

	private NBQTLControlPanel controls;

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
		controls.trackSpinner.addChangeListener(this);

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(errorLabel, BorderLayout.NORTH);
		add(new JScrollPane(table));
		add(controls, BorderLayout.SOUTH);

		updateModel(false);
	}

	public void updateModel(boolean optimize)
	{
		if (optimize)
			optimizeTrackUsage();

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

		// Set the spinner to the correct number of tracks for this dataset
		int size = dataSet.getMapByIndex(0).getTrackSet().size();
		controls.trackSpinner.setValue(size);
		controls.trackSpinner.setEnabled(size > 0);
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

		updateModel(false);
		Actions.projectModified();
	}

	public void stateChanged(ChangeEvent e)
	{
		int size = (Integer) controls.trackSpinner.getValue();

		// Redistribute the features across the *new* number of tracks - for
		// every chromosome in the data
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
			setTracks(size, c);
	}

	// Attempt to work out the optimum number of active tracks for a new set
	public void optimizeTrackUsage()
	{
		long s = System.currentTimeMillis();

		// Optimum number of tracks to use
		int optimum = 0;

		for (int i = 1; i <= 15; i++)
		{
			int trackCount = 0;

			// Scan each chromosome
			for (ChromosomeMap c: dataSet.getChromosomeMaps())
				if (setTracks(i, c))
					trackCount = i;

			// And update the best number if required
			if (trackCount > optimum)
				optimum = trackCount;
			// If a higher number wasn't found, then we've reached the optimum
			else
				break;
		}

		// Once we know the best number, reset to that number
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
			setTracks(optimum, c);

		System.out.println("Tracks optimised in " + (System.currentTimeMillis()-s + "ms"));
	}

	private boolean setTracks(int size, ChromosomeMap c)
	{
		Vector<Vector<Feature>> trackSet = c.getTrackSet();

		// 1: Move all features onto a temp track
		Vector<Feature> tmpTrack = new Vector<Feature>();
		for (Vector<Feature> track: trackSet)
		{
			Enumeration<Feature> features = track.elements();
			while (features.hasMoreElements())
				tmpTrack.add(features.nextElement());
		}

		// 2: Sort the features back into order
		Collections.sort(tmpTrack);

		// 3: Set up the correct number of new tracks
		trackSet.removeAllElements();
		for (int i = 0; i < size; i++)
			trackSet.add(new Vector<Feature>());

		// 4: Distribute the features across the tracks
		for (Feature f: tmpTrack)
		{
			boolean added = false;
			for (Vector<Feature> track: trackSet)
			{
				added = addToTrack(track, f);
				if (added)
					break;
			}

			if (added == false)
				trackSet.get(0).add(f);
		}

		// Return true if any QTLs are on the final track
		int count = trackSet.size();
		if (count > 0)
			return trackSet.get(count-1).size() > 0;
		else
			return false;
	}

	// Checks to see if a feature can be added to the end of this track without
	// clashing with an existing element
	private boolean addToTrack(Vector<Feature> track, Feature f)
	{
		if (track.size() == 0)
		{
			track.add(f);
			return true;
		}

		Feature prev = track.get(track.size()-1);
		if (f.getMin() > prev.getMax())
		{
			track.add(f);
			return true;
		}

		return false;
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