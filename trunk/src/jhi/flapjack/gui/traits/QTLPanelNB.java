// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.traits;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import scri.commons.gui.*;

class QTLPanelNB extends javax.swing.JPanel
{
	QTLPanelNB()
	{
		initComponents();

		errorLabel.setText("<html>" + RB.getString("gui.traits.QTLPanel.errorMsg"));
		errorLabel.setForeground(Color.red);

		statusLabel.setText(RB.format("gui.traits.QTLPanel.traitCount", 0));

		RB.setText(bImport, "gui.traits.NBQTLControlPanel.bImport");
		RB.setText(bExport, "gui.traits.NBQTLControlPanel.bExport");
		RB.setText(bRemove, "gui.traits.NBQTLControlPanel.bRemove");
		RB.setText(bFilter, "gui.traits.NBQTLControlPanel.bFilter");

		bImport.setIcon(Icons.getIcon("IMPORTTRAITS"));
		bExport.setIcon(Icons.getIcon("EXPORTTRAITS"));
		bRemove.setIcon(Icons.getIcon("DELETE"));
		bFilter.setIcon(Icons.getIcon("TRAITS"));
	}

	private JTable createTable()
	{
		return new JTable()	{
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel)
				{
					public String getToolTipText(MouseEvent e)
					{
						Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						if (index >= 0 && index < columnModel.getColumnCount())
						{
							int realIndex = columnModel.getColumn(index).getModelIndex();
							return getModel().getColumnName(realIndex);
						}
						else
							return null;
					}
				};
		}};
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        errorLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = createTable();
        statusLabel = new javax.swing.JLabel();
        bImport = new javax.swing.JButton();
        bExport = new javax.swing.JButton();
        bFilter = new javax.swing.JButton();
        bRemove = new javax.swing.JButton();

        errorLabel.setText("jLabel1");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        statusLabel.setText("Number of QTLs: 0");

        bImport.setText("Import");

        bExport.setText("Export");

        bFilter.setText("Filter");

        bRemove.setText("Clear");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                        .addComponent(bImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bRemove))
                    .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(bRemove)
                    .addComponent(bExport)
                    .addComponent(bFilter)
                    .addComponent(bImport))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bExport;
    javax.swing.JButton bFilter;
    javax.swing.JButton bImport;
    javax.swing.JButton bRemove;
    javax.swing.JLabel errorLabel;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JLabel statusLabel;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}