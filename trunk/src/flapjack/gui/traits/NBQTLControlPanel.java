// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
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

        bImport.setText("Import QTL data");

        statusLabel.setText("Number of QTLs: 0");

        bRemove.setText("Remove all QTLs");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(statusLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 179, Short.MAX_VALUE)
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
                    .add(bImport))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bImport;
    javax.swing.JButton bRemove;
    javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

}