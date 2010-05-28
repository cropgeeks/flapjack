// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

class NBFilterQTLsPanel extends JPanel implements ActionListener
{
	private GenotypePanel gPanel;
	private DataSet dataSet;

	// Stores all the traits and experiments (and their visibility states)
	private LinkedHashMap<String, Boolean> traits;
	private LinkedHashMap<String, Boolean> experiments;

	public NBFilterQTLsPanel(GenotypePanel gPanel, DataSet dataSet)
	{
		this.gPanel = gPanel;

		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBFilterQTLsPanel.panel.title")));
		RB.setText(traitsLabel, "gui.dialog.NBFilterQTLsPanel.traitsLabel");
		RB.setText(experimentsLabel, "gui.dialog.NBFilterQTLsPanel.experimentsLabel");
		RB.setText(selectAllTraits, "gui.dialog.NBFilterQTLsPanel.selectAll");
		RB.setText(selectNoTraits, "gui.dialog.NBFilterQTLsPanel.selectNone");
		RB.setText(selectAllExperiments, "gui.dialog.NBFilterQTLsPanel.selectAll");
		RB.setText(selectNoExperiments, "gui.dialog.NBFilterQTLsPanel.selectNone");

		this.dataSet = dataSet;

		updateTables();
		createLinkLabels();
	}

	private void updateTables()
	{
		initHashtables();
		createTraitsTable();
		createExperimentsTable();
	}

	private void initHashtables()
	{
		traits = new LinkedHashMap<String, Boolean>();
		experiments = new LinkedHashMap<String, Boolean>();

		// Track how many QTLs are currently visible
		int count = 0;

		// Scan every track in every chromosome
		for (ChromosomeMap cMap: dataSet.getChromosomeMaps())
			for (Feature feature: cMap.getFeatures())
				if (feature instanceof QTL)
				{
					QTL qtl = (QTL) feature;
					count += qtl.isVisible() ? 1 : 0;

					Boolean tValue = traits.get(qtl.getTrait());

					// Either add the trait information...
					if (tValue == null)
						traits.put(qtl.getTrait(), qtl.isVisible());
					// Or update it to ensure that if *any* QTL is visible
					// with this trait, then it must be enabled
					else if (qtl.isVisible())
						traits.put(qtl.getTrait(), true);

					// And the same for experiments
					Boolean eValue = experiments.get(qtl.getExperiment());

					if (eValue == null)
						experiments.put(qtl.getExperiment(), qtl.isVisible());
					else if (qtl.isVisible())
						experiments.put(qtl.getExperiment(), true);
				}

		numberLabel.setText(RB.format(
			"gui.dialog.NBFilterQTLsPanel.numberLabel", count));
	}

	// Fill the traits table with data
	private void createTraitsTable()
	{
		String[] columnNames = {
			RB.getString("gui.dialog.NBFilterQTLsPanel.trait"), "" };

		Object[][] data = new Object[traits.size()][2];

		Iterator<String> itor = traits.keySet().iterator();

		for (int i = 0; itor.hasNext(); i++)
		{
			data[i][0] = itor.next();
			data[i][1] = new Boolean(traits.get(data[i][0]));
		}

		traitsTable.setModel(getModel(data, columnNames));
		traitsTable.getColumnModel().getColumn(1).setMaxWidth(35);
	}

	// Fill the experiments table with data
	private void createExperimentsTable()
	{
		String[] columnNames = {
			RB.getString("gui.dialog.NBFilterQTLsPanel.experiment"), "" };

		Object[][] data = new Object[experiments.size()][2];

		Iterator<String> itor = experiments.keySet().iterator();

		for (int i = 0; itor.hasNext(); i++)
		{
			data[i][0] = itor.next();
			data[i][1] = new Boolean(experiments.get(data[i][0]));
		}

		experimentsTable.setModel(getModel(data, columnNames));
		experimentsTable.getColumnModel().getColumn(1).setMaxWidth(35);
	}

