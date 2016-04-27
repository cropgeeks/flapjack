package jhi.flapjack.gui.dialog.analysis;

import java.awt.Color;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class ChromosomeSelectionPanel extends JPanel implements ActionListener
{
	private ChromosomeSelectionTableModel model;

    /** Creates new form ChromosomeSelectionPanel */
    public ChromosomeSelectionPanel()
	{
        initComponents();

		setBackground(Color.WHITE);

		RB.setText(lblMarkersFromChr, "gui.dialog.analysis.ChromosomeSelectionPanel.lblMarkersFromChr");

		RB.setText(selectAllLabel, "gui.dialog.analysis.ChromosomeSelectionPanel.selectAll");
		RB.setText(selectNoneLabel, "gui.dialog.analysis.ChromosomeSelectionPanel.selectNone");

		selectAllLabel.addActionListener(this);
		selectNoneLabel.addActionListener(this);
    }

	public void setupComponents(GTViewSet viewSet)
	{
		// Set up analysis objects to get counts of all and selected lines
		AnalysisSet allLines = new AnalysisSet(viewSet);
		allLines.withAllLines();

		AnalysisSet selectedLines = new AnalysisSet(viewSet);
		selectedLines.withSelectedLines();

		lblSelectedLines.setText(RB.format("gui.dialog.analysis.ChromosomeSelectionPanel.lblSelectedLines", selectedLines.lineCount(), allLines.lineCount()));

		// Setup analysis objects from which we'll get our counts of selected and all markers for each chromosome, as
		// well as the chromosome names
		AnalysisSet allMarkers = new AnalysisSet(viewSet);
		allMarkers.withViews(null);
		allMarkers.withAllMarkers();

		AnalysisSet selectedMarkers = new AnalysisSet(viewSet);
		selectedMarkers.withViews(null);
		selectedMarkers.withSelectedMarkers();

		ArrayList<String> markerSelectionValues = new ArrayList<>();
		ArrayList<String> chromosomeNames = new ArrayList<>();
		for (int i=0; i < allMarkers.viewCount(); i++)
		{
			markerSelectionValues.add(selectedMarkers.markerCount(i) + "/" + allMarkers.markerCount(i));
			chromosomeNames.add(viewSet.getView(allMarkers.chrMapIndex(i)).getChromosomeMap().getName());
		}

		// Setup the table model and make the first column of the table small enough for a checkbox
		model = new ChromosomeSelectionTableModel(chromosomeNames, markerSelectionValues);
		chromosomesTable.setModel(model);
		chromosomesTable.getColumnModel().getColumn(0).setPreferredWidth(20);

		UIScaler.setCellHeight(chromosomesTable);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == selectAllLabel)
			for (int i = 0; i < model.getRowCount(); i++)
				model.setValueAt(true, i, 0);

		else if (e.getSource() == selectNoneLabel)
			for (int i = 0; i < model.getRowCount(); i++)
				model.setValueAt(false, i, 0);
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used in the sort
	public boolean[] getSelectedChromosomes()
	{
		boolean[] array = new boolean[model.getRowCount()];

		for (int i = 0; i < array.length; i++)
			array[i] = (Boolean) model.getValueAt(i, 0);

		return array;
	}

	public class ChromosomeSelectionTableModel extends AbstractTableModel
	{
		private final String[] columnNames;
		private final boolean[] selected;

		private final int rowCount;

		private final ArrayList<String> chromosomeNames;
		private final ArrayList<String> markerSelectionValues;

		ChromosomeSelectionTableModel(ArrayList<String> chromosomeNames, ArrayList<String> markerSelectionValues)
		{
			this.chromosomeNames = chromosomeNames;
			this.markerSelectionValues = markerSelectionValues;
			this.rowCount = chromosomeNames.size();

			columnNames = new String[] {
				RB.getString("gui.dialog.analysis.ChromosomeSelectionPanel.column1"),
				RB.getString("gui.dialog.analysis.ChromosomeSelectionPanel.column2"),
				RB.getString("gui.dialog.analysis.ChromosomeSelectionPanel.column3")
			};

			selected = new boolean[rowCount];
			for (int i=0; i < selected.length; i++)
				selected[i] = true;
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
				case 1: return chromosomeNames.get(rowIndex);
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        lblSelectedLines = new javax.swing.JLabel();
        lblMarkersFromChr = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chromosomesTable = new javax.swing.JTable();
        selectAllLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        lblPipe = new javax.swing.JLabel();
        selectNoneLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        lblSelectedLines.setText("Only the currently selected lines will be used: 0 / 0");

        lblMarkersFromChr.setText("Only markers from the following selected chromosomes will be used:");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMarkersFromChr)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPipe)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblSelectedLines))
                        .addGap(0, 55, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSelectedLines)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMarkersFromChr, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPipe)
                    .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable chromosomesTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMarkersFromChr;
    private javax.swing.JLabel lblPipe;
    private javax.swing.JLabel lblSelectedLines;
    private scri.commons.gui.matisse.HyperLinkLabel selectAllLabel;
    private scri.commons.gui.matisse.HyperLinkLabel selectNoneLabel;
    // End of variables declaration//GEN-END:variables

}
