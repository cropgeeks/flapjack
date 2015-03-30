// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class ExportDataPanelNB extends JPanel implements ActionListener
{
	public ExportDataPanelNB(GTViewSet viewSet)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		mapPanel.setBackground((Color)UIManager.get("fjDialogBG"));

		mapPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBExportDataPanel.panel.title")));
		RB.setText(label, "gui.dialog.NBExportDataPanel.label");
		RB.setText(radioLabel, "gui.dialog.NBExportDataPanel.radioLabel");
		RB.setText(rAll, "gui.dialog.NBExportDataPanel.rAll");
		RB.setText(rSelected, "gui.dialog.NBExportDataPanel.rSelected");
		RB.setText(tableLabel, "gui.dialog.NBExportDataPanel.tableLabel");
		RB.setText(selectAllLabel, "gui.dialog.NBExportDataPanel.selectAllLabel");
		RB.setText(selectNoneLabel, "gui.dialog.NBExportDataPanel.selectNoneLabel");

		combo.addItem(RB.getString("gui.dialog.NBExportDataPanel.comboMap"));
		combo.addItem(RB.getString("gui.dialog.NBExportDataPanel.comboDat"));

		selectAllLabel.addActionListener(this);
		selectNoneLabel.addActionListener(this);

		createTable(viewSet);
	}

	private void createTable(GTViewSet viewSet)
	{
		String[] columnNames = {
			RB.getString("gui.dialog.NBExportDataPanel.column1"),
			RB.getString("gui.dialog.NBExportDataPanel.column2"),
			RB.getString("gui.dialog.NBExportDataPanel.column3"),
			RB.getString("gui.dialog.NBExportDataPanel.column4")
		};

		// Fill the data array with the string values from the list
		Object[][] data = new Object[viewSet.chromosomeCount()][4];

		for (int i = 0; i < viewSet.chromosomeCount(); i++)
		{
			GTView view = viewSet.getView(i);

			data[i][0] = new Boolean(true);
			data[i][1] = view.getChromosomeMap().getName();
			data[i][2] = view.countSelectedMarkers() + " / "
				+ view.countGenuineMarkers();
			data[i][3] = view.countSelectedLines() + " / "
				+ view.lineCount();

			if (view.getChromosomeMap().isSpecialChromosome())
				data[i][0] = new Boolean(false);
		}

		table.setModel(new DefaultTableModel(data, columnNames)
		{
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return col == 0;
			}
		});

		DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
		cr.setHorizontalAlignment(JLabel.CENTER);

		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.setDefaultRenderer(String.class, cr);
		UIScaler.setCellHeight(table);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == selectAllLabel)
		{
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(true, i, 0);
		}

		if(e.getSource() == selectNoneLabel)
		{
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(false, i, 0);
		}
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used to export data from
	boolean[] getSelectedChromosomes()
	{
		boolean[] array = new boolean[table.getRowCount()];

		for (int i = 0; i < array.length; i++)
			array[i] = (Boolean) table.getValueAt(i, 0);

		return array;
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapGroup = new javax.swing.ButtonGroup();
        datGroup = new javax.swing.ButtonGroup();
        mapPanel = new javax.swing.JPanel();
        radioLabel = new javax.swing.JLabel();
        rAll = new javax.swing.JRadioButton();
        rSelected = new javax.swing.JRadioButton();
        label = new javax.swing.JLabel();
        combo = new javax.swing.JComboBox<String>();
        tableLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        selectAllLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        label2 = new javax.swing.JLabel();
        selectNoneLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        mapPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Export options:"));

        radioLabel.setText("Include information for the following markers and lines:");

        mapGroup.add(rAll);
        rAll.setSelected(true);
        rAll.setText("All markers and lines");

        mapGroup.add(rSelected);
        rSelected.setText("Only markers and lines I have selected");

        label.setLabelFor(combo);
        label.setText("Export file type:");

        tableLabel.setText("Only include data from the following selected chromosomes:");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        selectAllLabel.setText("Select all");

        label2.setText("|");

        selectNoneLabel.setText("Select none");

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mapPanelLayout.createSequentialGroup()
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(combo, 0, 319, Short.MAX_VALUE))
                    .addComponent(rAll)
                    .addComponent(radioLabel)
                    .addComponent(rSelected)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .addGroup(mapPanelLayout.createSequentialGroup()
                        .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tableLabel))
                .addContainerGap())
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label)
                    .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(radioLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rSelected)
                .addGap(18, 18, 18)
                .addComponent(tableLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label2)
                    .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox<String> combo;
    private javax.swing.ButtonGroup datGroup;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
    private javax.swing.JLabel label2;
    private javax.swing.ButtonGroup mapGroup;
    private javax.swing.JPanel mapPanel;
    javax.swing.JRadioButton rAll;
    private javax.swing.JRadioButton rSelected;
    private javax.swing.JLabel radioLabel;
    private scri.commons.gui.matisse.HyperLinkLabel selectAllLabel;
    private scri.commons.gui.matisse.HyperLinkLabel selectNoneLabel;
    private javax.swing.JTable table;
    private javax.swing.JLabel tableLabel;
    // End of variables declaration//GEN-END:variables

}