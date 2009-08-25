// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.prefs;

import java.awt.*;
import java.awt.event.*;
import static java.awt.image.BufferedImage.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

class NBVisualizationPanel extends JPanel implements IPrefsTab, ActionListener
{
	public NBVisualizationPanel()
    {
        initComponents();

        setBackground((Color)UIManager.get("fjDialogBG"));
        panel.setBackground((Color)UIManager.get("fjDialogBG"));

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBVisualizationPanel.panelTitle")));

		RB.setText(checkVisBackBuffer, "gui.dialog.prefs.NBVisualizationPanel.checkVisBackBuffer");
		RB.setText(checkVisBackBufferType, "gui.dialog.prefs.NBVisualizationPanel.checkVisBackBufferType");
		RB.setText(checkVisAdvancedZoom, "gui.dialog.prefs.NBVisualizationPanel.checkVisAdvancedZoom");
		RB.setText(checkVisCrosshair, "gui.dialog.prefs.NBVisualizationPanel.checkVisCrosshair");

		checkVisBackBuffer.addActionListener(this);

        initSettings();
    }

    public void actionPerformed(ActionEvent e)
    {
    	if (e.getSource() == checkVisBackBuffer)
    		checkVisBackBufferType.setEnabled(checkVisBackBuffer.isSelected());
    }

    private void initSettings()
    {
    	checkVisBackBuffer.setSelected(Prefs.visBackBuffer);
    	checkVisBackBufferType.setSelected(Prefs.visBackBufferType == TYPE_BYTE_INDEXED);
    	checkVisBackBufferType.setEnabled(checkVisBackBuffer.isSelected());
		checkVisAdvancedZoom.setSelected(Prefs.visAdvancedZoom);
		checkVisCrosshair.setSelected(Prefs.visCrosshair);
    }

	public void applySettings()
	{
		Prefs.visBackBuffer = checkVisBackBuffer.isSelected();
		Prefs.visBackBufferType = checkVisBackBufferType.isSelected() ? TYPE_BYTE_INDEXED : TYPE_INT_RGB;
		Prefs.visAdvancedZoom = checkVisAdvancedZoom.isSelected();
		Prefs.visCrosshair = checkVisCrosshair.isSelected();

		NBStatusPanel.setControlStates();
	}

	public void setDefaults()
	{
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        checkVisBackBuffer = new javax.swing.JCheckBox();
        checkVisBackBufferType = new javax.swing.JCheckBox();
        checkVisAdvancedZoom = new javax.swing.JCheckBox();
        checkVisCrosshair = new javax.swing.JCheckBox();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Performance options:"));

        checkVisBackBuffer.setText("Attempt to back-buffer the main canvas to improve performance");

        checkVisBackBufferType.setText("Use an 8 bit colour buffer to reduce memory usage");

        checkVisAdvancedZoom.setText("Enable advanced canvas zoom controls");

        checkVisCrosshair.setText("Highlight the mouse position when over the canvas");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(checkVisBackBuffer)
                    .add(checkVisBackBufferType)
                    .add(checkVisAdvancedZoom)
                    .add(checkVisCrosshair))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(checkVisBackBuffer)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkVisBackBufferType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkVisAdvancedZoom)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkVisCrosshair)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkVisAdvancedZoom;
    private javax.swing.JCheckBox checkVisBackBuffer;
    private javax.swing.JCheckBox checkVisBackBufferType;
    private javax.swing.JCheckBox checkVisCrosshair;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}