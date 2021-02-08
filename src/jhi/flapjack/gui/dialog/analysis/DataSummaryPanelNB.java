// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import javax.swing.table.*;

import jhi.flapjack.analysis.AnalysisSet;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class DataSummaryPanelNB extends JPanel implements AdjustmentListener
{
	private NumberFormat nf = NumberFormat.getInstance();
	private DecimalFormat d = new DecimalFormat("0.0");

	public DataSummaryPanelNB(GTViewSet viewSet, AnalysisSet as, ArrayList<long[]> results, long alleleCount)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		tablePanel.setBackground((Color)UIManager.get("fjDialogBG"));

		String name = viewSet.getName();

		// i18n
		viewLabel.setText(RB.format("gui.dialog.analysis.NBAlleleStatisticsPanel.viewLabel", name, alleleCount));
		tablePanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.NBAlleleStatisticsPanel.tablePanel.title")));
		RB.setText(tableLabel1, "gui.dialog.analysis.NBAlleleStatisticsPanel.tableLabel1");
		RB.setText(tableLabel2, "gui.dialog.analysis.NBAlleleStatisticsPanel.tableLabel2");

		createSumTable(viewSet.getDataSet().getStateTable(), results);
		createViewTable(viewSet.getDataSet().getStateTable(), as, results);

		sp1.getVerticalScrollBar().addAdjustmentListener(this);
		sp2.getVerticalScrollBar().addAdjustmentListener(this);
	}

	private void createSumTable(StateTable stateTable, ArrayList<long[]> results)
	{
		// Results array
		Object[][] r = new Object[stateTable.size()][3];

		// Enter the data for the state column (column=0)
		for (int i = 0; i < r.length; i++)
			r[i][0] = stateTable.getAlleleState(i);

		// Calculate the sum of each row's allele count (the total number of
		// each allele type)
		long[] totals = new long[r.length];
		long alleleTotal = 0;

		// For each allele...
		for (int i = 0; i < r.length; i++)
		{
			// Count up each chromosome's number for that allele
			for (long[] data: results)
				totals[i] += data[i];

			// And track the overal number across all chromosomes
			alleleTotal += totals[i];
		}

		// Now populate the table with this data
		for (int i = 0; i < r.length; i++)
		{
			float prct = (((float)totals[i] / alleleTotal) * 100);
			r[i][1] = nf.format(totals[i]);
			r[i][2] = d.format(prct);
		}

		// Column headers
		String[] names = {
			RB.getString("gui.dialog.analysis.NBAlleleStatisticsPanel.sumTable.state"),
			RB.getString("gui.dialog.analysis.NBAlleleStatisticsPanel.sumTable.count"),
			"%" };

		sumTable.setModel(new DefaultTableModel(r, names) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
        		return false;
        }});

        sumTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        sumTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        sumTable.getColumnModel().getColumn(2).setPreferredWidth(35);

		DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
		cr.setHorizontalAlignment(JLabel.RIGHT);
        sumTable.setDefaultRenderer(Object.class, cr);
	}

	private void createViewTable(StateTable stateTable, AnalysisSet as, ArrayList<long[]> results)
	{
		// Column headers
		int viewCount = as.viewCount();
		String[] names = new String[viewCount*2];
		for (int i = 0, col=0; i < viewCount; i++, col+=2)
		{
			names[col] = as.getGTView(i).getChromosomeMap().getName();
			names[col+1] = "%";
		}

		Object[][] r = new Object[stateTable.size()][viewCount*2];

		// Enter the data for each chromosome
		for (int view = 0, col=0; view < viewCount; view++, col+=2)
		{
			long[] data = results.get(view);
			for (int i = 0; i < r.length; i++)
			{
				long value = data[i];
				float prct = (((float)data[i] / data[data.length-1]) * 100);
				r[i][col] = nf.format(value);
				r[i][col+1] = d.format(prct);
			}
		}


		viewTable.setModel(new DefaultTableModel(r, names) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
        		return false;
        }});

        for (int i = 0; i < viewCount*2; i++)
        	if (i % 2 == 0)
        		viewTable.getColumnModel().getColumn(i).setPreferredWidth(70);
        	else
        		viewTable.getColumnModel().getColumn(i).setPreferredWidth(35);

        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
		cr.setHorizontalAlignment(JLabel.RIGHT);
        viewTable.setDefaultRenderer(Object.class, cr);
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if (e.getSource() == sp1.getVerticalScrollBar())
		{
			int value = sp1.getVerticalScrollBar().getValue();
			sp2.getVerticalScrollBar().setValue(value);
		}
		else
		{
			int value = sp2.getVerticalScrollBar().getValue();
			sp1.getVerticalScrollBar().setValue(value);
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

        viewLabel = new javax.swing.JLabel();
        tablePanel = new javax.swing.JPanel();
        sp2 = new javax.swing.JScrollPane();
        viewTable = new javax.swing.JTable();
        tableLabel2 = new javax.swing.JLabel();
        sp1 = new javax.swing.JScrollPane();
        sumTable = new javax.swing.JTable();
        tableLabel1 = new javax.swing.JLabel();

        viewLabel.setText("Summary for view: <viewname>");

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Allele summary:"));

        sp2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        viewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {},
                {},
                {},
                {}
            },
            new String []
            {

            }
        ));
        viewTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        sp2.setViewportView(viewTable);

        tableLabel2.setLabelFor(viewTable);
        tableLabel2.setText("Breakdown by chromosome:");

        sp1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        sumTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {},
                {},
                {},
                {}
            },
            new String []
            {

            }
        ));
        sumTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        sp1.setViewportView(sumTable);

        tableLabel1.setLabelFor(sumTable);
        tableLabel1.setText("Totals:");

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableLabel1)
                    .addComponent(sp1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableLabel2)
                    .addComponent(sp2, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
                .addContainerGap())
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableLabel1)
                    .addComponent(tableLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp2, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(sp1, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(viewLabel)
                    .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane sp1;
    private javax.swing.JScrollPane sp2;
    private javax.swing.JTable sumTable;
    private javax.swing.JLabel tableLabel1;
    private javax.swing.JLabel tableLabel2;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JLabel viewLabel;
    private javax.swing.JTable viewTable;
    // End of variables declaration//GEN-END:variables
}