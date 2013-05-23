// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class SelectLMPanelNB extends javax.swing.JPanel
{
	public SelectLMPanelNB(GTView view, boolean selectLines)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));

		if (selectLines)
		{
			RB.setText(label, "gui.dialog.NBSelectLMPanel.lineLabel");

			for (int i = 0; i < view.lineCount(); i++)
				combo.addItem(view.getLine(i).getName());

			if (view.mouseOverLine >= 0 && view.mouseOverLine < view.lineCount())
				combo.setSelectedIndex(view.mouseOverLine);
		}
		else
		{
			RB.setText(label, "gui.dialog.NBSelectLMPanel.markerLabel");

			for (int i = 0; i < view.markerCount(); i++)
				combo.addItem(view.getMarker(i).getName());

			if (view.mouseOverMarker >= 0 && view.mouseOverMarker < view.markerCount())
				combo.setSelectedIndex(view.mouseOverMarker);
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();
        combo = new javax.swing.JComboBox<String>();

        label.setLabelFor(combo);
        label.setText("Select comparison line:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(combo, 0, 255, Short.MAX_VALUE)
                    .addComponent(label))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox<String> combo;
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables
}