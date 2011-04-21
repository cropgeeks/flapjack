// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.analysis;

import java.awt.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

/**
 *
 * @author  imilne
 */
class NBSortingLinesProgressPanel extends javax.swing.JPanel
{

	/** Creates new form NBSortingLinesProgressPanel */
	public NBSortingLinesProgressPanel()
	{
		initComponents();

		RB.setText(label, "gui.dialog.analysis.NBSortingLinesProgressPanel.label");
	}

	JProgressBar getProgressBar()
		{ return pBar; }

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents()
    {

        label = new javax.swing.JLabel();
        pBar = new javax.swing.JProgressBar();

        label.setText("Loading project - please be patient...");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(label))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 66, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(label)
                .add(11, 11, 11)
                .add(pBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>


    // Variables declaration - do not modify
    private javax.swing.JLabel label;
    private javax.swing.JProgressBar pBar;
    // End of variables declaration


}