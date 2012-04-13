// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.dialog.*;

import scri.commons.gui.*;

public class NBStartWelcomePanel extends javax.swing.JPanel implements ActionListener
{
	public NBStartWelcomePanel()
	{
		initComponents();
		setOpaque(false);

		flapjackLabel.setText("<html>" + RB.format("gui.navpanel.NBStartWelcomePanel.panel.label", Install4j.VERSION));
		feedbackLabel.setText(RB.getString("gui.navpanel.NBStartWelcomePanel.panel.feedback"));
		twitterLabel.setText(RB.getString("gui.navpanel.NBStartWelcomePanel.panel.twitter"));

		feedbackLabel.setIcon(Icons.getIcon("FEEDBACK"));
		twitterLabel.setIcon(Icons.getIcon("TWITTER"));
		feedbackLabel.addActionListener(this);
		twitterLabel.addActionListener(this);
	}

	private void sendFeedback()
	{
		if (SystemUtils.jreVersion() >= 1.6)
			FlapjackUtils.sendFeedback();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == feedbackLabel)
			FlapjackUtils.sendFeedback();

		else if (e.getSource() == twitterLabel)
			FlapjackUtils.visitURL("http://twitter.com/cropgeeks");
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        flapjackLabel = new javax.swing.JLabel();
        feedbackLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        twitterLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        flapjackLabel.setText("<html>Flapjack x.xx.xx.xx - &copy; Plant Bioinformatics Group, SCRI.");

        feedbackLabel.setForeground(new java.awt.Color(68, 106, 156));
        feedbackLabel.setText("Send feedback");

        twitterLabel.setForeground(new java.awt.Color(68, 106, 156));
        twitterLabel.setText("Follow us");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(flapjackLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 40, Short.MAX_VALUE)
                .add(feedbackLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(twitterLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(flapjackLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(twitterLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(feedbackLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel feedbackLabel;
    private javax.swing.JLabel flapjackLabel;
    private scri.commons.gui.matisse.HyperLinkLabel twitterLabel;
    // End of variables declaration//GEN-END:variables

}