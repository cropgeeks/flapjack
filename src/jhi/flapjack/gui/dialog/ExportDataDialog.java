// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class ExportDataDialog extends JDialog implements ActionListener
{
	private GTViewSet viewSet;
	private String baseName;

	private ButtonGroup buttonGroup;

	public ExportDataDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportDataDialog.title"),
			true
		);

		this.viewSet = viewSet;
		baseName = viewSet.getDataSet().getName();
		baseName = baseName.substring(0, baseName.lastIndexOf(" "));

		initComponents();
		initComponents2();

		FlapjackUtils.initDialog(this, bExport, bCancel, true, getContentPane(), mapPanel);
	}

	private void initComponents2()
	{
		// Dialog panel buttons
		RB.setText(bExport, "gui.dialog.ExportDataDialog.bExport");
		bExport.addActionListener(this);
		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "export_data.html");

		// Components of the rest of the dialog
		mapPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBExportDataPanel.panel.title")));
		RB.setText(label, "gui.dialog.NBExportDataPanel.label");
		RB.setText(radioLabel, "gui.dialog.NBExportDataPanel.radioLabel");
		RB.setText(rAll, "gui.dialog.NBExportDataPanel.rAll");
		RB.setText(rSelected, "gui.dialog.NBExportDataPanel.rSelected");
		RB.setText(tableLabel, "gui.dialog.NBExportDataPanel.tableLabel");
		RB.setText(selectAllLabel, "gui.dialog.NBExportDataPanel.selectAllLabel");
		RB.setText(selectNoneLabel, "gui.dialog.NBExportDataPanel.selectNoneLabel");

		buttonGroup = new ButtonGroup();
		buttonGroup.add(rAll);
		buttonGroup.add(rSelected);

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

			data[i][0] = true;
			data[i][1] = view.getChromosomeMap().getName();
			data[i][2] = view.countSelectedMarkers() + " / "
				+ view.countGenuineMarkers();
			data[i][3] = view.countSelectedLines() + " / "
				+ view.lineCount();

			if (view.getChromosomeMap().isSpecialChromosome())
				data[i][0] = false;
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
	}


	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == bExport)
		{
			if (combo.getSelectedIndex() == 0)
				exportMap();
			else
				exportDat();
		}

		else if(e.getSource() == selectAllLabel)
		{
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(true, i, 0);
		}

		else if(e.getSource() == selectNoneLabel)
		{
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(false, i, 0);
		}
	}

	// Counts how many markers will be exported
	private int getMarkerCount(boolean[] chrm)
	{
		boolean allMarkers = rAll.isSelected();

		int count = 0;
		for (int i = 0; i < viewSet.chromosomeCount(); i++)
		{
			if (chrm[i] == false)
				continue;

			GTView view = viewSet.getView(i);

			if (allMarkers)
				count += view.markerCount();
			else
				count += view.countSelectedMarkers();
		}

		return count;
	}

	// Counts how many lines will be exported
	private int getLineCount()
	{
		boolean allLines = rAll.isSelected();

		if (allLines)
			return viewSet.getView(0).lineCount();
		else
			return viewSet.getView(0).countSelectedLines();
	}

	// Export the map to disk
	private void exportMap()
	{
		boolean useAll = rAll.isSelected();
		boolean[] chrm = getSelectedChromosomes();

		int count = getMarkerCount(chrm);

		String name = baseName + "_" + count + ".map";
		File filename = promptForFilename(new File(name), "map");

		if (filename != null)
		{
			ChromosomeMapExporter exporter
				= new ChromosomeMapExporter(filename, viewSet, useAll, chrm, count);

			displayDialog(exporter, filename);
		}
	}

	// Export the genotype data to disk
	private void exportDat()
	{
		boolean useAll = rAll.isSelected();
		boolean[] chrm = getSelectedChromosomes();

		int mrkrCount = getMarkerCount(chrm);
		int lineCount = getLineCount();

		String name = baseName + "_" + viewSet.getName() + "_"
			+ lineCount + "x" + mrkrCount + ".dat";
		File filename = promptForFilename(new File(name), "dat");

		if (filename != null)
		{
			GenotypeDataExporter exporter
				= new GenotypeDataExporter(filename, viewSet, useAll, chrm, lineCount);

			displayDialog(exporter, filename);
		}
	}

	private void displayDialog(ITrackableJob exporter, File filename)
	{
		ProgressDialog dialog = new ProgressDialog(exporter,
			 RB.format("gui.dialog.ExportDataDialog.exportTitle"),
			 RB.format("gui.dialog.ExportDataDialog.exportLabel"),
			 Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;

		TaskDialog.info(
			RB.format("gui.dialog.ExportDataDialog.exportSuccess", filename),
			RB.getString("gui.text.close"));
	}

	private File promptForFilename(File name, String extension)
	{
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters." + extension), extension);

		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.dialog.ExportDataDialog.saveDialog"), name, filter);

		if (filename != null)
			return new File(filename);
		else
			return null;
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

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bExport = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bExport.setText("Export...");
        dialogPanel1.add(bExport);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        mapPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Export options:"));

        radioLabel.setText("Include information for the following markers and lines:");

        rAll.setSelected(true);
        rAll.setText("All markers and lines");

        rSelected.setText("Only markers and lines I have selected");

        label.setText("Export file type:");

        tableLabel.setText("Only include data from the following selected chromosomes:");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bExport;
    private javax.swing.JButton bHelp;
    javax.swing.JComboBox<String> combo;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
    private javax.swing.JLabel label2;
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