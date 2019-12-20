// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class FilterDialog extends JDialog implements ActionListener
{
	private boolean isOK = false;
	private FilterDialogTableModel model;

	private GTViewSet viewSet;

	public static FilterDialog getFilterDialog(FilterColumn[] allCols, FilterColumn[] lastUsedCols, GTViewSet viewSet)
	{
		FilterDialog dialog = new FilterDialog(allCols, lastUsedCols, viewSet);
		dialog.infoLabel.setVisible(false);
		dialog.setVisible(true);

		return dialog;
	}

	// Changes the text of the dialog to make it appear like a Select dialog
	public static FilterDialog getSelectDialog(FilterColumn[] allCols, FilterColumn[] lastUsedCols, GTViewSet viewSet)
	{
		FilterDialog dialog = new FilterDialog(allCols, lastUsedCols, viewSet);

		dialog.setTitle(RB.getString("gui.table.FilterDialog.titleAS"));
		RB.setText(dialog.bFilter, "gui.table.FilterDialog.bFilterAS");
		dialog.model.setColumnName(1, RB.getString("gui.table.FilterDialog.col2AS"));
		dialog.setVisible(true);


		return dialog;
	}


	private FilterDialog(FilterColumn[] allCols, FilterColumn[] lastUsedCols)
	{
		super(Flapjack.winMain, RB.getString("gui.table.FilterDialog.title"), true);

		initComponents();
		initTable(allCols, lastUsedCols);

		RB.setText(bCancel, "gui.text.cancel");
		RB.setText(bFilter, "gui.table.FilterDialog.bFilter");
		RB.setText(bReset, "gui.table.FilterDialog.bReset");

		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "analysis_results_tables.html#filtering-lines");

		getContentPane().setBackground((Color)UIManager.get("fjDialogBG"));
		bFilter.addActionListener(this);
		bReset.addActionListener(this);
		bCancel.addActionListener(this);

		getRootPane().setDefaultButton(bFilter);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
	}

	private FilterDialog(FilterColumn[] allCols, FilterColumn[] lastUsedCols, GTViewSet viewSet)
	{
		super(Flapjack.winMain, RB.getString("gui.table.FilterDialog.title"), true);

		this.viewSet = viewSet;

		initComponents();
		initTable(allCols, lastUsedCols);

		RB.setText(bCancel, "gui.text.cancel");
		RB.setText(bFilter, "gui.table.FilterDialog.bFilter");
		RB.setText(bReset, "gui.table.FilterDialog.bReset");

		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "analysis_results_tables.html#filtering-lines");

		getContentPane().setBackground((Color)UIManager.get("fjDialogBG"));
		bFilter.addActionListener(this);
		bReset.addActionListener(this);
		bCancel.addActionListener(this);

		getRootPane().setDefaultButton(bFilter);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
	}

	// We construct the dialog on the assumption of use as a 'Filter' (as seen
	// by the user), but if isAutoSelect=true, then we'll change the buttons so
	// it can be used as a 'Select' dialog instead
	public void setUsageAndDisplay(boolean isAutoSelect)
	{
		setTitle(RB.getString("gui.table.FilterDialog.titleAS"));

		setVisible(true);
	}

	private JTable createTable()
	{
		return new JTable()
		{
			public TableCellEditor getCellEditor(int row, int column)
			{
				int modelColumn = convertColumnIndexToModel(column);

				if (modelColumn == 1)
				{
					if (model.needsBooleanFilter(row))
						return new DefaultCellEditor(FilterColumn.booleanFilters());
					else if (model.needsPedVerF1sFilter(row))
						return new DefaultCellEditor(PedVerF1sFilterColumn.pedVerF1Filters(viewSet.getPedVerF1sBatchList().getDecisionMethod()));
					else if (model.needsPedVerLinesFilter(row))
						return new DefaultCellEditor(PedVerLinesFilterColumn.pedVerLinesFilters());
					else if (model.needsMabcFilter(row))
						return new DefaultCellEditor(MabcFilterColumn.mabcFilters());
					else
						return new DefaultCellEditor(FilterColumn.numericalFilters());
				}
				else
				{
					DefaultCellEditor editor =
						(DefaultCellEditor)super.getCellEditor(row, column);
					editor.setClickCountToStart(1);
					return editor;
				}
			}
		};
	}

	private void initTable(FilterColumn[] allCols, FilterColumn[] lastUsedCols)
	{
		model = new FilterDialogTableModel(allCols, lastUsedCols);

		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Deals with the table not storing edits when focus is lost (mainly due
		// to the user pressing OK, which is when we *want* edits saved!!
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFilter)
		{
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bReset)
			model.clear();

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public FilterColumn[] getResults()
	{
		FilterColumn[] results = model.getResults();

		// We copy the results into a new array (with the internal elements
		// being cloned too) because each trackable instance needs to be unique
		FilterColumn[] clone = new FilterColumn[results.length];
		for (int i = 0; i < clone.length; i++)
			clone[i] = results[i].cloneMe();

		return clone;
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
    private void initComponents() {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bFilter = new javax.swing.JButton();
        bReset = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = createTable();
        infoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bFilter.setText("Filter");
        dialogPanel1.add(bFilter);

        bReset.setText("Reset");
        dialogPanel1.add(bReset);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jScrollPane1.setViewportView(table);

        infoLabel.setText("Note that selection criteria will only apply to lines that are currently visible in the results table.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bFilter;
    private javax.swing.JButton bHelp;
    private javax.swing.JButton bReset;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}