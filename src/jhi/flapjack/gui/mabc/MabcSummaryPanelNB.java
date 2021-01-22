// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

class MabcSummaryPanelNB extends JPanel
{
	private MabcSummaryTableModel model;

	MabcSummaryPanelNB(MabcPanel panel, GTViewSet viewSet)
	{
		// Safety check for any results that were generated before we added
		// summary information. This'll create a new list of size one.
		if (viewSet.getMabcBatchList() == null)
		{
			MabcBatchList list = new MabcBatchList();
			list.add(viewSet);
		}

		initComponents();

		bExport.setPopup(((SummaryTable)table).getExportMenu());
		bExport.setIcon(Icons.getIcon("EXPORTTRAITS"));

		autoResize.addActionListener(panel);

		/////////////

		model = new MabcSummaryTableModel(viewSet.getMabcBatchList());
		table.setModel(model);
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
        table = new SummaryTable();
        bExport = new scri.commons.gui.matisse.MenuButton();
        autoResize = new javax.swing.JCheckBox();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {

            }
        ));
        jScrollPane1.setViewportView(table);

        bExport.setText("Export");
        bExport.setActionCommand("Export...");

        autoResize.setSelected(true);
        autoResize.setText("Auto-fit columns");
        autoResize.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(autoResize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoResize))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox autoResize;
    scri.commons.gui.matisse.MenuButton bExport;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}