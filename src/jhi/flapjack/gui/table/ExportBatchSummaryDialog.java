// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.table;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class ExportBatchSummaryDialog extends JDialog implements ActionListener
{
	private boolean isOK = false;

	public ExportBatchSummaryDialog()
	{
		super(Flapjack.winMain, RB.getString("gui.table.ExportBatchSummaryDialog.title"), true);

		initComponents();
		initComponents2();

		RB.setText(bCancel, "gui.text.cancel");
		RB.setText(bExport, "gui.table.ExportBatchSummaryDialog.bExport");
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "batch_analysis.html#export-results-summary-to-file");

		initOptions();

		bBrowse.addActionListener(this);
		bExport.addActionListener(this);
		bCancel.addActionListener(this);

		FlapjackUtils.initDialog(this, bExport, bCancel, true, getContentPane());
	}

	private void initComponents2()
	{
		RB.setText(fileLabel, "gui.table.ExportBatchSummaryDialog.fileLabel");
		RB.setText(bBrowse, "gui.text.browse");
	}

	private void initOptions()
	{
		filename.setHistory(Prefs.guiSumTableExportHistory);
	}

	private boolean saveOptions()
	{
		if (filename.getText().isEmpty())
		{
			TaskDialog.warning(
				"You must supply a file name to write the exported data to.",
				RB.getString("gui.text.ok"));
			return false;
		}

		Prefs.guiSumTableExportHistory = filename.getHistory();

		return true;
	}

	File getFilename()
		{ return new File(filename.getText()); }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bBrowse)
			browseForFile();

		else if (e.getSource() == bExport)
		{
			if (saveOptions() == false)
				return;

			isOK = true;
			setVisible(false);
		}
		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	private void browseForFile()
	{
		File saveAs = new File(Prefs.guiCurrentDir, "table-data.txt");
		if (!filename.getText().isEmpty())
			saveAs = new File(filename.getText());

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.txt"), "txt");

		// Ask the user for a filename to save the current view as
		String file = FlapjackUtils.getSaveFilename("Save table data as", saveAs, filter);

		// Quit if the user cancelled the file selection
		if (file != null)
			filename.updateComboBox(file);
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

        bGroup = new javax.swing.ButtonGroup();
        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bExport = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
        fileLabel = new javax.swing.JLabel();
        filename = new scri.commons.gui.matisse.HistoryComboBox();
        bBrowse = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bExport.setText("Export");
        dialogPanel1.add(bExport);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        fileLabel.setText("File name:");

        bBrowse.setText("Browse...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filename, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bBrowse)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileLabel)
                    .addComponent(filename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowse;
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bExport;
    private javax.swing.ButtonGroup bGroup;
    private javax.swing.JButton bHelp;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel fileLabel;
    private scri.commons.gui.matisse.HistoryComboBox filename;
    // End of variables declaration//GEN-END:variables
}