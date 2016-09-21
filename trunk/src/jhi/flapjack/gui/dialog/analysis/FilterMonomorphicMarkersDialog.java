// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class FilterMonomorphicMarkersDialog extends JDialog implements ActionListener
{
	private GTViewSet viewSet;
	private ChromosomeSelectionDialog csd;

	private boolean isOK;

	public FilterMonomorphicMarkersDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.FilterMonomorphicDialog.title"),
			true
		);

		this.viewSet = viewSet;
		isOK = false;

		initComponents();
		initComponents2();
		FlapjackUtils.setDialogBG(getContentPane());

		getRootPane().setDefaultButton(bFilter);
		SwingUtils.addCloseHandler(this, bFilter);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void initComponents2()
	{
//		RB.setText(bFilter, "gui.text.ok");
		bFilter.addActionListener(this);

		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);

		csd = new ChromosomeSelectionDialog(viewSet, false);
		csdLabel.addActionListener(e -> { csd.setVisible(true); } );
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used in the sort
	public boolean[] getSelectedChromosomes()
	{
		return csd.getSelectedChromosomes();
	}

	public boolean isOK()
		{ return isOK; }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFilter)
		{
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
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
        bFilter = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        csdLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bFilter.setText("Filter");
        dialogPanel1.add(bFilter);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        jLabel1.setText("This filter will remove all markers that are monomorphic across all of the currently selected lines.");

        csdLabel.setText("Select chromosomes to filter across");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(csdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(csdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bFilter;
    private scri.commons.gui.matisse.HyperLinkLabel csdLabel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}