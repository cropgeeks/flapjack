// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.traits;

import flapjack.gui.*;

import scri.commons.gui.*;

class NBQTLControlPanel extends javax.swing.JPanel
{
	NBQTLControlPanel()
	{
		initComponents();

		statusLabel.setText(RB.format("gui.traits.QTLPanel.traitCount", 0));

		RB.setText(trackLabel, "gui.traits.NBQTLControlPanel.trackLabel");
		RB.setText(bImport, "gui.traits.NBQTLControlPanel.bImport");
		RB.setText(bRemove, "gui.traits.NBQTLControlPanel.bRemove");

		bImport.setIcon(Icons.getIcon("IMPORTTRAITS"));
		bRemove.setIcon(Icons.getIcon("DELETE"));
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bImport = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        bRemove = new javax.swing.JButton();
        trackSpinner = new javax.swing.JSpinner();
        trackLabel = new javax.swing.JLabel();

        bImport.setText("Import QTL data");

        statusLabel.setText("Number of QTLs: 0");

        bRemove.setText("Remove all QTLs");

        trackSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 15, 1));

        trackLabel.setText("Active tracks:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(statusLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 52, Short.MAX_VALUE)
                .add(trackLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(trackSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(bImport)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(bRemove)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusLabel)
                    .add(bRemove)
                    .add(bImport)
                    .add(trackSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(trackLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bImport;
    javax.swing.JButton bRemove;
    javax.swing.JLabel statusLabel;
    private javax.swing.JLabel trackLabel;
    javax.swing.JSpinner trackSpinner;
    // End of variables declaration//GEN-END:variables

}