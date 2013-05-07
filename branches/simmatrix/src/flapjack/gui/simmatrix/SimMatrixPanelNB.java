// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.simmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

class SimMatrixPanelNB extends JPanel implements ActionListener
{
	private GTViewSet viewSet;

	public SimMatrixPanelNB(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		initComponents();

		bDendrogram.addActionListener(this);
	}

	private JTable createTable()
	{
		return new JTable()
		{
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = SimMatrixTableModel.getCellRenderer();
				return (tcr != null) ? tcr : super.getCellRenderer(row, col);
			}

//			public String getToolTipText(MouseEvent e)
//			{
//				return panel.getTableToolTip(e);
	//		}
		};
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bDendrogram)
		{
			Flapjack.winMain.mData.dataDendrogram(viewSet);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = createTable();
        bDendrogram = new javax.swing.JButton();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(table);

        bDendrogram.setText("Dendrogram...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bDendrogram)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bDendrogram)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDendrogram;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}