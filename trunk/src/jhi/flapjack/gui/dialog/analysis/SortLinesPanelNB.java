// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class SortLinesPanelNB extends javax.swing.JPanel implements ActionListener
{
	private GTView view;

	public SortLinesPanelNB(SortLinesDialog dialog, GTViewSet viewSet)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		linePanel.setBackground((Color)UIManager.get("fjDialogBG"));
		tablePanel.setBackground((Color)UIManager.get("fjDialogBG"));

		if (true)
			RB.setText(lineLabel, "gui.dialog.analysis.NBSortLinesPanel.lineLabel.similarity");

		linePanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.NBSortLinesPanel.linePanel.title")));
		tablePanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.NBSortLinesPanel.tablePanel.title")));
		RB.setText(tableLabel, "gui.dialog.analysis.NBSortLinesPanel.tableLabel");
		RB.setText(selectAllLabel, "gui.dialog.analysis.NBSortLinesPanel.selectAllLabel");
		RB.setText(selectNoneLabel, "gui.dialog.analysis.NBSortLinesPanel.selectNoneLabel");

		view = viewSet.getView(viewSet.getViewIndex());

		DefaultComboBoxModel<LineInfo> lineModel = new DefaultComboBoxModel<>();
		for (int i = 0; i < view.lineCount(); i++)
			lineModel.addElement(view.getLineInfo(i));

		selectedLine.setModel(lineModel);

		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.lineCount())
			selectedLine.setSelectedIndex(view.mouseOverLine);

		selectedLine.addActionListener(dialog);

		selectAllLabel.addActionListener(this);
		selectNoneLabel.addActionListener(this);
		createTable(viewSet);
	}

	private void createTable(GTViewSet viewSet)
	{
		String[] columnNames = {
			RB.getString("gui.dialog.analysis.NBSortLinesPanel.column1"),
			RB.getString("gui.dialog.analysis.NBSortLinesPanel.column2"),
			RB.getString("gui.dialog.analysis.NBSortLinesPanel.column3")
		};

		// Fill the data array with the string values from the list
		Object[][] data = new Object[viewSet.chromosomeCount()][3];

		for (int i = 0; i < viewSet.chromosomeCount(); i++)
		{
			GTView view = viewSet.getView(i);

			if (viewSet.getViewIndex() == i)
				data[i][0] = new Boolean(true);
			else
				data[i][0] = new Boolean(false);

			data[i][1] = view.getChromosomeMap().getName();
			data[i][2] = view.countSelectedMarkers() + " / "
				+ view.markerCount();

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

		// Ensure the single selection is actually visible when first displayed
		Rectangle r = table.getCellRect(viewSet.getViewIndex(), 0, true);
		table.scrollRectToVisible(r);
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used in the sort
	boolean[] getSelectedChromosomes()
	{
		boolean[] array = new boolean[table.getRowCount()];

		for (int i = 0; i < array.length; i++)
			array[i] = (Boolean) table.getValueAt(i, 0);

		return array;
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


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group1 = new javax.swing.ButtonGroup();
        group2 = new javax.swing.ButtonGroup();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        tableLabel = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        selectAllLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        selectNoneLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        linePanel = new javax.swing.JPanel();
        selectedLine = new javax.swing.JComboBox<LineInfo>();
        lineLabel = new javax.swing.JLabel();

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sort over chromosomes:"));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        tableLabel.setLabelFor(table);
        tableLabel.setText("Only markers from the following selected chromosomes will be used:");

        label2.setText("|");

        selectAllLabel.setText("Select all");

        selectNoneLabel.setText("Select none");

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(tableLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tablePanelLayout.createSequentialGroup()
                        .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label2)
                    .addComponent(selectAllLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectNoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        linePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Comparison line:"));

        lineLabel.setLabelFor(selectedLine);
        lineLabel.setText("Sort {0} to this line:");

        javax.swing.GroupLayout linePanelLayout = new javax.swing.GroupLayout(linePanel);
        linePanel.setLayout(linePanelLayout);
        linePanelLayout.setHorizontalGroup(
            linePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedLine, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        linePanelLayout.setVerticalGroup(
            linePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(linePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineLabel)
                    .addComponent(selectedLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tablePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(linePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(linePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup group1;
    private javax.swing.ButtonGroup group2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JPanel linePanel;
    private scri.commons.gui.matisse.HyperLinkLabel selectAllLabel;
    private scri.commons.gui.matisse.HyperLinkLabel selectNoneLabel;
    javax.swing.JComboBox<LineInfo> selectedLine;
    private javax.swing.JTable table;
    private javax.swing.JLabel tableLabel;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
}