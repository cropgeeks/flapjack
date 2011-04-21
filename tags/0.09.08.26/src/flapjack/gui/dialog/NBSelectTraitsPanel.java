// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;

import scri.commons.gui.*;

class NBSelectTraitsPanel extends javax.swing.JPanel implements ActionListener
{
	private GTViewSet viewSet;

	public NBSelectTraitsPanel(GTViewSet viewSet)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));

		this.viewSet = viewSet;

		RB.setText(label, "gui.dialog.NBSelectTraitsPanel.label");
		RB.setText(selectAllLabel, "gui.dialog.NBSelectTraitsPanel.selectAllLabel");
		RB.setText(selectNoneLabel, "gui.dialog.NBSelectTraitsPanel.selectNoneLabel");

		createLinkLabels();
		createTable(viewSet);
	}

	private void createLinkLabels()
	{
		selectAllLabel.addActionListener(this);
		selectNoneLabel.addActionListener(this);
	}

	// Builds a simple table model that contains two columns - column 0 has the
	// name of each trait/phenotype, while column 1 contains whether or not that
	// trait has been selected for display
	private void createTable(GTViewSet viewSet)
	{
		String[] columnNames = {
			RB.getString("gui.dialog.NBSelectTraitsPanel.traitsColumn"),
			RB.getString("gui.dialog.NBSelectTraitsPanel.showColumn")
		};

		Vector<Trait> traits = viewSet.getDataSet().getTraits();

		int[] selected = viewSet.getTraits();
		Object[][] data = new Object[traits.size()][2];

		for (int i = 0; i < data.length; i++)
		{
			data[i][0] = traits.get(i).getName();

			// Search the current list of visible traits to see if this trait
			// is one of them. If it is, enable it in the table
			boolean show = false;
			for (int j = 0; j < selected.length; j++)
				if (selected[j] == i)
					show = true;

			data[i][1] = show;
		}

		table.setModel(new DefaultTableModel(data, columnNames)
		{
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			// Column 1 contains the tickboxes, and must be editable
			public boolean isCellEditable(int row, int col) {
				return col == 1;
			}
		});

		table.getColumnModel().getColumn(1).setPreferredWidth(10);
	}

	void isOK()
	{
		// Generate a boolean array with true/false states for every trait
		boolean[] array = new boolean[table.getRowCount()];
		for (int i = 0; i < array.length; i++)
			array[i] = (Boolean) table.getValueAt(i, 1);

		int active = 0;
		for (boolean b: array)
			if (b) active++;

		// Copy ONLY the selected traits into a new array
		int[] traits = new int[active];
		for (int i = 0, j = 0; i < array.length; i++)
			if (array[i])
				traits[j++] = i;

		// Assign the selected traits back to the view
		viewSet.setTraits(traits);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == selectAllLabel)
		{
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(true, i, 1);
		}

		if(e.getSource() == selectNoneLabel)
		{
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(false, i, 1);
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

        label = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        label2 = new javax.swing.JLabel();
        selectAllLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        selectNoneLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        label.setLabelFor(table);
        label.setText("The traits heatmap will display any of the following selected traits:");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        label2.setText("|");

        selectAllLabel.setText("Select all");

        selectNoneLabel.setText("Select none");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .add(label)
                    .add(layout.createSequentialGroup()
                        .add(selectAllLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(label2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(selectNoneLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(label2)
                    .add(selectAllLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(selectNoneLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
    private javax.swing.JLabel label2;
    private scri.commons.gui.matisse.HyperLinkLabel selectAllLabel;
    private scri.commons.gui.matisse.HyperLinkLabel selectNoneLabel;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}