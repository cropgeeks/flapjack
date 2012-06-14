// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.traits;

import scri.commons.gui.*;

class TraitsPanelNB extends javax.swing.JPanel
{
	TraitsPanelNB()
	{
		initComponents();

		RB.setText(bImport, "gui.traits.NBTraitsControlPanel.bImport");
		RB.setText(bExport, "gui.traits.NBTraitsControlPanel.bExport");
		RB.setText(bRemove, "gui.traits.NBTraitsControlPanel.bRemove");

		bImport.setIcon(Icons.getIcon("IMPORTTRAITS"));
		bExport.setIcon(Icons.getIcon("EXPORTTRAITS"));
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
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        bExport = new javax.swing.JButton();

        bImport.setText("Import");

        statusLabel.setText("Number of traits: 0");

        bRemove.setText("Clear");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        bExport.setText("Export");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                .addComponent(bImport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bRemove)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bRemove)
                    .addComponent(statusLabel)
                    .addComponent(bImport)
                    .addComponent(bExport))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bExport;
    javax.swing.JButton bImport;
    javax.swing.JButton bRemove;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JLabel statusLabel;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}