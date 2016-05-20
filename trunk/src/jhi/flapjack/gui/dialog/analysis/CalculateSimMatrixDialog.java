// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class CalculateSimMatrixDialog extends JDialog implements ActionListener, TableModelListener
{
	private GTViewSet viewSet;

	private boolean isOK;

	public CalculateSimMatrixDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.CalculateSimMatrixDialog.title"),
			true
		);

		this.viewSet = viewSet;
		isOK = false;

		initComponents();
		initComponents2();
		getContentPane().setBackground(Color.WHITE);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bOK);

		// If fewer than 2 lines are selected, disable the OK button.
		if (viewSet.getView(0).countSelectedLines() < 2)
			bOK.setEnabled(false);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void initComponents2()
	{
		RB.setText(bOK, "gui.text.ok");
		bOK.addActionListener(this);

		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);

		chromosomeSelectionPanel.setupComponents(viewSet);
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used in the sort
	public boolean[] getSelectedChromosomes()
	{
		return chromosomeSelectionPanel.getSelectedChromosomes();
	}

	public boolean isOK()
		{ return isOK; }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	@Override
	public void tableChanged(TableModelEvent e)
	{
//		boolean canGenerate = false;
//
//		for (int i=0; i < model.getRowCount(); i++)
//			if (model.getValueAt(i, 0) == true)
//				canGenerate = true;
//
//		bOK.setEnabled(canGenerate);
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

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        chromosomeSelectionPanel = new jhi.flapjack.gui.dialog.analysis.ChromosomeSelectionPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOK.setText("OK");
        dialogPanel1.add(bOK);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chromosomeSelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chromosomeSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private jhi.flapjack.gui.dialog.analysis.ChromosomeSelectionPanel chromosomeSelectionPanel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    // End of variables declaration//GEN-END:variables

}