// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.HyperLinkLabel;

public class NBStartFilePanel extends javax.swing.JPanel implements ActionListener
{
	private HyperLinkLabel[] labels = new HyperLinkLabel[4];
	private String[] files = new String[4];

	public NBStartFilePanel()
	{
		initComponents();
		setOpaque(false);

		RB.setText(importLabel, "gui.navpanel.NBStartFilePanel.importLabel");
		RB.setText(openLabel, "gui.navpanel.NBStartFilePanel.openLabel");
		RB.setText(rateLabel, "gui.navpanel.NBStartFilePanel.rateLabel");

		importLabel.setIcon(Icons.getIcon("FILEIMPORT"));
		importLabel.addActionListener(this);

		// Create the labels array
		labels[0] = project0; labels[1] = project1;
		labels[2] = project2; labels[3] = project3;

		// Create the files array
		files[0] = Prefs.guiRecentProject1; files[1] = Prefs.guiRecentProject2;
		files[2] = Prefs.guiRecentProject3; files[3] = Prefs.guiRecentProject4;

		for (int i = 0; i < labels.length; i++)
		{
			if (files[i] != null)
			{
				labels[i].addActionListener(this);
				labels[i].setText(files[i]);
			}
			else
				labels[i].setVisible(false);
		}

		ratingsPanel.doSetup(Prefs.rating,
			Icons.getIcon("STARON"), Icons.getIcon("STAROFF"));
		ratingsPanel.addActionListener(this);
    }

	public void actionPerformed(ActionEvent e)
	{
		WinMain wm = Flapjack.winMain;

		if(e.getSource() == importLabel)
		{
			wm.mFile.fileImport();
		}

		for (int i = 0; i < labels.length; i++)
			if (e.getSource() == labels[i])
				wm.mFile.fileOpen(new File(files[i]));

		if (e.getSource() == ratingsPanel)
			Prefs.rating = ratingsPanel.getRating();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        openLabel = new javax.swing.JLabel();
        importLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        project0 = new scri.commons.gui.matisse.HyperLinkLabel();
        project1 = new scri.commons.gui.matisse.HyperLinkLabel();
        project2 = new scri.commons.gui.matisse.HyperLinkLabel();
        project3 = new scri.commons.gui.matisse.HyperLinkLabel();
        rateLabel = new javax.swing.JLabel();
        ratingsPanel = new scri.commons.gui.matisse.RatingsPanel();

        openLabel.setText("Open an previously accessed Flapjack project:");

        importLabel.setForeground(new java.awt.Color(68, 106, 156));
        importLabel.setText("Import data into Flapjack");

        project0.setForeground(new java.awt.Color(68, 106, 156));
        project0.setText("<project0>");

        project1.setForeground(new java.awt.Color(68, 106, 156));
        project1.setText("<project1>");

        project2.setForeground(new java.awt.Color(68, 106, 156));
        project2.setText("<project2>");

        project3.setForeground(new java.awt.Color(68, 106, 156));
        project3.setText("<project3>");

        rateLabel.setText("Click to rate Flapjack:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(importLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(openLabel)
                    .add(project0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(project1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(project2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(project3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(rateLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(ratingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(importLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(openLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(project0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(project1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(project2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(project3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(rateLabel)
                    .add(ratingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel importLabel;
    private javax.swing.JLabel openLabel;
    private scri.commons.gui.matisse.HyperLinkLabel project0;
    private scri.commons.gui.matisse.HyperLinkLabel project1;
    private scri.commons.gui.matisse.HyperLinkLabel project2;
    private scri.commons.gui.matisse.HyperLinkLabel project3;
    private javax.swing.JLabel rateLabel;
    private scri.commons.gui.matisse.RatingsPanel ratingsPanel;
    // End of variables declaration//GEN-END:variables

}