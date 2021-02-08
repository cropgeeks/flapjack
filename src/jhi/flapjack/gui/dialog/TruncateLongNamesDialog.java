// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class TruncateLongNamesDialog extends JDialog
{
	private GTViewSet viewSet;
	private ListPanel listPanel;
	
	public TruncateLongNamesDialog(GTViewSet viewSet, ListPanel listPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.TruncateLongNamesDialog.title"),
			true
		);

		this.viewSet = viewSet;
		this.listPanel = listPanel;
		
		initComponents();
		initComponents2();

		FlapjackUtils.initDialog(this, bClose, bClose, true, getContentPane());
	}

	private void initComponents2()
	{
		RB.setText(bClose, "gui.text.close");
		bClose.addActionListener(e -> setVisible(false));

		RB.setText(chkTruncate, "gui.dialog.TruncateLongNamesDialog.chkbox");
		chkTruncate.setSelected(Prefs.guiTruncateNames);
		chkTruncate.addActionListener(actionEvent ->
		{
			Prefs.guiTruncateNames = !Prefs.guiTruncateNames;
			listPanel.populateList();

			truncateSpinner.setEnabled(Prefs.guiTruncateNames);
		});

		int max = viewSet.getLines().stream()
			.map(LineInfo::name)
			.max(Comparator.comparingInt(String::length))
			.get()
			.length();

		truncateSpinner.setEnabled(Prefs.guiTruncateNames);
		SpinnerNumberModel model = new SpinnerNumberModel(Math.min(Prefs.guiTruncateNamesLength, max), 1, max, 1);
		truncateSpinner.setModel(model);
		truncateSpinner.addChangeListener(changeEvent ->
		{
			JSpinner spinner = (JSpinner) changeEvent.getSource();
			Prefs.guiTruncateNamesLength = (int)spinner.getValue();
			listPanel.populateList();
		});
	}


	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bClose = new javax.swing.JButton();
        chkTruncate = new javax.swing.JCheckBox();
        truncateSpinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bClose.setText("Close");
        dialogPanel1.add(bClose);

        chkTruncate.setText("Maximum line name length:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkTruncate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(truncateSpinner)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkTruncate)
                    .addComponent(truncateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bClose;
    private javax.swing.JCheckBox chkTruncate;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JSpinner truncateSpinner;
    // End of variables declaration//GEN-END:variables
}