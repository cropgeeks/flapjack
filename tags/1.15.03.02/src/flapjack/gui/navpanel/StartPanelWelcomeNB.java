// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import java.awt.event.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class StartPanelWelcomeNB extends javax.swing.JPanel implements ActionListener
{
	public StartPanelWelcomeNB()
	{
		initComponents();
		setOpaque(false);

		flapjackLabel.setText("<html>" + RB.format("gui.navpanel.NBStartWelcomePanel.panel.label", Install4j.VERSION, "\u0026"));
		feedbackLabel.setText(RB.getString("gui.navpanel.NBStartWelcomePanel.panel.feedback"));
		twitterLabel.setText(RB.getString("gui.navpanel.NBStartWelcomePanel.panel.twitter"));

		feedbackLabel.setIcon(Icons.getIcon("FEEDBACK"));
		twitterLabel.setIcon(Icons.getIcon("TWITTER"));
		feedbackLabel.addActionListener(this);
		twitterLabel.addActionListener(this);
	}

	@Override
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
    private void initComponents()
    {

        flapjackLabel = new javax.swing.JLabel();
        feedbackLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        twitterLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        flapjackLabel.setText("<html>Flapjack x.xx.xx.xx - &copy; Plant Bioinformatics Group, JHI.");

        feedbackLabel.setForeground(new java.awt.Color(68, 106, 156));
        feedbackLabel.setText("Feedback");

        twitterLabel.setForeground(new java.awt.Color(68, 106, 156));
        twitterLabel.setText("Follow");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(flapjackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 197, Short.MAX_VALUE)
                .addComponent(feedbackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(twitterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(flapjackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(twitterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(feedbackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel feedbackLabel;
    private javax.swing.JLabel flapjackLabel;
    private scri.commons.gui.matisse.HyperLinkLabel twitterLabel;
    // End of variables declaration//GEN-END:variables

}