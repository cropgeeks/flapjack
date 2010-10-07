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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 179, Short.MAX_VALUE)
                .addComponent(bImport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bRemove)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(bRemove)
                    .addComponent(bImport))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bImport;
    javax.swing.JButton bRemove;
    javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

}