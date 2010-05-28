// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class NBAlleleStatisticsPanel extends JPanel implements AdjustmentListener
{
	private NumberFormat nf = NumberFormat.getInstance();
	private DecimalFormat d = new DecimalFormat("0.0");

	public NBAlleleStatisticsPanel(GTViewSet viewSet, ArrayList<int[]> results)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		tablePanel.setBackground((Color)UIManager.get("fjDialogBG"));

		String name = viewSet.getName();
		int total = viewSet.countAllAlleles();

		// i18n
		viewLabel.setText(RB.format("gui.dialog.analysis.NBAlleleStatisticsPanel.viewLabel", name, total));
		tablePanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.NBAlleleStatisticsPanel.tablePanel.title")));
		RB.setText(tableLabel1, "gui.dialog.analysis.NBAlleleStatisticsPanel.tableLabel1");
		RB.setText(tableLabel2, "gui.dialog.analysis.NBAlleleStatisticsPanel.tableLabel2");

		createSumTable(viewSet, results);
		createViewTable(viewSet, results);

		sp1.getVerticalScrollBar().addAdjustmentListener(this);
		sp2.getVerticalScrollBar().addAdjustmentListener(this);
	}

	private void createSumTable(GTViewSet viewSet, ArrayList<int[]> results)
	{
		StateTable stateTable = viewSet.getDataSet().getStateTable();

		// Results array
		Object[][] r = new Object[stateTable.size()][3];

		// Enter the data for the state column (column=0)
		for (int i = 0; i < r.length; i++)
			r[i][0] = stateTable.getAlleleState(i);

		// Calculate the sum of each row's allele count (the total number of
		// each allele type)
		int[] totals = new int[r.length];
		int alleleTotal = 0;

		// For each allele...
		for (int i = 0; i < r.length; i++)
		{
			// Count up each chromosome's number for that allele
			for (int[] data: results)
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

        sumTable.setDefaultRenderer(Object.class, new StatisticsRenderer());
	}

	private void createViewTable(GTViewSet viewSet, ArrayList<int[]> results)
	{
		StateTable stateTable = viewSet.getDataSet().getStateTable();

		// Column headers
		int viewCount = viewSet.getViews().size();
		String[] names = new String[viewCount*2];
		for (int i = 0, col=0; i < viewCount; i++, col+=2)
		{
			names[col] = viewSet.getView(i).getChromosomeMap().getName();
			names[col+1] = "%";
		}

		Object[][] r = new Object[stateTable.size()][viewCount*2];

		// Enter the data for each chromosome
		for (int view = 0, col=0; view < viewCount; view++, col+=2)
		{
			int[] data = results.get(view);
			for (int i = 0; i < r.length; i++)
			{
				int value = data[i];
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

        viewTable.setDefaultRenderer(Object.class, new StatisticsRenderer());
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
    private void initComponents() {

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
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        viewTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        sp2.setViewportView(viewTable);

        tableLabel2.setLabelFor(viewTable);
        tableLabel2.setText("Breakdown by chromosome:");

        sp1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        sumTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        sumTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        sp1.setViewportView(sumTable);

        tableLabel1.setLabelFor(sumTable);
        tableLabel1.setText("Totals:");

        org.jdesktop.layout.GroupLayout tablePanelLayout = new org.jdesktop.layout.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tableLabel1)
                    .add(sp1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tableLabel2)
                    .add(sp2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
                .addContainerGap())
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tableLabel1)
                    .add(tableLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sp2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .add(sp1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(viewLabel)
                    .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(viewLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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


	public static class StatisticsRenderer extends DefaultTableCellRenderer
	{
		public StatisticsRenderer()
		{
			setHorizontalAlignment(JLabel.RIGHT);
		}
	}
}