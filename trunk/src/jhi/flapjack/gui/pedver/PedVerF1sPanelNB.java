// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import javax.swing.*;

import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

class PedVerF1sPanelNB extends JPanel
{
	PedVerF1sPanelNB(PedVerF1sPanel panel)
	{
		initComponents();

		bFilter.setPopup(((LineDataTable)table).getMenu().getFilterMenu());
		bFilter.setIcon(Icons.getIcon("FILTER"));

		bSort.addActionListener(panel);
		bSort.setIcon(Icons.getIcon("SORT"));

		bExport.setPopup(((LineDataTable)table).getMenu().getExportMenu());
		bExport.setIcon(Icons.getIcon("EXPORTTRAITS"));

		bSelect.setPopup(((LineDataTable)table).getMenu().getSelectMenu());
		bSelect.setIcon(Icons.getIcon("AUTOSELECT"));

		autoResize.addActionListener(panel);

//		lblF1MarkerCount.setText("Expected F1 marker count: " + results.getF1MarkerCount());
//		lblF1HetCount.setText("Expected F1 heterozygous allele count: " + results.getF1HeterozygousCount());
//		lblF1HetPercentage.setText("Expected F1 heterozygous allele percentage: " + results.getF1PercentHeterozygous());
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new LineDataTable();
        bSort = new javax.swing.JButton();
        bExport = new scri.commons.gui.matisse.MenuButton();
        jLabel1 = new javax.swing.JLabel();
        autoResize = new javax.swing.JCheckBox();
        filterLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bFilter = new scri.commons.gui.matisse.MenuButton();
        bSelect = new scri.commons.gui.matisse.MenuButton();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {

            }
        ));
        jScrollPane1.setViewportView(table);

        bSort.setText("Sort...");

        bExport.setText("Export");

        jLabel1.setText("|");

        autoResize.setSelected(true);
        autoResize.setText("Auto-fit columns");
        autoResize.setOpaque(false);

        filterLabel.setText("Lines visible:");

        jLabel2.setText("|");

        bFilter.setText("Filter");

        bSelect.setText("Select");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(autoResize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bSort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bSort)
                    .addComponent(bExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(autoResize)
                    .addComponent(filterLabel)
                    .addComponent(jLabel2)
                    .addComponent(bFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox autoResize;
    scri.commons.gui.matisse.MenuButton bExport;
    scri.commons.gui.matisse.MenuButton bFilter;
    scri.commons.gui.matisse.MenuButton bSelect;
    javax.swing.JButton bSort;
    javax.swing.JLabel filterLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}