	// Builds a table model for the two methods above
	private DefaultTableModel getModel(Object[][] data, String[] columnNames)
	{
		return new DefaultTableModel(data, columnNames)
		{
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return col == 1;
			}
		};
	}

	// Adds a listener to each select label to catch a mouse click on it
	private void createLinkLabels()
	{
		selectAllTraits.addActionListener(this);
		selectNoTraits.addActionListener(this);
		selectAllExperiments.addActionListener(this);
		selectNoExperiments.addActionListener(this);
	}

	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == selectAllTraits)
			tableSelect(traitsTable, true);
		else if (event.getSource() == selectNoTraits)
			tableSelect(traitsTable, false);
		else if (event.getSource() == selectAllExperiments)
			tableSelect(experimentsTable, true);
		else if (event.getSource() == selectNoExperiments)
			tableSelect(experimentsTable, false);
	}

	// Selects or de-selects every row of the given table
	void tableSelect(JTable table, boolean state)
	{
		for (int i = 0; i < table.getRowCount(); i++)
			table.setValueAt(state, i, 1);
	}

	// Runs the actual filter operation
	void filterQTLs()
	{
		// Update the hashtables with the latest values from the UI tables
		traits.clear();
		experiments.clear();

		for (int i = 0; i < traitsTable.getRowCount(); i++)
		{
			String tName = (String) traitsTable.getValueAt(i, 0);
			boolean tValue = (Boolean) traitsTable.getValueAt(i, 1);

			traits.put(tName, tValue);
		}

		for (int i = 0; i < experimentsTable.getRowCount(); i++)
		{
			String eName = (String) experimentsTable.getValueAt(i, 0);
			boolean eValue = (Boolean) experimentsTable.getValueAt(i, 1);

			experiments.put(eName, eValue);
		}

		// Scan over all the chromosomes/tracks and update the QTLs
		for (ChromosomeMap cMap: dataSet.getChromosomeMaps())
			for (Feature feature: cMap.getFeatures())
				if (feature instanceof QTL && feature.isAllowed())
				{
					QTL qtl = (QTL) feature;

					// Make the QTL visible only if both its trait and
					// exeperiment values have been set to true
					if (traits.get(qtl.getTrait()) && experiments.get(qtl.getExperiment()))
						qtl.setVisible(true);
					else
						qtl.setVisible(false);
				}


		// Update the display after the filter has run
//		Flapjack.winMain.repaint();
		gPanel.refreshView();
		updateTables();

		Actions.projectModified();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        traitsTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        experimentsTable = new javax.swing.JTable();
        traitsLabel = new javax.swing.JLabel();
        experimentsLabel = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        numberLabel = new javax.swing.JLabel();
        selectAllTraits = new scri.commons.gui.matisse.HyperLinkLabel();
        selectNoTraits = new scri.commons.gui.matisse.HyperLinkLabel();
        selectAllExperiments = new scri.commons.gui.matisse.HyperLinkLabel();
        selectNoExperiments = new scri.commons.gui.matisse.HyperLinkLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter visible QTLs:"));

        traitsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        traitsTable.setRowSelectionAllowed(false);
        traitsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(traitsTable);

        experimentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        experimentsTable.setRowSelectionAllowed(false);
        experimentsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(experimentsTable);

        traitsLabel.setLabelFor(traitsTable);
        traitsLabel.setText("Only show these traits:");

        experimentsLabel.setLabelFor(experimentsTable);
        experimentsLabel.setText("Only show these experiments:");

        label2.setText("|");

        label3.setText("|");

        numberLabel.setText("Number of QTLs currently visible (across all chromosomes):");

        selectAllTraits.setText("Select all");

        selectNoTraits.setText("Select none");

        selectAllExperiments.setText("Select all");

        selectNoExperiments.setText("Select none");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelLayout.createSequentialGroup()
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(traitsLabel)
                            .add(panelLayout.createSequentialGroup()
                                .add(selectAllTraits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(label2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(selectNoTraits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 198, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panelLayout.createSequentialGroup()
                                .add(selectAllExperiments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(label3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(selectNoExperiments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(experimentsLabel)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 192, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(numberLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelLayout.linkSize(new java.awt.Component[] {jScrollPane1, jScrollPane2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelLayout.createSequentialGroup()
                        .add(traitsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(label2)
                            .add(selectAllTraits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(selectNoTraits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(panelLayout.createSequentialGroup()
                        .add(experimentsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(label3)
                            .add(selectAllExperiments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(selectNoExperiments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(18, 18, 18)
                .add(numberLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel experimentsLabel;
    private javax.swing.JTable experimentsTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel numberLabel;
    private javax.swing.JPanel panel;
    private scri.commons.gui.matisse.HyperLinkLabel selectAllExperiments;
    private scri.commons.gui.matisse.HyperLinkLabel selectAllTraits;
    private scri.commons.gui.matisse.HyperLinkLabel selectNoExperiments;
    private scri.commons.gui.matisse.HyperLinkLabel selectNoTraits;
    private javax.swing.JLabel traitsLabel;
    private javax.swing.JTable traitsTable;
    // End of variables declaration//GEN-END:variables
}