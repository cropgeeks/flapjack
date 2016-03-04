// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.traits;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class TraitsPanelNB extends JPanel
{
	TraitsPanelNB()
	{
		initComponents();

		RB.setText(checkColor, "gui.traits.NBTraitsControlPanel.checkColor");
		RB.setText(bImport, "gui.traits.NBTraitsControlPanel.bImport");
		RB.setText(bExport, "gui.traits.NBTraitsControlPanel.bExport");
		RB.setText(bRemove, "gui.traits.NBTraitsControlPanel.bRemove");
		RB.setText(bColors, "gui.traits.NBTraitsControlPanel.bColors");

		bImport.setIcon(Icons.getIcon("IMPORTTRAITS"));
		bExport.setIcon(Icons.getIcon("EXPORTTRAITS"));
		bRemove.setIcon(Icons.getIcon("DELETE"));
		bColors.setIcon(Icons.getIcon("VISUALIZATIONTAB"));

		checkColor.setSelected(Prefs.guiColorTraitTable);
		checkColor.addActionListener(e -> {
			Prefs.guiColorTraitTable = checkColor.isSelected();
			Flapjack.winMain.repaint();
		});
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

        bImport = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        bRemove = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = createTable();
        bExport = new javax.swing.JButton();
        checkColor = new javax.swing.JCheckBox();
        bColors = new javax.swing.JButton();

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

        checkColor.setText("Colour the table cells using heat map values");
        checkColor.setOpaque(false);

        bColors.setText("Colors");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkColor)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bColors)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bRemove)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkColor)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bRemove)
                    .addComponent(statusLabel)
                    .addComponent(bImport)
                    .addComponent(bExport)
                    .addComponent(bColors))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bColors;
    javax.swing.JButton bExport;
    javax.swing.JButton bImport;
    javax.swing.JButton bRemove;
    private javax.swing.JCheckBox checkColor;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JLabel statusLabel;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}