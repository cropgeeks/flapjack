// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class PCoASettingsDialog extends JDialog implements ActionListener
{
	private boolean isOK;

	private SpinnerNumberModel dimSpinnerModel;

	private int noLines;

	public PCoASettingsDialog(int noLines)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.PCoASettingsDialog.title"),
			true
		);

		isOK = false;

		this.noLines = noLines;

		initComponents();
		initComponents2();
		getContentPane().setBackground(Color.WHITE);

		setupComponentText();
		setupDimensionSpinner();

		curlywhirlyLinkLabel.addActionListener(this);

		FlapjackUtils.initDialog(this, bOK, bCancel, true, getContentPane());
	}

	private void initComponents2()
	{
		RB.setText(bOK, "gui.text.ok");
		bOK.addActionListener(this);

		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);
	}

	private void setupComponentText()
	{
		RB.setText(pcoaLabel, "gui.dialog.analysis.PCoASettingsDialog.pcoaLabel");
		RB.setText(curlywhirlyLabel, "gui.dialog.analysis.PCoASettingsDialog.curlywhirlyLabel");
		RB.setText(curlywhirlyLinkLabel, "gui.dialog.analysis.PCoASettingsDialog.curlywhirlyLinkLabel");
		RB.setText(pcoaDimLablel, "gui.dialog.analysis.PCoASettingsDialog.pcoaDimLablel");
		RB.setText(dimensionHelpLabel, "gui.dialog.analysis.PCoASettingsDialog.dimensionHelpLabel");
	}

	private void setupDimensionSpinner()
	{
		int maxPossDims = noLines - 1;

		int min = 1;
		int max = maxPossDims > 20 ? 20 : maxPossDims;
		int def = maxPossDims < 3 ? maxPossDims : 3;

		dimSpinnerModel = new SpinnerNumberModel(def, min, max, 1);
		dimSpinner.setModel(dimSpinnerModel);
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

		else if (e.getSource() == curlywhirlyLinkLabel)
			FlapjackUtils.visitURL("http://ics.hutton.ac.uk/curlywhirly");
	}

	public String getNoDimensions()
	{
		return dimSpinner.getValue().toString();
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
        pcoaLabel = new javax.swing.JLabel();
        curlywhirlyLabel = new javax.swing.JLabel();
        curlywhirlyLinkLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        dimSpinner = new javax.swing.JSpinner();
        pcoaDimLablel = new javax.swing.JLabel();
        dimensionHelpLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOK.setText("OK");
        dialogPanel1.add(bOK);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        pcoaLabel.setText("PCoA generation relies on internet access to a remote server (ics.hutton.ac.uk) to perform the analysis.");

        curlywhirlyLabel.setText("You must have CurlyWhirly installed to be able to view any results.");

        curlywhirlyLinkLabel.setText("(CurlyWhirly)");

        pcoaDimLablel.setText("Number of PCoA Dimensions:");

        dimensionHelpLabel.setText("(cannot equal or excede number of lines, must be less than 20)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pcoaLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pcoaDimLablel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dimSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dimensionHelpLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(curlywhirlyLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(curlywhirlyLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pcoaLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(curlywhirlyLabel)
                    .addComponent(curlywhirlyLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pcoaDimLablel)
                    .addComponent(dimSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dimensionHelpLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private javax.swing.JLabel curlywhirlyLabel;
    private scri.commons.gui.matisse.HyperLinkLabel curlywhirlyLinkLabel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JSpinner dimSpinner;
    private javax.swing.JLabel dimensionHelpLabel;
    private javax.swing.JLabel pcoaDimLablel;
    private javax.swing.JLabel pcoaLabel;
    // End of variables declaration//GEN-END:variables

}