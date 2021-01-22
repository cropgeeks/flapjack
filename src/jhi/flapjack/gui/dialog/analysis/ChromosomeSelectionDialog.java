// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class ChromosomeSelectionDialog extends JDialog implements ActionListener
{
	private ChromosomeSelectionTableModel model;
	private boolean hideAllChrs;

	public ChromosomeSelectionDialog(GTViewSet viewSet, boolean hideAllChrs, boolean display)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.ChromosomeSelectionDialog.title"),
			display
		);

		this.hideAllChrs = hideAllChrs;

		initComponents();
		initComponents2(viewSet);

		FlapjackUtils.initDialog(this, bClose, bClose, false, getContentPane());
	}

	private void initComponents2(GTViewSet viewSet)
	{
		bClose.addActionListener(this);
		selectAllLabel.addActionListener(this);
		selectNoneLabel.addActionListener(this);

		// Set up analysis objects to get counts of all and selected lines
		AnalysisSet allLines = new AnalysisSet(viewSet);
		allLines.withAllLines();

		AnalysisSet selectedLines = new AnalysisSet(viewSet);
		selectedLines.withSelectedLines();

		lblSelectedLines.setText(RB.format("gui.dialog.analysis.ChromosomeSelectionDialog.lblSelectedLines", selectedLines.lineCount(), allLines.lineCount()));

		RB.setText(lblMarkersFromChr, "gui.dialog.analysis.ChromosomeSelectionDialog.lblMarkersFromChr");

		RB.setText(selectAllLabel, "gui.dialog.analysis.ChromosomeSelectionDialog.selectAll");
		RB.setText(selectNoneLabel, "gui.dialog.analysis.ChromosomeSelectionDialog.selectNone");

		// Setup analysis objects from which we'll get our counts of selected and all markers for each chromosome, as
		// well as the chromosome names
		AnalysisSet allMarkers = new AnalysisSet(viewSet);
		allMarkers.withViews(null);
		allMarkers.withAllMarkers();

		AnalysisSet selectedMarkers = new AnalysisSet(viewSet);
		selectedMarkers.withViews(null);
		selectedMarkers.withSelectedMarkers();

		ArrayList<String> markerSelectionValues = new ArrayList<>();
		ArrayList<ChromosomeMap> maps = new ArrayList<>();
		for (int i=0; i < allMarkers.viewCount(); i++)
		{
			markerSelectionValues.add(selectedMarkers.markerCount(i) + "/" + allMarkers.markerCount(i));
			maps.add(allMarkers.getGTView(i).getChromosomeMap());
		}

		// Setup the table model and make the first column of the table small enough for a checkbox
		model = new ChromosomeSelectionTableModel(maps, markerSelectionValues);
		chromosomesTable.setModel(model);
		chromosomesTable.getColumnModel().getColumn(0).setPreferredWidth(20);
	}

	public void hideLineSummary()
	{
		lblSelectedLines.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == selectAllLabel)
		{
			for (int i = 0; i < model.getRowCount(); i++)
			{
				if (model.isCellEditable(i, 0))
					model.setValueAt(true, i, 0);
			}
		}
		else if (e.getSource() == selectNoneLabel)
			for (int i = 0; i < model.getRowCount(); i++)
				model.setValueAt(false, i, 0);
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used in the sort
	public boolean[] getSelectedChromosomes()
	{
		model.updateForFinalSelection();

		boolean[] array = new boolean[model.getRowCount()];

		for (int i = 0; i < array.length; i++)
			array[i] = (Boolean) model.getValueAt(i, 0);

		return array;
	}

	private class ChromosomeSelectionTableModel extends AbstractTableModel
	{
		private final String[] columnNames;
		private final boolean[] selected;

		private int rowCount;
		private boolean rowCountModified = false;

		private final ArrayList<ChromosomeMap> maps;
		private final ArrayList<String> markerSelectionValues;

		ChromosomeSelectionTableModel(ArrayList<ChromosomeMap> maps, ArrayList<String> markerSelectionValues)
		{
			this.maps = maps;
			this.markerSelectionValues = markerSelectionValues;
			this.rowCount = maps.size();

			columnNames = new String[] {
				RB.getString("gui.dialog.analysis.ChromosomeSelectionPanel.column1"),
				RB.getString("gui.dialog.analysis.ChromosomeSelectionPanel.column2"),
				RB.getString("gui.dialog.analysis.ChromosomeSelectionPanel.column3")
			};

			selected = new boolean[rowCount];
			for (int i=0; i < selected.length; i++)
				if (markerSelectionValues.get(i).startsWith("0") == false)
					selected[i] = true;

			// Fudge to hide any 'all chromsomes' view from the table, and also
			// to ensure it's pre-deselected for later
			if (hideAllChrs && maps.get(rowCount-1).isSpecialChromosome())
			{
				rowCountModified = true;
				selected[rowCount-1] = false;
				rowCount--;
			}
		}

		void updateForFinalSelection()
		{
			if (rowCountModified)
				rowCount++;
		}

		@Override
		public int getRowCount()
		{
			return rowCount;
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public String getColumnName(int columnIndex)
		{
			return columnNames[columnIndex];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			switch (columnIndex)
			{
				case 0: return selected[rowIndex];
				case 1: return maps.get(rowIndex);
				case 2: return markerSelectionValues.get(rowIndex);

				default: return null;
			}
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
				selected[rowIndex] = (boolean) value;

			fireTableCellUpdated(rowIndex, columnIndex);
		}

		@Override
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			return col == 0;
		}
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
        bClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblSelectedLines = new javax.swing.JLabel();
        lblMarkersFromChr = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chromosomesTable = new javax.swing.JTable();
        selectAllLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        lblPipe = new javax.swing.JLabel();
        selectNoneLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bClose.setText("Close");
        dialogPanel1.add(bClose);

        jLabel1.setText("<html>Flapjack tasks only run on any selected lines and markers, however you can use the<br>settings here to exclude certain chromosomes if need be.");

        lblSelectedLines.setText("Number of lines currently selected in this view: {0} / {1}");

        lblMarkersFromChr.setText("Only use the selected markers from the following chromosomes:");

        chromosomesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {

            }
        ));
        jScrollPane1.setViewportView(chromosomesTable);

        selectAllLabel.setText("Select all");

        lblPipe.setText("|");

        selectNoneLabel.setText("Select none");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSelectedLines)
                            .addComponent(lblMarkersFromChr)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPipe)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSelectedLines)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMarkersFromChr, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPipe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bClose;
    private javax.swing.JTable chromosomesTable;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMarkersFromChr;
    private javax.swing.JLabel lblPipe;
    private javax.swing.JLabel lblSelectedLines;
    private scri.commons.gui.matisse.HyperLinkLabel selectAllLabel;
    private scri.commons.gui.matisse.HyperLinkLabel selectNoneLabel;
    // End of variables declaration//GEN-END:variables
}