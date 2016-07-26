// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class SortDialog extends JDialog implements ActionListener, ListSelectionListener
{
	private boolean isOK = false;
	private SortDialogTableModel model;

	public SortDialog(SortColumn[] allCols, SortColumn[] lastUsedCols)
	{
		super(Flapjack.winMain, RB.getString("gui.table.SortDialog.title"), true);

		initComponents();
		initTable(allCols, lastUsedCols);

		RB.setText(bCancel, "gui.text.cancel");
		RB.setText(bSort, "gui.table.SortDialog.bSort");
		RB.setText(bAdd, "gui.table.SortDialog.bAdd");
		RB.setText(bDelete, "gui.table.SortDialog.bDelete");

		setBackground((Color)UIManager.get("fjDialogBG"));
		bSort.addActionListener(this);
		bCancel.addActionListener(this);
		bAdd.addActionListener(this);
		bDelete.addActionListener(this);

		checkButtonStates();

		getRootPane().setDefaultButton(bSort);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void initTable(SortColumn[] allCols, SortColumn[] lastUsedCols)
	{
		model = new SortDialogTableModel(allCols, lastUsedCols);

		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		UIScaler.setCellHeight(table);

		table.getColumnModel().getColumn(0).setCellEditor(
			new DefaultCellEditor(model.getColumnNameComboBox()));
		table.getColumnModel().getColumn(1).setCellEditor(
			new DefaultCellEditor(model.getSortOrderComboBox()));
		table.getColumnModel().getColumn(1).setPreferredWidth(60);

		// Needed when combo box editors lose focus (pressing Delete) and they
		// can end up calling setValueAt after the row has been removed
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bAdd)
			model.addRow();
		else if (e.getSource() == bDelete)
			model.deleteRow(table.getSelectedRow());

		checkButtonStates();

		if (e.getSource() == bSort)
		{
			isOK = true;
			setVisible(false);
		}
		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public SortColumn[] getResults()
	{
		return model.getResults();
	}

	private void checkButtonStates()
	{
		bAdd.setEnabled(model.getRowCount() > 0);

		// Only enable the delete button if something is selected and there's
		// 2 or more items in the table
		if (model.getRowCount() > 1 && table.getSelectedRow() != -1)
			bDelete.setEnabled(true);
		else
			bDelete.setEnabled(false);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		checkButtonStates();
	}

	public boolean isOK()
		{ return isOK; }

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
        bSort = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        bAdd = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bSort.setText("Sort");
        dialogPanel1.add(bSort);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {

            }
        ));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jScrollPane1.setViewportView(table);

        bAdd.setText("Add sort level");

        bDelete.setText("Delete sort level");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bDelete)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bAdd)
                    .addComponent(bDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAdd;
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bSort;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}