package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

class NBFilterQTLsPanel extends javax.swing.JPanel
{
	private DataSet dataSet;

	// Stores all the traits and experiments (and their visibility states)
	private LinkedHashMap<String, Boolean> traits;
	private LinkedHashMap<String, Boolean> experiments;

	public NBFilterQTLsPanel(DataSet dataSet)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBFilterQTLsPanel.panel.title")));
		RB.setText(filterLabel, "gui.dialog.NBFilterQTLsPanel.filterLabel");
		RB.setText(traitsLabel, "gui.dialog.NBFilterQTLsPanel.traitsLabel");
		RB.setText(experimentsLabel, "gui.dialog.NBFilterQTLsPanel.experimentsLabel");
		RB.setText(selectAllTraits, "gui.dialog.NBFilterQTLsPanel.selectAll");
		RB.setText(selectNoTraits, "gui.dialog.NBFilterQTLsPanel.selectNone");
		RB.setText(selectAllExperiments, "gui.dialog.NBFilterQTLsPanel.selectAll");
		RB.setText(selectNoExperiments, "gui.dialog.NBFilterQTLsPanel.selectNone");

		this.dataSet = dataSet;

		initHashtables();
		createTraitsTable();
		createExperimentsTable();
		createLinkLabels();
	}

	private void initHashtables()
	{
		traits = new LinkedHashMap<String, Boolean>();
		experiments = new LinkedHashMap<String, Boolean>();

		// Scan every track in every chromosome
		for (ChromosomeMap cMap: dataSet.getChromosomeMaps())
			for (Vector<Feature> track: cMap.getTrackSet())
				for (Feature feature: track)
					if (feature instanceof QTL)
					{
						QTL qtl = (QTL) feature;

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
	}

	// Fill the traits table with data
	private void createTraitsTable()
	{
		String[] columnNames = { "Trait", "Visible" };
		Object[][] data = new Object[traits.size()][2];

		Iterator<String> itor = traits.keySet().iterator();

		for (int i = 0; itor.hasNext(); i++)
		{
			data[i][0] = itor.next();
			data[i][1] = new Boolean(traits.get(data[i][0]));
		}

		traitsTable.setModel(getModel(data, columnNames));
	}

	// Fill the experiments table with data
	private void createExperimentsTable()
	{
		String[] columnNames = { "Experiment", "Visible" };
		Object[][] data = new Object[experiments.size()][2];

		Iterator<String> itor = experiments.keySet().iterator();

		for (int i = 0; itor.hasNext(); i++)
		{
			data[i][0] = itor.next();
			data[i][1] = new Boolean(experiments.get(data[i][0]));
		}

		experimentsTable.setModel(getModel(data, columnNames));
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
		selectAllTraits.setCursor(FlapjackUtils.HAND_CURSOR);
		selectNoTraits.setCursor(FlapjackUtils.HAND_CURSOR);
		selectAllExperiments.setCursor(FlapjackUtils.HAND_CURSOR);
		selectNoExperiments.setCursor(FlapjackUtils.HAND_CURSOR);

		LinkMouseAdapter linkAdapter = new LinkMouseAdapter();
		selectAllTraits.addMouseListener(linkAdapter);
		selectNoTraits.addMouseListener(linkAdapter);
		selectAllExperiments.addMouseListener(linkAdapter);
		selectNoExperiments.addMouseListener(linkAdapter);
	}

	private class LinkMouseAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent event)
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
	}

	// Selects or de-selects every row of the given table
	private void tableSelect(JTable table, boolean state)
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
			for (Vector<Feature> track: cMap.getTrackSet())
				for (Feature feature: track)
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
        filterLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        traitsTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        experimentsTable = new javax.swing.JTable();
        traitsLabel = new javax.swing.JLabel();
        experimentsLabel = new javax.swing.JLabel();
        selectAllTraits = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        selectNoTraits = new javax.swing.JLabel();
        selectAllExperiments = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        selectNoExperiments = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter visible QTLs:"));

        filterLabel.setText("You can filter which QTLs are visible by using the options below.");

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

        selectAllTraits.setForeground(new java.awt.Color(0, 0, 255));
        selectAllTraits.setText("Select all");

        label2.setText("|");

        selectNoTraits.setForeground(java.awt.Color.blue);
        selectNoTraits.setText("Select none");

        selectAllExperiments.setForeground(new java.awt.Color(0, 0, 255));
        selectAllExperiments.setText("Select all");

        label3.setText("|");

        selectNoExperiments.setForeground(java.awt.Color.blue);
        selectNoExperiments.setText("Select none");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(filterLabel)
                    .add(panelLayout.createSequentialGroup()
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(traitsLabel)
                            .add(panelLayout.createSequentialGroup()
                                .add(selectAllTraits)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(label2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(selectNoTraits)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panelLayout.createSequentialGroup()
                                .add(selectAllExperiments)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(label3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(selectNoExperiments))
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(experimentsLabel))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelLayout.linkSize(new java.awt.Component[] {jScrollPane1, jScrollPane2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(filterLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelLayout.createSequentialGroup()
                        .add(traitsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(selectAllTraits)
                            .add(label2)
                            .add(selectNoTraits)))
                    .add(panelLayout.createSequentialGroup()
                        .add(experimentsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(selectAllExperiments)
                            .add(label3)
                            .add(selectNoExperiments))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
    private javax.swing.JLabel filterLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JPanel panel;
    private javax.swing.JLabel selectAllExperiments;
    private javax.swing.JLabel selectAllTraits;
    private javax.swing.JLabel selectNoExperiments;
    private javax.swing.JLabel selectNoTraits;
    private javax.swing.JLabel traitsLabel;
    private javax.swing.JTable traitsTable;
    // End of variables declaration//GEN-END:variables
}